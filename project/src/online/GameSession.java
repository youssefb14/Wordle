package online;

import java.util.ArrayList; // Pour utiliser les ArrayList
import java.util.List;    // Pour utiliser les List
import java.util.concurrent.atomic.AtomicBoolean; // Pour utiliser les AtomicBoolean
import controller.WordleGame; // Pour utiliser la classe WordleGame
import localView.GamePortal;
import localView.WordleView;

public class GameSession {
    private final List<ClientHandler> clientHandlers;   // Liste des gestionnaires de clients
    private GameConfig gameConfig;  // Configuration du jeu
    private WordleGame game;   // Jeu Wordle
    private AtomicBoolean gameInProgress = new AtomicBoolean(false); // Indique si le jeu est en cours

    public GameSession(List<ClientHandler> clientHandlers, GameConfig gameConfig) {
        this.clientHandlers = new ArrayList<>(clientHandlers); // On initialise la liste des gestionnaires de clients
        this.gameConfig = gameConfig;  // On initialise la configuration du jeu
    }

    public synchronized boolean isFull() {
        return clientHandlers.size() >= gameConfig.getMaxPlayers();
    }

    public void closeSession() {
        notifyClients("La session de jeu se termine."); // On notifie les clients que la session de jeu se termine
        for (ClientHandler clientHandler : clientHandlers) { // Pour chaque gestionnaire de client
            clientHandler.closeEverything(); // On ferme tout
        }
    }

	public synchronized boolean removeClient(ClientHandler clientHandler) {
	    return clientHandlers.remove(clientHandler);
	}

	public synchronized void relayAction(String action, ClientHandler sourceClient) {
	    for (ClientHandler client : clientHandlers) { // Pour chaque gestionnaire de client
	        if (client != sourceClient) { // Si le gestionnaire de client n'est pas celui qui a envoyé l'action
	            client.sendMessage(action); // On envoie l'action au gestionnaire de client
	        }
	    }
	}

	public synchronized void startGame() {
        if (!gameInProgress.getAndSet(true)) { // Si le jeu n'est pas en cours
            game.startGame(gameConfig.getDifficultyLevel()); // On démarre le jeu avec le niveau de difficulté de la configuration du jeu
            notifyAllPlayersToStart(); // On notifie tous les joueurs de commencer  
        }
        if (!gameInProgress.getAndSet(true)) { // Si le jeu n'est pas en cours
            notifyClients("Le jeu commence !"); // On notifie les clients que le jeu commence
        }
    }
	
	public synchronized void startGame(WordleView wordleView, WordleGame gamePassed) {
        if (!gameInProgress.getAndSet(true)) { // Si le jeu n'est pas en cours
        	notifyAllPlayersToStart(); // On notifie tous les joueurs de commencer
            
        	wordleView.setGamePortal(new GamePortal(wordleView.getPrimaryStage(), gamePassed)); // should be modified so that game launches on each player's window
            wordleView.setGame(gamePassed);
        	wordleView.startGame();
        }
        if (!gameInProgress.getAndSet(true)) { // Si le jeu n'est pas en cours
            notifyClients("Le jeu commence !"); // On notifie les clients que le jeu commence
        }
    }

    private void notifyAllPlayersToStart() {
        String message = "Le jeu commence! Vous pouvez tous soumettre vos mots."; // Message à envoyer
        notifyClients(message); // On notifie les clients
    }

    public synchronized void processPlayerGuess(ClientHandler clientHandler, String guess) {
        if (gameInProgress.get()) { // Si le jeu est en cours
            boolean isCorrect = game.evaluateGuess(guess); // On évalue le mot proposé
            if (isCorrect) { // Si le mot est correct
                notifyClients(clientHandler.getPlayerName() + " a trouvé le bon mot : " + guess); // On notifie les clients
                clientHandler.sendMessage("Félicitations ! Vous avez trouvé le mot correct."); // On félicite le joueur concerné
                endGame(); // On termine le jeu
            } else { // Sinon
                clientHandler.sendMessage("Essai incorrect."); // On informe le joueur que son essai est incorrect
            }
        }
    }

    private void endGame() {
        gameInProgress.set(false); // On indique que le jeu n'est plus en cours
        notifyClients("La session de jeu se termine. Le mot était : " + game.getSelectedWord()); // On notifie les clients
    }

    private void notifyClients(String message) { 
        for (ClientHandler clientHandler : clientHandlers) { // Pour chaque gestionnaire de client
            clientHandler.sendMessage(message); // On envoie le message
        }
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        if (!isFull()) { // Si la session de jeu n'est pas pleine
            this.clientHandlers.add(clientHandler); // On ajoute le gestionnaire de client
            System.out.println("Joueur ajouté à la session."); // On affiche un message
        }
    }

	public ClientHandler[] getPlayers() {
		        return clientHandlers.toArray(new ClientHandler[0]);
    }
}
