package view;

import controller.WordleGame;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GamePortal 
{
    private Stage primaryStage;
    private BorderPane root;

    private WordleGame game;

    public GamePortal(Stage primaryStage, WordleGame game) {
        this.primaryStage = primaryStage;
        this.game = game;
        initializeStage();
    }

    /**
     * Méthode initializeStage
     * Cette méthode permet d'initialiser la fenetre principale.
     * @return void
     * @author: Dylan Gely
     */
    private void initializeStage() {
        root = new BorderPane();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Portail de Jeux");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        root.setStyle("-fx-background-color: grey");
        primaryStage.setMinWidth(375);
        primaryStage.setMinHeight(700);
        initializeGamePortalContent();
    }


    /**
     * Méthode createAndSetCenterContent
     * Cette méthode permet de créer et configurer le contenu du portail de jeux.
     * @return VBox
     * @author: Dylan Gely
     */
    private VBox initializeGamePortalContent()
    {
    	root.setStyle("-fx-background-color: black");

        Label titleLabel = new Label("Bienvenue au portail de jeux"); // Crée une étiquette de titre
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;"); // Définit le style de l'étiquette

        Button wordleButton = createGameButton("WORDLE");

        wordleButton.setOnAction(e -> {
            WordleView wordleView = new WordleView(primaryStage);
            wordleView.setGame(game);
            wordleView.showWordleGame(); 
        });

        // Définit l'action du bouton qui va afficher le jeu Wordle
        Button otherGamesButton = createGameButton("Autres Jeux"); // Crée le bouton " Autres Jeux "
        otherGamesButton.setOnAction(e -> showPopupMessage("D'autres jeux seront proposés plus tard.")); // Définit l'action du bouton qui affiche un pop up

        VBox buttonBox = new VBox(20, wordleButton, otherGamesButton); // Crée une VBox pour organiser les boutons verticalement avec un espacement de 20 px
        buttonBox.setAlignment(Pos.CENTER); // Aligne les boutons de la VBox au centre

        VBox titleAndButtons = new VBox(30, titleLabel, buttonBox); // Crée une VBox contenant le titre et la VBox des boutons avec un espacement de 30 px
        titleAndButtons.setAlignment(Pos.CENTER); // Aligne le contenu au centre
        root.setCenter(titleAndButtons); // Définit le contenu central du BorderPane avec le VBox
        return titleAndButtons;
    }

	 /**
    * Méthode createGameButton
    * Cette méthode permet de créer un bouton pour un jeu.
    * @return Button
    * @author: Dylan Gely
    */
   private Button createGameButton(String gameName)
   {
       Button button = new Button(gameName);   // Crée un bouton avec le nom du jeu
       button.setStyle("-fx-font-size: 30; -fx-min-width: 200px; -fx-min-height: 100px;"); // Définit le style du bouton
       return button;  // Retourne le bouton
   }

   /**
    * Méthode showPopupMessage
    * Cette méthode permet d'afficher un pop up avec un message.
    * @param message : Il s'agit du message à afficher
    * @return void
    * @author: Dylan Gely
    */
   private void showPopupMessage(String message)
   {
       Stage popupStage = new Stage(StageStyle.UTILITY); // Crée une fenetre de style "UTILITY" pour le pop up
       popupStage.initOwner(primaryStage); // Définit la fenetre principale comme propriétaire du pop up
       Label label = new Label(message); // Crée une étiquette avec le message
       label.setStyle("-fx-font-size: 18;"); // Définit le style de l'étiquette
       VBox popupContent = new VBox(label); // Crée une VBox contenant l'étiquette
       popupContent.setStyle("-fx-background-color: white; -fx-padding: 30;"); // Définit le style de la VBox avec marge de 30px pour son contenu
       popupStage.setResizable(false); // Désactive le redimensionnement de la fenetre du pop up
       Scene popupScene = new Scene(popupContent, 400, 100); // Crée une scène pour la fenetre pop up et définit sa taille
       popupStage.setScene(popupScene); // Attribution de la scène à la fenetre pop up
       popupStage.show(); // Affiche la fenetre du pop up
   }

	public WordleGame getGame() {
		return game;
	}

	public void setGame(WordleGame game) {
		this.game = game;
	}
}
