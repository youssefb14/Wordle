package online;

import java.io.BufferedReader; // Pour lire les données en entrée
import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie
import java.io.InputStreamReader; // Pour lire les données en entrée
import java.io.PrintWriter; // Pour écrire les données en sortie
import java.net.Socket; // Pour créer un socket
import controller.Score; // Pour gérer le score


public class ClientHandler implements Runnable {
    private Socket socket; // Attribut pour le socket
    private PrintWriter out; // Attribut pour écrire les données en sortie 
    private BufferedReader in; // Attribut pour lire les données en entrée
    private String playerName; // Attribut pour le nom du joueur
    private Server server; // Attribut pour le serveur
    private Score score; // Attribut pour le score
    private Lobby lobby; // Lobby associated to this clientHandler
    private HumanPlayer player; // Player associated to this clientHandler

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket; // On initialise le socket
        this.server = server; // On initialise le serveur
        this.score = new Score(); // On initialise le score
        try { // On gère les exceptions
            out = new PrintWriter(socket.getOutputStream(), true); // On crée un flux de sortie pour écrire les données
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // On crée un flux d'entrée pour lire les données
        } catch (IOException e) { // On gère les exceptions
            System.out.println("ClientHandler error: " + e.getMessage()); // On affiche un message d'erreur
        } 
    }
    
    public ClientHandler(Socket socket, Server server, Lobby lobby) {
        this.socket = socket; // On initialise le socket
        this.server = server; // On initialise le serveur
        this.score = new Score(); // On initialise le score
        this.lobby = lobby;
        try { // On gère les exceptions
            out = new PrintWriter(socket.getOutputStream(), true); // On crée un flux de sortie pour écrire les données
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // On crée un flux d'entrée pour lire les données
        } catch (IOException e) { // On gère les exceptions
            System.out.println("ClientHandler error: " + e.getMessage()); // On affiche un message d'erreur
        } 
    }

    public ClientHandler(Socket socket, Server server, String playerName) {
        this.socket = socket;   // On initialise le socket
        this.server = server;  // On initialise le serveur
        this.playerName = playerName; // On initialise le nom du joueur
        this.player = new HumanPlayer(playerName);
        try {
            out = new PrintWriter(socket.getOutputStream(), true); // On crée un flux de sortie pour écrire les données 
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // On crée un flux d'entrée pour lire les données
            sendMessage("Bienvenue " + playerName + "!"); // On envoie un message de bienvenue au joueur
        } catch (IOException e) { // On gère les exceptions
            System.out.println("ClientHandler error: " + e. getMessage()); // On affiche un message d'erreur
        }
    }
    
    public ClientHandler(Lobby lobby) {
    	this.lobby = lobby;
    }

    public void sendMessage(String message) {
        out.println(message); // On envoie le message au client
    }

    void closeEverything() {
        try { // On gère les exceptions
            if (out != null) { // Si le flux de sortie n'est pas null
				out.close(); // On le ferme
			}
            if (in != null) { // Si le flux d'entrée n'est pas null
				in.close();
			}
            if (socket != null) { // Si le socket n'est pas null
				socket.close();
			}
        } catch (IOException e) { // On gère les exceptions
            e.printStackTrace(); // On affiche un message d'erreur
        } finally { // On gère les exceptions
            server.removeClient(this); // On supprime le client
            server.getLobby().removeClient(this); // On supprime le client du lobby
        }
    }

    public String getPlayerName() {
        return playerName; // On retourne le nom du joueur
    }
    
    public HumanPlayer getPlayer() {
    	return this.player;
    }
    
    public void setPlayer(HumanPlayer player) {
    	this.player = player;
    }

    public void run() {
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Message from " + (playerName != null ? playerName : "unknown") + ": " + inputLine);
                processCommand(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception in ClientHandler for " + (playerName != null ? playerName : "unknown") + ": " + e.getMessage());
        } finally {
            closeEverything();
        }
    }

    private void processCommand(String message) { 
    	if (message.startsWith("SEND_NAME:")) {
    	    String name = message.substring("SEND_NAME:".length()).trim();  
    	    this.playerName = name;
    	    this.player = new HumanPlayer(this.playerName);
    	    System.out.println("ClientHandler - processCommand(): SEND_NAME - Nom du joueur: " + name); 
    	}
    	else if(message.startsWith("CHAT:")) { 
            String chatMessage = message.substring("CHAT:".length()).trim(); 
            if (playerName == null) { 
                playerName = "Anonymous";
            }
            server.relayChatMessage(playerName + ": " + chatMessage);
        }
    	else if(message.startsWith("SYSTEM:")) { 
            String chatMessage = message.substring("SYSTEM:".length()).trim(); 
            if (playerName == null) { 
                playerName = "Anonymous";
            }
            server.relayChatMessage("SYSTEM: " + chatMessage);
        }
    	else if (message.startsWith("CONFIG_UPDATE:")) {
            server.handleConfigurationChange(message);
            server.broadcastConfigurationUpdate();
        } else if (message.startsWith("GAME_INFO:")) {
            handleGameInfoUpdate(message);
        }
    	else if (message.equals("START_GAME")) { 
//			server.startGame(); 
			if (lobby.getHost() != null) lobby.startGameForAllSessions();
			System.out.println("ClientHandler - processCommand(): START_GAME - Démarrage du jeu");
		}
    	else if (message.equals("PLAYER_JOINED")) { 
    		server.startGame(); 
			System.out.println("ClientHandler - processCommand(): A new player joined");
		}
    	else if (message.equals("GET_GAME_INFO")) { 
	        GameInfo gameInfo = server.getLobby().getGameInfo(); 
	        sendMessage("GAME_INFO:" + gameInfo.toString()); 
	    }
    	else if (message.equals("RECONNECT")) { 
			//server.reconnectClient(this); 
			System.out.println("ClientHandler - processCommand(): RECONNECT - Reconnexion du client"); // On affiche un message
		} 
    	else if (message.equals("ADD_BOT")) { 
			//server.reconnectClient(this); 
			System.out.println("ClientHandler - processCommand(): A bot is added"); // On affiche un message
		} 
    	else if (message.equals("QUIT_GAME")) { 
		    server.removeClient(this); 
		    closeEverything(); 
		    System.out.println(playerName + " a quitté le jeu."); // On affiche un message
		}
    	else if (message.equals("PLAYER_READY")) { 
			// Change player state
    		if (this.getPlayer().isReady() == false) this.getPlayer().setReady(true);
    		else if (this.getPlayer().isReady() == true) this.getPlayer().setReady(false);
    		String isReady = this.getPlayer().isReady() ? "Ready" : "Not Ready";
    		server.relayChatMessage("SYSTEM: " + this.getPlayerName() + " is " + isReady);
			System.out.println("ClientHandler - processCommand(): PLAYER_READY - " + playerName + " is " + isReady); // On affiche un message
		}
    	
    	else { // Sinon c'est a dire si la commande n'est pas reconnue
			System.out.println("ClientHandler - processCommand(): Commande non reconnue: " + message); // On affiche un message d'erreur
		}
    }
    
    private void handleGameInfoUpdate(String message) {

        String info = message.substring("GAME_INFO:".length());
        sendMessage("Updated Game Info: " + info);
    }

    public void handleReconnection(Socket newSocket) {
        try { // On gère les exceptions
            if (out != null) { // Si le flux de sortie n'est pas null
				out.close(); // On le ferme
			}
            if (in != null) { // Si le flux d'entrée n'est pas null
				in.close(); 
			}
            if (socket != null && !socket.isClosed()) { // Si le socket n'est pas null et n'est pas fermé
				socket.close();
			}

            this.socket = newSocket; // On initialise le nouveau socket

            out = new PrintWriter(newSocket.getOutputStream(), true); // On crée un flux de sortie pour écrire les données
            in = new BufferedReader(new InputStreamReader(newSocket.getInputStream())); // On crée un flux d'entrée pour lire les données
 
            sendMessage("Reconnexion réussie."); // On envoie un message de confirmation

        } catch (IOException e) {
            System.out.println("Erreur lors de la gestion de la reconnexion : " + e.getMessage());
            // TODO : Enregistrer dans un journal le fait que la reconnexion a échoué
        }
    }

    public void processChatMessage(String message) {
        String formattedMessage = playerName + ": " + message; // On formate le message
        server.relayChatMessage(formattedMessage); // On envoie le message de chat
    }

    public void correctLetter() {
        score.correctLetter();
    }

    public void correctWord() {
        score.correctWord();
    }

    public void resetScore() {
        score.reset();
    }

    public int getScore() {
        return score.getScore();
    }

}
