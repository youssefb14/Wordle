package localView;

import controller.WordleGame;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe Main
 * Cette classe permet de créer la fenêtre principale du jeu Wordle et de gérer les événements.
 * @return void
 * @author: BOUDOUNT Youssef
 */
public class Main extends Application
{
	private Stage primaryStage; // Fenêtre principale

    /**
     * Methode start
     * Cette méthode permet d'initialise et affiche la scène principale du jeu Wordle.
     * @param primaryStage : Il s'agit de la fenêtre principale
     * @return void
     * @author: BOUDOUNT Youssef
     */
	@Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        WordleGame game = new WordleGame("E:\\UNIVERSITE\\depots\\projet programmation\\project\\data\\mots.json", 
        		"E:\\UNIVERSITE\\depots\\projet programmation\\project\\data\\diccoGeneral.json");   // Créer une instance du jeu Wordle

        new GamePortal(primaryStage, game); // Créer une instance du portail de jeux

        primaryStage.setTitle("Portail de Jeux"); // Définir le titre de la fenêtre
        primaryStage.setMaximized(true); // Maximiser la fenêtre
        primaryStage.show(); // Afficher la fenêtre
    }

    /**
     * Methode getPrimaryStage
     * Cette méthode permet de récupérer la fenêtre principale.
     * @return Stage : Il s'agit de la fenêtre principale
     * @author: BOUDOUNT Youssef
     */
	public Stage getPrimaryStage() {
		return primaryStage; // Récupérer la fenêtre principale
	}

    /**
     * Methode setPrimaryStage
     * Cette méthode permet de définir la fenêtre principale.
     * @param primaryStage : Il s'agit de la fenêtre principale
     * @author: BOUDOUNT Youssef
     */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

    /**
     * Methode main
     * Cette méthode permet de lancer l'application.
     * @param args : Il s'agit des arguments de la ligne de commande
     * @return void
     * @author: BOUDOUNT Youssef
     */
	public static void main(String[] args) {
        launch(args);
    }
}

