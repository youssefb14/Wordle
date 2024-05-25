package online;

import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie
import java.net.ServerSocket; // Pour créer un serveur socket
import java.net.Socket; // Pour créer un socket
import java.util.ArrayList; // Pour utiliser la classe ArrayList
import java.util.Arrays;
import java.util.HashMap; // Pour utiliser la classe HashMap
import java.util.HashSet; // Pour utiliser la classe HashSet
import java.util.List; // Pour utiliser la classe List
import java.util.Map; // Pour utiliser la classe Map
import java.util.Set; // Pour utiliser la classe Set
import java.util.concurrent.ExecutorService; // Pour exécuter des tâches en arrière-plan
import java.util.concurrent.Executors; // Pour exécuter des tâches en arrière-plan 
import java.util.stream.Collectors; // Pour utiliser les streams

public class Server {
    private ServerSocket serverSocket; // Attribut pour le serveur socket
    private Lobby lobby;   // Attribut pour le lobby
    private Set<ClientHandler> clients = new HashSet<>(); // Attribut pour les clients
    private Map<String, ClientHandler> clientHandlersByName = new HashMap<>(); // Attribut pour les clients par nom
    private ExecutorService clientThreadExecutor; // Attribut pour exécuter des tâches en arrière-plan
    private boolean isServerStarted = false; // Attribut pour indiquer si le serveur est démarré

    public Server(int port, int maxPlayersPerSession, int gameDuration, int difficultyLevel) {
        try { // On essaie
            serverSocket = new ServerSocket(port); // On crée un serveur socket avec le port
            clientThreadExecutor = Executors.newCachedThreadPool(); // On crée un pool de threads pour exécuter des tâches en arrière-plan
            //System.out.println("Server lancé avec succès. a l'adresse: " + port);
            this.lobby = new Lobby(port, maxPlayersPerSession, gameDuration, difficultyLevel, this);  // Pass 'this' as a reference to Server
            isServerStarted = true; // On indique que le serveur est démarré
        } catch (IOException e) {
            System.out.println("Error creating server: " + e.getMessage()); // On affiche un message d'erreur
            e.printStackTrace(); // On affiche les erreurs
        }
    }

    public void startServer() {
    	if (!isServerStarted) { // Si le serveur n'est pas démarré
            System.out.println("Le serveur n'a pas démarré correctement, arrêt de startServer car deja demarré");
            return; // On arrête
        }
        try { // On essaie
            while (!serverSocket.isClosed()) { // Tant que le serveur socket n'est pas fermé
                Socket socket = serverSocket.accept(); // On accepte le socket
                ClientHandler clientHandler = new ClientHandler(socket, this, lobby); // On crée un gestionnaire de client avec le socket et le serveur
                clients.add(clientHandler); // On ajoute le gestionnaire de client à la liste des clients
                clientThreadExecutor.execute(clientHandler); // On exécute le gestionnaire de client
                Thread thread = new Thread(clientHandler); // On crée un thread avec le gestionnaire de client
                thread.start(); // On démarre le thread

                String lobbyStatus = lobby.getLobbyStatus(); // On récupère l'état du lobby
                clientHandler.sendMessage("Bienvenue! " + lobbyStatus); // On envoie un message de bienvenue avec l'état du lobby

                lobby.addClient(clientHandler); // On ajoute le client au lobby
 
                GameInfo gameInfo = lobby.getGameInfo();
                String gameInfoMessage = "GAME_INFO:" + gameInfo.toString();
                clientHandler.sendMessage(gameInfoMessage);
 
                updateLobbyStateForAllClients(); // On met à jour l'état du lobby pour tous les clients
            }
        } catch (IOException e) { // On gère les exceptions d'entrée/sortie
            closeServerSocket(); // On ferme le serveur socket
        }finally { // Enfin
            if (clientThreadExecutor != null) { // Si le pool de threads n'est pas nul
                clientThreadExecutor.shutdown(); // On arrête le pool de threads
            }
        }
    }

