package online;

import java.io.BufferedReader; // Pour lire les données en entrée
import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie
import java.io.InputStreamReader; // Pour lire les données en entrée
import java.io.PrintWriter; // Pour écrire les données en sortie 
import java.net.Socket; // Pour créer un socket
import javafx.application.Platform; // Pour exécuter des tâches sur le thread JavaFX

public class Client {
    private String serverAddress; // Attribut pour l'adresse du serveur
    private int serverPort; // Attribut pour le port du serveur
    private Socket socket;  // Attribut pour le socket
    private PrintWriter out;    // Attribut pour écrire les données en sortie
    private BufferedReader in;  // Attribut pour lire les données en entrée
    private boolean running = true; // Attribut pour indiquer si le client est en cours d'exécution
    private ServerMessageListener listener; // Attribut pour écouter les messages du serveur
    private String playerName;  // Attribut pour le nom du joueur
    private int numberOfPlayers;    // Attribut pour le nombre de joueurs
    private HumanPlayer player; // Player associated to this client
    
    private Server server;

    public Client(String serverAddress, int serverPort, String playerName) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.playerName = playerName;
        this.player = new HumanPlayer(playerName);
    }
    
    public Client(String serverAddress, int serverPort, String playerName, Server server) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.playerName = playerName;
        this.player = new HumanPlayer(playerName);
        this.server = server;
    }

    public void setServerMessageListener(ServerMessageListener listener) {
        this.listener = listener;   // On définit l'écouteur pour les messages du serveur
    }

    public void connect() {
        try {
            socket = new Socket(serverAddress, serverPort); // On crée un socket avec l'adresse et le port du serveur
            out = new PrintWriter(socket.getOutputStream(), true);  // On crée un flux de sortie pour écrire les données
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // On crée un flux d'entrée pour lire les données
            listenForServerMessages(); // On écoute les messages du serveur
            //System.out.println("Client - connect(): Connexion réussie au serveur");

            sendMessage("SEND_NAME:" + playerName); // On envoie le nom du joueur au serveur
            listenForServerMessages();  // On écoute les messages du serveur
        } catch (IOException e) {   // On gère les exceptions d'entrée/sortie
            System.out.println("Client - connect(): Erreur lors de la connexion au serveur: " + e.getMessage());    // On affiche un message d'erreur
        }
    }

    public void joinLobby(String lobbyName) {
        sendMessage("JOIN_LOBBY " + lobbyName); // On envoie un message pour rejoindre un lobby
    }

    public void listenForServerMessages() {
    	new Thread(() -> { // Création d'un nouveau thread pour écouter les messages du serveur
    	    try { // On gère les exceptions d'entrée/sortie
    	        String message; // On crée une variable pour stocker le message
    	        while ((message = in.readLine()) != null) { // On lit les messages du serveur
    	            String finalMessage = message; // On stocke le message dans une variable finale
    	            if (listener != null) { // Si l'écouteur n'est pas nul
    	                Platform.runLater(() -> listener.onServerMessage(finalMessage));    // On exécute l'écouteur sur le thread JavaFX
    	            }
    	        }
    	    } catch (IOException e) {  // On gère les exceptions d'entrée/sortie
    	        e.printStackTrace(); // On affiche un message d'erreur
    	    }
    	}).start(); // On démarre le thread
    }

    public void sendMessage(String message) {
        if (out != null) { // Si le flux de sortie n'est pas nul
            out.println(message);  // On envoie le message
        }
    }

    public String readMessage() {
        try { // On gère les exceptions d'entrée/sortie
            if (in != null) { // Si le flux d'entrée n'est pas nul
                String msg = in.readLine(); // On lit le message
                //System.out.println("Client - readMessage(): Message reçu: " + msg);
                return msg; // On retourne le message
            }
        } catch (IOException e) {  // On gère les exceptions d'entrée/sortie
            System.out.println(" Client - readMessage(): Erreur lors de la lecture du message: " + e.getMessage()); // On affiche un message d'erreur
        }
        return null;
    }

    public void sendChatMessage(String message) {
        sendMessage("CHAT: " + message); // On envoie un message de chat
    }

    public void listenForMessages() {
        new Thread(() -> { // Création d'un nouveau thread pour écouter les messages
            String msgFromServer; // On crée une variable pour stocker le message du serveur
            try {   // On gère les exceptions d'entrée/sortie
                while (running && (msgFromServer = in.readLine()) != null) { // On lit les messages du serveur
                    System.out.println("Server says: " + msgFromServer); // On affiche le message du serveur
                } // On continue tant que le client est en cours d'exécution et qu'il y a des messages
            } catch (IOException e) {  // On gère les exceptions d'entrée/sortie
                System.out.println("Error reading from server: " + e.getMessage()); // On affiche un message d'erreur
            }
        }).start(); // On démarre le thread
    }

    public void stopConnection() {
        running = false;   // On arrête la connexion en mettant le booléen à faux
        try {  // On gère les exceptions d'entrée/sortie
            if (in != null) { // Si le flux d'entrée n'est pas nul
				in.close(); // On ferme le flux d'entrée
			}
            if (out != null) { // Si le flux de sortie n'est pas nul
				out.close(); // On ferme le flux de sortie
			}
            if (socket != null) { // Si le socket n'est pas nul
				socket.close(); // On ferme le socket
			}
        } catch (IOException e) { // On gère les exceptions d'entrée/sortie
            System.out.println("Error closing the connection: " + e.getMessage()); // On affiche un message d'erreur
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void updateGameInfo(GameInfo gameInfo) {
        this.numberOfPlayers = gameInfo.getNumberOfPlayers(); // On met à jour le nombre de joueurs
    }

    public Server getServer() {
        return server;
    }

	public HumanPlayer getPlayer() {
		return player;
	}

	public void setPlayer(HumanPlayer player) {
		this.player = player;
	}

}