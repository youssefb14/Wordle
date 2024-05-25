package online;

import java.util.ArrayList; // Pour utiliser ArrayList
import java.util.List; // Pour utiliser les listes
import java.util.Objects; // Pour utiliser Objects
import java.util.stream.Collectors; // Pour utiliser Collectors

import controller.WordleGame;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import localView.GamePortal;
import localView.WordleView;

public class Lobby {
	private final List<ClientHandler> waitingClients; // Liste des clients en attente
    private final List<GameSession> activeSessions;   // Liste des sessions actives
    private int maxPlayersPerSession;           // Nombre maximum de joueurs par session
    private final GameConfig gameConfig;              // Configuration du jeu 
    private ClientHandler host;                       // Hôte du lobby
    private final int serverPort;                     // Port du serveur
    private Server server;                            // Serveur

    public Lobby(int serverPort, int maxPlayersPerSession, int gameDuration, int difficultyLevel, Server server) {
        this.server = server;
        this.serverPort = serverPort; // On initialise le port du serveur
        this.waitingClients = new ArrayList<>(); // On initialise la liste des clients en attente
        this.activeSessions = new ArrayList<>(); // On initialise la liste des sessions actives
        this.maxPlayersPerSession = maxPlayersPerSession; // On initialise le nombre maximum de joueurs par session
        this.gameConfig = new GameConfig(maxPlayersPerSession, gameDuration, difficultyLevel); // On initialise la configuration du jeu
        this.host = null; // On initialise l'hôte du lobby à null
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        if (isFull()) { // Si le lobby est plein
            clientHandler.sendMessage("ERROR:LobbyFull"); // On envoie un message d'erreur au client
            return; // On sort de la méthode
        }
        if (waitingClients.isEmpty() && host == null) { // Si la liste des clients en attente est vide et aucun hôte n'est défini
            host = clientHandler; // On définit le client comme hôte
            host.setPlayer(new HumanPlayer(clientHandler.getPlayerName()));
        }
        waitingClients.add(clientHandler); // On ajoute le client à la liste des clients en attente
        updateLobbyStateForAllClients(); // On met à jour l'état du lobby pour tous les clients
        if ((waitingClients.size() >= maxPlayersPerSession) && (activeSessions.size() < 4)) {
            createNewGameSession(); // On crée une nouvelle session de jeu
        }
    }

    public boolean canStartGame() {
        return waitingClients.size() >= maxPlayersPerSession; // On retourne vrai si le nombre de clients en attente est supérieur ou égal au nombre maximum de joueurs par session
    }

    public void createNewGameSession() {
    	if (waitingClients.size() < maxPlayersPerSession) { // Si le nombre de clients en attente est inférieur au nombre maximum de joueurs par session
            System.out.println("Pas assez de joueurs pour démarrer une nouvelle session."); // On affiche un message
            return; // On sort de la méthode
        }

        List<ClientHandler> playersForNewSession = new ArrayList<>(waitingClients.subList(0, maxPlayersPerSession)); // On crée une liste de clients pour la nouvelle session
        GameSession newSession = new GameSession(playersForNewSession, gameConfig); // On crée une nouvelle session de jeu
        activeSessions.add(newSession); // On ajoute la nouvelle session à la liste des sessions actives
        waitingClients.removeAll(playersForNewSession); // On supprime les clients de la liste des clients en attente
    } 

    public void closeAllSessions() {
        for (GameSession session : activeSessions) { // Pour chaque session active
            session.closeSession(); // On ferme la session
        }
        activeSessions.clear(); // On vide la liste des sessions actives
    }
 