   public synchronized void configureGame(String configuration) {
        String[] tokens = configuration.split("="); // On divise la configuration
        if (tokens[0].trim().equals("SET MAX_PLAYERS")) { // Si le premier token est égal à "SET MAX_PLAYERS" donc on a une configuration pour le nombre maximum de joueurs
            int maxPlayers = Integer.parseInt(tokens[1].trim()); // On récupère le nombre maximum de joueurs
            System.out.println("Max players set to: " + maxPlayers); // On affiche le nombre maximum de joueurs
		} 
        else if (tokens[0].trim().equals("SET GAME_DURATION")) { // Si le premier token est égal à "SET GAME_DURATION" donc on a une configuration pour la durée du jeu
			int gameDuration = Integer.parseInt(tokens[1].trim()); // On récupère la durée du jeu
			System.out.println("Game duration set to: " + gameDuration); // On affiche la durée du jeu
		} 
        else if (tokens[0].trim().equals("SET DIFFICULTY")) { // Si le premier token est égal à "SET DIFFICULTY" donc on a une configuration pour le niveau de difficulté
			int difficulty = Integer.parseInt(tokens[1].trim()); // On récupère le niveau de difficulté
			System.out.println("Difficulty set to: " + difficulty);  // On affiche le niveau de difficulté
		} 
        else { // Sinon
			System.out.println("Invalid configuration command."); // On affiche un message d'erreur
		}

        String[] settings = configuration.split(";"); // On divise la configuration en utilisant le point-virgule
        for (String setting : settings) { // Pour chaque configuration
            String[] keyValue = setting.split("="); // On divise la configuration en utilisant le signe égal
            switch (keyValue[0]) {  // On vérifie le premier token
                case "difficulty": // Si c'est "difficulty"
                    lobby.setDifficulty(keyValue[1]); // On définit la difficulté
                    break; // On arrête
                case "duration": // Si c'est "duration"
                    lobby.setGameDuration(Integer.parseInt(keyValue[1])); // On définit la durée du jeu
                    break;
                case "words": // Si c'est "words"
                    lobby.setNumberOfWords(Integer.parseInt(keyValue[1])); // On définit le nombre de mots
                    break;
                default: // Sinon
                    System.out.println("Invalid configuration command."); // On affiche un message d'erreur
                    break; // On arrête
            }
        }
    }

