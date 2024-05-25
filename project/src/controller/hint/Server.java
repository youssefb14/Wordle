package controller.hint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.OptionalInt;

/**
 * This class starts and close the server.
 * While started, this class allows to make request to the HTTP server.
 * @author Axel Bertrand
 */
public class Server {
	// Process running the HTTP python server
    private static Process PROCESS;
	// Address of the HTTP server
    private static String ADDRESS;
	// Start of error messages from python server, used to detect and throw errors
	private static final String ERROR_MESSAGE_START = "Error: ";
	// Status of server
	private static ServerStatus STATUS = ServerStatus.CLOSED;
	// Index of current hint in hint array
	private static int HINT_INDEX = 0;
	// Number of hints per request
	private static int REQUEST_SIZE = 0;
	// Maximum number of hints
	private static OptionalInt HINT_LIMIT = OptionalInt.empty();
	// Current word to guess
	private static String TARGET_WORD;
	// Array to store hints from request
	private static ArrayList<GensimPair> ARRAY = new ArrayList<>();
	// Hold server starting thread to wait for it to terminate if needed
	private static Thread startThread;

	// Current status of server (STARTING is when start thread is running and not terminated)
	public enum ServerStatus {
		CLOSED, STARTING, STARTED
	}

	/**
	 * Start the HTTP server while setting the address value
	 * @throws IOException if an I/O error occurs when starting process or when reading initial process output to get address
	 */
    public static void start() throws IOException {
        ProcessBuilder processBuilder =  new ProcessBuilder("flask", "--app", pythonPath("server.py"), "run");
        processBuilder.redirectErrorStream(true);
        PROCESS = processBuilder.start();

		try (BufferedReader br = PROCESS.inputReader()) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(" * Running on")) {
					ADDRESS = line.substring(14);
					break;
				}
			}
		}

		STATUS = ServerStatus.STARTED;
    }

	/**
	 * Start the HTTP server, does not wait for it to be open to return
	 */
	public static void async_start() {
		Thread thread = new Thread(() -> {
			try {
				start();
			}
			catch (IOException e) {
				STATUS = ServerStatus.CLOSED;
			}
		});
		startThread = thread;
		STATUS = ServerStatus.STARTING;
		thread.start();
	}

	/**
	 * @return String of HTTP server address formatted like "http://127.0.0.1:5000"
	 */
    public static String getAddress() {
        return ADDRESS;
    }

	/**
	 * @return Status of HTTP server
	 */
	public static boolean isStarted() {
		return STATUS == ServerStatus.STARTED;
	}

	/**
	 * @return number of hints requested for current target word
	 */
	public static int getHintCount() {
		return HINT_INDEX;
	}

	/**
	 * Close server and reset address
	 */
    public static void close() {
		STATUS = ServerStatus.CLOSED;
		ADDRESS = null;
		resetRequest();
        if (PROCESS != null) {
            PROCESS.destroy();
        }
    }

	/**
	 * Get the text content from an url
	 * @param url from which text content should be extracted
	 * @return String of content from url
	 * @throws IOException if I/O error occurs while opening connection or reading content
	 */
    private static String getContent(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}

		in.close();
		con.disconnect();

		return content.toString();
	}

	/**
	 * Returns similarity between two words from the gensim model
	 * @param word1 first word to compare
	 * @param word2 second word to compare
	 * @return value of similarity between the two words
	 * @throws GensimException if an error occurs from python script due to inputs
	 * @throws ServerClosedException if server is closed
	 * @throws java.net.MalformedURLException if an error occurs while constructing URL
	 * @throws NumberFormatException if content from request cannot be converted to float
	 */
	public static Float getSimilarity(String word1, String word2) throws Exception {
		statusCheck();
		word1 = word1.toLowerCase();
		word2 = word2.toLowerCase();

		String content = getContent(URI.create(Server.getAddress() + "/diff?word1=" + word1.toLowerCase() + "&word2=" + word2.toLowerCase()).toURL());
		if (isContentErrored(content)) {
			throw getError(content);
		}
		
		return Float.valueOf(content);
	}

	/**
	 * normalize a word by using NFKD form to remove accents and by lowering case
	 * @param word string to normalize
	 * @return normalized string of input word
	 */
	private static String normalize(String word) {
		return Normalizer.normalize(word, Normalizer.Form.NFKD).replaceAll("\\p{M}", "").toLowerCase();
	}

	/**
	 * Returns the n most similar words of input word
	 * @param word input word
	 * @param n number of words to receive (must be greater than 0)
	 * @return ArrayList of GensimPair (holding the word and the similarity from input word)
	 * @throws GensimException if an error occurs from python script due to inputs
	 * @throws ServerClosedException if server is closed
	 * @throws java.net.MalformedURLException if an error occurs while constructing URL
	 * @throws NumberFormatException if content from request cannot be converted to float
	 */
	public static ArrayList<GensimPair> getMostSimilar(String word, int n) throws Exception {
		statusCheck();
		word = word.toLowerCase();

		URL url = URI.create(Server.getAddress() + "/sim?word=" + word.toLowerCase() + "&n=" + n).toURL();
		String raw_content = getContent(url);
		if (isContentErrored(raw_content)) {
			throw getError(raw_content);
		}

		String[] contents = raw_content.replaceAll("([)(' \\[\\]])", "").split(",");
		ArrayList<GensimPair> array = new ArrayList<>(contents.length);
		for (int i = 0; i < contents.length; i++) {
			String key = contents[i];

			// remove too similar words (example : plural words like "chien" and "chiens")
			String normalizedTarget = normalize(word);
			String normalizedKey = normalize(key);
			if (normalizedTarget.contains(normalizedKey) || normalizedKey.contains(normalizedTarget)) {
				REQUEST_SIZE--;
				i++;
				continue;
			}
			float number = Float.parseFloat(contents[++i]);
			array.add(new GensimPair(key, number));
		}

		return array;
	}

	/**
	 * @param word target word to guess and get hints for
	 * @return next GensimPair (key and similarity) from request
	 * @throws GensimException if an error occurs from python script due to inputs
	 * @throws ServerClosedException if server is closed
	 * @throws java.net.MalformedURLException if an error occurs while constructing URL
	 * @throws NumberFormatException if content from request cannot be converted to float
	 */
	public static GensimPair getHint(String word) throws Exception {
		System.out.println("Server::getHint debug : targetWord="+word);
		// wait if starting
		waitServerStarting();
		// check if not open, throw exception if it is
		statusCheck();

		if (TARGET_WORD == null || !TARGET_WORD.equals(word)) {
			TARGET_WORD = word;
			resetRequest();
		}

		OptionalInt limit = getHintLimit();
		if (limit.isPresent() && HINT_INDEX >= limit.getAsInt()) {
			throw new HintLimitException("La limite de "+limit.getAsInt()+" indices a été dépassée");
		}

		if (HINT_INDEX >= REQUEST_SIZE) {
			REQUEST_SIZE += 10;
			ARRAY = getMostSimilar(TARGET_WORD, REQUEST_SIZE);
		}

		return ARRAY.get(HINT_INDEX++);
	}

	/**
	 * wait until server start thread has terminated
	 * @throws InterruptedException if current thread is interrupted
	 */
	public static void waitServerStarting() throws InterruptedException {
		if (STATUS == ServerStatus.STARTING) {
			// wait for starting thread to terminate
			startThread.join();
		}
	}

	/**
	 * check if server is open, otherwise throws exception
	 * @throws ServerClosedException if server is not open
	 */
	private static void statusCheck() throws ServerClosedException {
		if (!isStarted()) {
			throw new ServerClosedException("Le serveur n'est pas ouvert");
		}
	}

	/**
	 * reset static fields from request, is called when target word change
	 */
	private static void resetRequest() {
		HINT_INDEX = 0;
		REQUEST_SIZE = 0;
	}

	/**
	 * change the number of times getHint() can be called on a same word
	 * @param limit maximum number of hints, can be null to set to no hint limit
	 */
	public static void setHintLimit(Integer limit) {
		if (limit == null) {
			HINT_LIMIT = OptionalInt.empty();
		} else {
			HINT_LIMIT = OptionalInt.of(limit);
		}
	}

	/**
	 * @return empty optional if there is no limit, otherwise an optional of the limit
	 */
	public static OptionalInt getHintLimit() {
		return HINT_LIMIT;
	}

	/**
	 * @param content result of HTTP request to search errors in
	 * @return true if there is an error, false otherwise
	 */
	private static boolean isContentErrored(String content) {
		return content.startsWith(ERROR_MESSAGE_START);
	}

	/**
	 * @param raw_content content of HTTP request
	 * @return appropriate GensimException from error message
	 */
	private static Exception getError(String raw_content) {
		String errorMessage = getErrorMessage(raw_content);
		if (errorMessage.contains("key")) {
			return new KeyNotPresentException(errorMessage);
		} else {
			return new InvalidNumberOfWordsException(errorMessage);
		}
	}

	/**
	 * @param errorMessage message from HTTP request
	 * @return extracted error message
	 */
	private static String getErrorMessage(String errorMessage) {
		return errorMessage.substring(ERROR_MESSAGE_START.length());
	}

	/**
	 * @param file python server file
	 * @return path to server file from project
	 */
    private static String pythonPath(String file){
		Path path = Paths.get(System.getProperty("user.dir"), "src", "resources", file);
		return path.toString();
	}
}
