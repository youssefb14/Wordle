package view;

import controller.WordleGame;
import controller.hint.Server;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe Main
 * Cette classe permet de créer la fenêtre principale du jeu Wordle et de gérer les événements.
 * @return void
 * @author: BOUDOUNT Youssef
 * @author: Dylan Gely
 */
public class Main extends Application
{
	private Stage primaryStage;

    /**
     * Methode start
     * Cette méthode permet d'initialise et affiche la scène principale du jeu Wordle.
     * @param primaryStage : Il s'agit de la fenêtre principale
     * @return void
     * @author: Dylan Gely
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        WordleView wordleView = new WordleView(primaryStage);

        wordleView.createAndSetCenterContent(); // Affiche le portail de jeux au lancement de l'application
        primaryStage.setTitle("Portail de Jeux");
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        Server.async_start();
        // make sure server is closed when app is closed to avoid having multiple ghost servers running in background
        Runtime.getRuntime().addShutdownHook(new Thread(Server::close));
    }
    
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}