    public void startGameForAllSessions() {
    	boolean isAllReady = true;
    	WordleGame game = GamePortal.getStaticGame();
    	for (GameSession session : activeSessions) {
    		for (ClientHandler clientHandler : session.getPlayers()) {
    			if (clientHandler.getPlayer().isReady() == false) isAllReady = false;
    		}
    	  }
    	// Check if all players are ready
    	if (isAllReady) {
    		for (GameSession session : activeSessions) {
    			Platform.runLater(() -> {
					WordleView wordleView = new WordleView(new Stage());  // Create WordleView with new Stage
					session.startGame(wordleView, game);  // Pass the single WordleGame instance
	    		});
    		}
		} else {
    		Platform.runLater(() -> {
    			showAlert("Alert", "All players must be ready.");
    		});
		}
    }
    
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Créer une alerte de type erreur
        alert.setTitle(title); // Ajouter un titre à l'alerte
        alert.setHeaderText(null); // Ajouter un en-tête à l'alerte
        alert.setContentText(message); // Ajouter un contenu à l'alerte
        alert.showAndWait(); // Afficher l'alerte
    }

    public void removeClient(ClientHandler clientHandler) {
        boolean removed = waitingClients.remove(clientHandler); // On supprime le client de la liste des clients en attente
        if (!removed) { // Si le client n'a pas été supprimé
            for (GameSession session : activeSessions) { // Pour chaque session active
                if (session.removeClient(clientHandler)) { // Si le client a été supprimé de la session
                    break; // On sort de la boucle
                }
            }
        }
        updateLobbyStateForAllClients();    // On met à jour l'état du lobby pour tous les clients
    }

    public synchronized void updateLobbyStateForAllClients() {
        for (ClientHandler client : server.getClients()) {
            client.sendMessage("Lobby status: " + getLobbyStatus());
            client.sendMessage("GAME_INFO:" + getGameInfo().toString());  // Assurez-vous d'envoyer les informations de jeu mises à jour
        }
    }

    public void notifyAllClients(String message) { // On notifie tous les clients
        for (ClientHandler client : waitingClients) { // Pour chaque client en attente
            client.sendMessage(message); // On envoie le message 
        }
    }

    public void addSession(GameSession session) {
        this.activeSessions.add(session); // On ajoute la session à la liste des sessions actives
    }

    public void removeSession(GameSession session) {
        this.activeSessions.remove(session); // On supprime la session de la liste des sessions actives
    }

    public synchronized void startNewGameSession(GameConfig config) {
        if (!activeSessions.stream().anyMatch(session -> session.getPlayers().equals(waitingClients))) {
            GameSession newSession = new GameSession(new ArrayList<>(waitingClients), config);
            activeSessions.add(newSession);
            System.out.println("New game session created with configuration: " + config);
        } else {
            System.out.println("A session with the current players already exists.");
        }
    }
    
    private String mapDifficultyToString(int difficultyLevel) {
        switch(difficultyLevel) { // On utilise une structure de contrôle switch
            case 1: return "Facile"; // Si le niveau de difficulté est 1, on retourne "Facile"
            case 2: return "Moyen"; // Si le niveau de difficulté est 2, on retourne "Moyen"
            case 3: return "Difficile"; // Si le niveau de difficulté est 3, on retourne "Difficile"
            default: return "Inconnu"; // Sinon, on retourne "Inconnu"
        }
    }

    public boolean isFull() {
        return waitingClients.size() >= maxPlayersPerSession; // On retourne vrai si le nombre de clients en attente est supérieur ou égal au nombre maximum de joueurs par session
    }

    public void updateGameSettings(int difficultyLevel, int gameDuration, int numberOfWords, int maxPlayers) {
        this.maxPlayersPerSession = maxPlayers; // Update max players
        this.gameConfig.setGameDuration(gameDuration);
        this.gameConfig.setNumberOfWords(numberOfWords);
        this.gameConfig.setDifficultyLevel(difficultyLevel);
        
        // Log the new settings to confirm they're being updated
        System.out.println("Updated lobby settings: Difficulty=" + difficultyLevel + ", Duration=" + gameDuration + ", Words=" + numberOfWords + ", Max Players=" + maxPlayers);

        broadcastGameInfo(); // Update all clients with the new game info
    }

    public synchronized void broadcastGameInfo() {
        String gameInfoMessage = "GAME_INFO:" + getGameInfo().toString();
        for (ClientHandler client : server.getClients()) {
            client.sendMessage(gameInfoMessage);
        }
    }
    
    public synchronized void updateLobbyConfig(int maxPlayers, int gameDuration, int difficultyLevel, int numberOfWords) {
        this.maxPlayersPerSession = maxPlayers;
        this.gameConfig.setGameDuration(gameDuration);
        this.gameConfig.setNumberOfWords(numberOfWords);
        this.gameConfig.setDifficultyLevel(difficultyLevel);

        // Notification aux clients que les paramètres ont été mis à jour
        String gameInfoMessage = "CONFIG_UPDATE:" + this.getGameInfo().toString();
        broadcastGameInfo(gameInfoMessage); // Assurez-vous que cette méthode envoie les informations à tous les clients
    }

    public synchronized void broadcastGameInfo(String gameInfoMessage) {
        for (ClientHandler client : server.getClients()) {
            client.sendMessage(gameInfoMessage);
        }
    }


    public synchronized String getLobbyStatus() {
        return "Nombre de joueurs en attente : " + waitingClients.size();
    }

    public List<String> getPlayerNamesInLobby() {
        return waitingClients.stream()
                             .map(ClientHandler::getPlayerName)
                             .filter(Objects::nonNull)
                             .collect(Collectors.toList());
    }

    public List<GameSession> getActiveSessions() {
        return this.activeSessions; // On retourne les sessions de jeu actives
    }

    public GameInfo getGameInfo() {
        return new GameInfo(serverPort, waitingClients.size(), mapDifficultyToString(gameConfig.getDifficultyLevel()), gameConfig.getGameDuration(), gameConfig.getNumberOfWords(), maxPlayersPerSession, host != null ? host.getPlayerName() : "N/A");
    }

    public ClientHandler getHost() {
        return host;
    }

    public void setDifficulty(String difficulty) {
        this.gameConfig.setDifficultyLevel(mapDifficultyToLevel(difficulty));
    }

    public void setGameDuration(int duration) {
        this.gameConfig.setGameDuration(duration);
    }

    public void setNumberOfWords(int numberOfWords) {
        this.gameConfig.setNumberOfWords(numberOfWords);
    }

    private int mapDifficultyToLevel(String difficulty) {
        switch (difficulty) {
            case "Facile": return 1;
            case "Moyen": return 2;
            case "Difficile": return 3;
            default: return 1; // ou lancez une exception si nécessaire
        }
    }
      
    public int getMaxPlayersPerSession() {
        return maxPlayersPerSession;
    }
}