   public synchronized void updateLobbyStateForAllClients() {
	    String playerListUpdate = "PLAYER_LIST_UPDATE:" + clients.stream() // On crée un message avec la liste des joueurs
	        .map(ClientHandler::getPlayerName) // On récupère le nom du joueur
	        .collect(Collectors.joining(",")); // On joint les noms des joueurs
	    broadcastMessage(playerListUpdate); // On envoie le message à tous les clients
	}

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler); // On supprime le client
        clientHandlersByName.values().remove(clientHandler); // On supprime le client par nom
        lobby.removeClient(clientHandler); // On supprime le client du lobby
        updateLobbyStateForAllClients(); // On met à jour l'état du lobby pour tous les clients
    }

    public void closeServerSocket() {
        try { // On essaie
            if (serverSocket != null) { // Si le serveur socket n'est pas nul
                serverSocket.close(); // On ferme le serveur socket
            }
        } catch (IOException e) { // On gère les exceptions d'entrée/sortie
            e.printStackTrace(); // On affiche les erreurs
        }
    }

    public void handleClientReconnection(String playerName, Socket socket) {
        ClientHandler clientHandler = clientHandlersByName.get(playerName);
        if (clientHandler != null) { // Client already exists
            clientHandler.handleReconnection(socket); // Use existing handler
        } else {
            // New handler for new or returning player
            clientHandler = new ClientHandler(socket, this);
            clientHandlersByName.put(playerName, clientHandler);
            clients.add(clientHandler);
            lobby.addClient(clientHandler);
        }
        lobby.notifyAllClients(playerName + " s'est reconnecté.");
    }

    public synchronized void addClientName(String playerName, ClientHandler clientHandler) {
        if (clientHandlersByName.containsKey(playerName)) {
            clientHandler.sendMessage("ERROR:NameTaken");
            System.out.println("Nom du joueur déjà utilisé : " + playerName);
        } else if (lobby.isFull()) {
            clientHandler.sendMessage("ERROR:LobbyFull");
            System.out.println("Tentative de connexion à un lobby plein par " + playerName);
        } else {
            clientHandlersByName.put(playerName, clientHandler);
            clients.add(clientHandler);
            lobby.addClient(clientHandler);
            updateLobbyStateForAllClients(); // Déjà existant
            sendGameInfoUpdate(); // Assurez-vous que cette ligne est présente pour mettre à jour les infos du jeu
        }
    }

    public void relayChatMessage(String message) {
        for (ClientHandler client : clients) { // Pour chaque client
            client.sendMessage("CHAT:" + message); // On envoie un message de chat
        }
    }

    public synchronized void startGame() {
    	if (lobby.canStartGame()) { // Si le lobby peut démarrer le jeu
            lobby.startGameForAllSessions(); // On démarre le jeu pour toutes les sessions
            broadcastMessage("GAME_START"); // On envoie un message de démarrage du jeu
        }
        
        for (ClientHandler client : clients) { // Pour chaque client
            client.sendMessage("GAME_START"); // On envoie un message de démarrage du jeu
        }
        
        lobby.startNewGameSession(new GameConfig(4, 30, 1)); // On démarre une nouvelle session de jeu
    }

    public synchronized void createNewGameSession(GameConfig config) {
        GameSession newSession = new GameSession(new ArrayList<>(), config); // On crée une nouvelle session de jeu
        lobby.addSession(newSession);  // On ajoute la session au lobby
        //System.out.println("Nouvelle session de jeu créée avec la configuration : " + config);
    }

    public synchronized void addClientToSession(ClientHandler clientHandler, int sessionId) {
    	List<GameSession> activeSessions = lobby.getActiveSessions();  // On récupère les sessions actives
    	GameSession session = activeSessions.get(sessionId);  // On récupère la session
        if (session != null && !session.isFull()) { // Si la session n'est pas nulle et n'est pas pleine
            session.addClient(clientHandler); // On ajoute le client à la session
            //System.out.println("Joueur ajouté à la session : " + sessionId);
        } else { // Sinon
            System.out.println("La session est pleine ou n'existe pas.");
            // TODO : Envoyer un message d'erreur au client
        }
    }

    public synchronized void startGameSession(int sessionId) {
    	List<GameSession> activeSessions = lobby.getActiveSessions(); // On récupère les sessions actives
        GameSession session = activeSessions.get(sessionId); // On récupère la session
        if (session != null) { // Si la session n'est pas nulle
            session.startGame(); // On démarre le jeu
            //System.out.println("La partie commence pour la session : " + sessionId);
        } else { // Sinon
            System.out.println("Session introuvable."); // On affiche un message d'erreur
        }
    }

	public synchronized void removeGameSession(int sessionId) { 
		List<GameSession> activeSessions = lobby.getActiveSessions(); // On récupère les sessions actives
		GameSession session = activeSessions.get(sessionId);  // On récupère la session
		if (session != null) { // Si la session n'est pas nulle
			lobby.removeSession(session); // On supprime la session
			//System.out.println("Session supprimée : " + sessionId);
		} else { // Sinon
			System.out.println("Session introuvable."); // On affiche un message d'erreur
            // TODO : Envoyer un message d'erreur au client
		}
	}

	public Lobby getLobby() {
        return this.lobby; // On retourne le lobby
    }

	public synchronized void handleConfigurationChange(String configDetails) {
	    Map<String, String> configMap = Arrays.stream(configDetails.replace("CONFIG_UPDATE:", "").split(";"))
	        .filter(s -> s.contains("="))
	        .map(s -> s.split("="))
	        .filter(a -> a.length == 2)
	        .collect(Collectors.toMap(a -> a[0], a -> a[1]));

	    try {
	        int newMaxPlayers = Integer.parseInt(configMap.getOrDefault("maxPlayers", String.valueOf(lobby.getMaxPlayersPerSession())));
	        int newDifficultyLevel = mapDifficultyToLevel(configMap.getOrDefault("difficulty", "Facile"));
	        int newDuration = Integer.parseInt(configMap.getOrDefault("duration", "30"));
	        int newWords = Integer.parseInt(configMap.getOrDefault("words", "5"));

	        lobby.updateLobbyConfig(newMaxPlayers, newDuration, newDifficultyLevel, newWords);
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid number format in settings update");
	    }
	    broadcastConfigurationUpdate(); // Assurez-vous d'appeler ceci pour envoyer la mise à jour à tous les clients
	}
	
	public synchronized void updateLobbyConfig(int maxPlayers, int gameDuration, int difficultyLevel, int numberOfWords) {
	    lobby.updateGameSettings(difficultyLevel, gameDuration, numberOfWords, maxPlayers);
	    broadcastConfigurationUpdate(); // Mise à jour et diffusion des nouvelles configurations
	}
	
	public synchronized void broadcastConfigurationUpdate() {
	    GameInfo gameInfo = lobby.getGameInfo();
	    String configUpdateMessage = "CONFIG_UPDATE:" + gameInfo.toString();
	    broadcastMessage(configUpdateMessage);
	}

	private int mapDifficultyToLevel(String difficulty) {
	    switch (difficulty.toLowerCase()) {
	        case "facile": return 1;
	        case "moyen": return 2;
	        case "difficile": return 3;
	        default: return 1; // Default to "Moyen" if not matched
	    }
	}
		
	public synchronized void updateGameConfiguration(String difficulty, String duration, String words) {
	    boolean configChanged = false;

	    if (!lobby.getGameInfo().getDifficulty().equals(difficulty)) {
	        lobby.getGameInfo().setDifficulty(difficulty);
	        configChanged = true;
	    }

	    try {
	        int durationInt = Integer.parseInt(duration);
	        if (lobby.getGameInfo().getGameDuration() != durationInt) {
	            lobby.getGameInfo().setGameDuration(durationInt);
	            configChanged = true;
	        }
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid number format for game duration");
	    }

	    try {
	        int wordsInt = Integer.parseInt(words);
	        if (lobby.getGameInfo().getNumberOfWords() != wordsInt) {
	            lobby.getGameInfo().setNumberOfWords(wordsInt);
	            configChanged = true;
	        }
	    } catch (NumberFormatException e) {
	        System.out.println("Invalid number format for number of words");
	    }

	    if (configChanged) {
	        sendGameInfoUpdate();
	    }
	}
	
	public synchronized void sendGameInfoUpdate() {
	    String gameInfoMessage = "GAME_INFO:" + lobby.getGameInfo().toString();
	    broadcastMessage(gameInfoMessage);
	}
	
	public synchronized void broadcastMessage(String message) {
	    for (ClientHandler client : clients) {
	        client.sendMessage(message);
	    }
	}
	
	public Set<ClientHandler> getClients() {
	    return this.clients;
	}
	
}