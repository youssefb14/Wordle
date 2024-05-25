package online;

/**
 * Interface ServerMessageListener
 * Cette interface permet de définir une méthode pour écouter les messages du serveur.
 * @author: GENTI Anthony
 */
public interface ServerMessageListener {

	void onServerMessage(String message);
}
