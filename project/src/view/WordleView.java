package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Score;
import controller.WordleGame;
import controller.hint.Server;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import model.FrenchWordChecker;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class WordleView implements GameInterface {
	
	private Stage primaryStage; // On définit la fenêtre principale
    private BorderPane root;    // On définit le BorderPane pour la mise en page
    private StackPane gamePane; // On définit le StackPane pour le jeu Wordle
    private WordleGame game;
    private boolean doubleCellSize = false; // On crée un boolean pour la taille des cellules de la grille qui va gérer le changement de taille des cellules
    private String targetWord;  // On définit le mot cible  pour le jeu Wordle
    private Label errorLabel;   // On définit le label d'erreur pour les messages d'erreur
    private Map<String, Button> letterButtonMap = new HashMap<>();
    public int gridSize = 5; // Valeur par défaut pour la taille de la grille
    
    // Score
    private Score score;
    private Label scoreLabel;
    
    // Temps
    private Label timeLabel;
    private Timeline timeline;
    private long startTime;

    
    /********************************************************************************************
    Configuration initiale et paramètres de la vue
    ********************************************************************************************/
    public WordleView()
    {
    	root = new BorderPane();
    	gamePane = new StackPane();
    }
    
    public WordleView(Stage primaryStage)
    {
    	this.primaryStage = primaryStage;
        root = new BorderPane();
        gamePane = new StackPane();
        score = new Score();
        scoreLabel = new Label("Score: 0");
        this.timeLabel = new Label("Temps : 00:00:00");
        setupAndStartTimer();
    }

    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Méthode setGame
     * Cette méthode permet de définir le jeu Wordle.
     * @param game Il s'agit du jeu Wordle à définir
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	public void setGame(WordleGame game)
	{
		this.game = game;
	}

	/**
     * Méthode getGame
     * Cette méthode permet de récupérer le jeu Wordle.
     * @return
     */
	public WordleGame getGame()
	{
		return game;
	}
	
	public void initializeGame() {
	    this.game = new WordleGame("data/mots.json", "data/diccoGeneral.json");
	}
	
	/********************************************************************************************
    Gestion du jeu
    ********************************************************************************************/
    @Override
	public void showGame(Stage stage)
    {
        this.primaryStage = stage;
        showWordleGame(); // ou toute autre logique pour démarrer le jeu
    }

    /**
     * Méthode startGame
     * Cette méthode permet de démarrer le jeu Wordle.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public void startGame()
    {
        showWordleGame();   // On appelle la méthode qui affiche la fenetre de jeu Wordle
    }

    private void resetGame()
    {
        // Conserver l'état maximisé, la taille et la position de la fenêtre
        boolean isMaximized = primaryStage.isMaximized();
        double width = primaryStage.getWidth();
        double height = primaryStage.getHeight();
        double xPosition = primaryStage.getX();
        double yPosition = primaryStage.getY();

        // Réinitialisez la grille et les autres composants du jeu
        letterButtonMap.clear();
        gamePane.getChildren().clear();
        gamePane.setStyle("-fx-background-color: black");

        targetWord = game.getRandomWord(gridSize);
        setupGameView(); // Configurer la vue du jeu avec la nouvelle grille

        // Restaurer l'état maximisé, la taille et la position après la mise à jour
        Platform.runLater(() -> {
            if (isMaximized) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setWidth(width);
                primaryStage.setHeight(height);
                primaryStage.setX(xPosition);
                primaryStage.setY(yPosition);
            }
        });
        
        score.reset();
        updateScoreDisplay();
    }

    /********************************************************************************************
    Interface utilisateur et affichage de la vue
    ********************************************************************************************/
    public void showWordleGame() 
    {
        if (primaryStage == null) {
            primaryStage = new Stage();
            primaryStage.setTitle("Wordle Game");
        }

        setupGameView(); // Passer root et gamePane à la méthode setupGameView

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Méthode setupGameView
     * Cette méthode permet d'afficher la fenetre de jeu Wordle avec la grille, le clavier virtuel et les boutons et labels.
     * Elle permet aussi de gérer les événements des boutons et de la grille.
     * @return void
     * @auhtor:  Dylan Gely et BOUDOUNT Youssef
     */
    void setupGameView()
    {    	
    	root.getChildren().clear(); // Nettoyer tous les enfants précédents
    	root = new BorderPane();
    	
    	targetWord = game.getRandomWord(gridSize); // Obtention d'un mot aléatoire
        int wordLength = targetWord.length();
        root.setStyle("-fx-background-color: black");
        
        gamePane = new StackPane();
        gamePane.setStyle("-fx-background-color: black");
        
        // Grille de jeux
        gridSize = wordLength;
        GridPane grid = createGrid(gridSize);
              
        /******************/
        // Configuration du scoreLabel et du timeLabel
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        timeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        // Créer un VBox pour le scoreLabel et le timeLabel
        VBox scoreAndTimeContainer = new VBox(10, scoreLabel, timeLabel); // 10 est l'espacement vertical entre les labels
        scoreAndTimeContainer.setAlignment(Pos.TOP_LEFT); // Alignement en haut à gauche
        scoreAndTimeContainer.setPadding(new Insets(10, 0, 0, 10)); // Ajout de marge

        // Ajouter scoreAndTimeContainer au gamePane directement
        gamePane.getChildren().add(scoreAndTimeContainer);
        
        /**********************/
        
        // Messagee de notification
        errorLabel = new Label();   // On crée un label pour les messages d'erreur
        errorLabel.setStyle("-fx-text-fill: red; -fx-background-color: white; -fx-padding: 5px; -fx-border-radius: 5px;");  // On définit le style du label
        errorLabel.setVisible(false);  // On définit le label comme invisible

        // Boutons de l'en-tête
        Button aideButton = new Button("Aide"); // On crée un bouton pour l'aide
        aideButton.setOnAction(e -> showAide());    // On définit l'action du bouton qui va afficher l'aide
        Button indiceButton = new Button("Indice"); // On crée un bouton pour l'indice
        indiceButton.setOnAction(e -> showIndice(targetWord));  // On définit l'action du bouton qui va afficher l'indice
        Button recommencerButton = new Button("Recommencer la Partie"); // On crée un bouton pour recommencer la partie
        //recommencerButton.setOnAction(e -> resetGame());
        recommencerButton.setOnAction(e -> showDifficultySelection());
        Button quitButton = new Button("Quitter le jeu");   // On crée un bouton pour quitter le jeu
        
        quitButton.setOnAction(e -> {
            root.getChildren().clear(); // Nettoyer le root BorderPane
            createAndSetCenterContent(); // Retourner au menu principal
        });
        
        // Titre de l'en-tête
        Label titleLabel = new Label("Wordle"); // On crée un label pour le titre de l'en-tête
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // On définit le style du label
        
        
        HBox leftButtons = new HBox(10, aideButton, indiceButton);  // On crée une HBox pour organiser les boutons gauches horizontalement avec un espacement de 10 px
        HBox rightButtons = new HBox(10, recommencerButton, quitButton); // On crée une HBox pour organiser les boutons droits horizontalement avec un espacement de 10 px
        
        HBox headerBox = new HBox(20, leftButtons, titleLabel, rightButtons);   // On crée une HBox pour organiser les boutons et le titre horizontalement avec un espacement de 20 px
        headerBox.setAlignment(Pos.CENTER);
        
        headerBox.setAlignment(Pos.CENTER); // On aligne le contenu de la HBox au centre
        headerBox.setPadding(new Insets(10, 20, 10, 20));   // On définit la marge de la HBox (Haut, Droite, Bas, Gauche)
        headerBox.setStyle("-fx-border-color: white; -fx-border-width: 2;");    // On définit le style de la HBox

        // Clavier
        Pair<VBox, List<Button>> clavierData = createClavier(); // On crée le clavier virtuel
        VBox clavier = clavierData.getKey();    // On récupère la VBox du clavier virtuel
        List<Button> keyboardButtons = clavierData.getValue();  // On récupère la liste de boutons du clavier virtuel
        clavier.setAlignment(Pos.CENTER);   // On aligne le contenu de la VBox au centre

        // Liaison du clavier à la grille
        bindClavierToGrid(grid, keyboardButtons);   // On appelle la méthode qui permet de lier le clavier à la grille

        VBox gameLayout = new VBox(10, errorLabel, grid);   // On crée une VBox pour organiser la grille et le label d'erreur verticalement avec un espacement de 10 px
        gameLayout.setAlignment(Pos.CENTER);    // On aligne le contenu de la VBox au centre
        gamePane.getChildren().add(gameLayout);   // On ajoute la VBox au StackPane
        
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(root)); // Création initiale de la scène
        } else {
            primaryStage.getScene().setRoot(root); // Mise à jour du contenu de la scène existante
        }
        
        //*****************STYLE ET MISE EN PAGE***********************
        root.setTop(headerBox); // On ajoute l'en-tête au BorderPane
        root.setCenter(gamePane); // On ajoute le StackPane au BorderPane
        root.setBottom(clavier);    // On ajoute le clavier virtuel au BorderPane


        String buttonStyle = "-fx-background-color: #2E2E2E; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5px; -fx-padding: 5px 10px;";    // On définit le style des boutons
        aideButton.setStyle(buttonStyle);   // On définit le style du bouton d'aide
        indiceButton.setStyle(buttonStyle); // On définit le style du bouton d'indice
        recommencerButton.setStyle(buttonStyle);    // On définit le style du bouton de recommencer la partie
        quitButton.setStyle(buttonStyle);   // On définit le style du bouton de quitter le jeu

        String hoverStyle = "-fx-background-color: #444444;";
        aideButton.setOnMouseEntered(e -> aideButton.setStyle(buttonStyle + hoverStyle));   // On définit le style du bouton d'aide quand la souris est dessus
        aideButton.setOnMouseExited(e -> aideButton.setStyle(buttonStyle)); // On définit le style du bouton d'aide quand la souris n'est plus dessus

        indiceButton.setOnMouseEntered(e -> indiceButton.setStyle(buttonStyle + hoverStyle));   // On définit le style du bouton d'indice quand la souris est dessus
        indiceButton.setOnMouseExited(e -> indiceButton.setStyle(buttonStyle)); // On définit le style du bouton d'indice quand la souris n'est plus dessus

        recommencerButton.setOnMouseEntered(e -> recommencerButton.setStyle(buttonStyle + hoverStyle)); // On définit le style du bouton de recommencer la partie quand la souris est dessus
        recommencerButton.setOnMouseExited(e -> recommencerButton.setStyle(buttonStyle));   // On définit le style du bouton de recommencer la partie quand la souris n'est plus dessus

        quitButton.setOnMouseEntered(e -> quitButton.setStyle(buttonStyle + hoverStyle));   // On définit le style du bouton de quitter le jeu quand la souris est dessus
        quitButton.setOnMouseExited(e -> quitButton.setStyle(buttonStyle)); // On définit le style du bouton de quitter le jeu quand la souris n'est plus dessus
    }
    
    /**
     * Méthode createAndSetCenterContent
     * Cette méthode permet de créer et configurer le contenu du portail de jeux.
     * @return VBox
     * @author: Dylan Gely
     */
    public void createAndSetCenterContent() {
        root.setStyle("-fx-background-color: black");

        Label titleLabel = new Label("Bienvenue au portail de jeux");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

        Button wordleButton = createGameButton("WORDLE");
        wordleButton.setOnAction(e -> openDifficultySelection()); // Ouvre la fenêtre de sélection de la difficulté

        Button otherGamesButton = createGameButton("Autres Jeux");
        otherGamesButton.setOnAction(e -> showPopupMessage("D'autres jeux seront proposés plus tard."));

        VBox buttonBox = new VBox(20, wordleButton, otherGamesButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox titleAndButtons = new VBox(30, titleLabel, buttonBox);
        titleAndButtons.setAlignment(Pos.CENTER);
        root.setCenter(titleAndButtons);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void openDifficultySelection() {
        showDifficultySelection(); // Méthode existante pour afficher la fenêtre de choix de difficulté
    }

    /********************************************************************************************
    Gestion du clavier et des entrées
    ********************************************************************************************/
    /**
     * Méthode createClavier
     * Cette méthode permet de créer le clavier virtuel du jeu Wordle.
     * @return Pair<VBox : Il s'agit d'un conteneur qui permet d'organiser les éléments verticalement
     * @return List<Button> : Il s'agit d'une liste de boutons
     * @author: Dylan Gely et BOUDOUNT Youssef
     * source pour la création du clavier virtuel : https://www.youtube.com/watch?v=HyzA1aLKnFk&ab_channel=VysakhVidyadharan
     * source pour la création du clavier virtuel : https://stackoverflow.com/questions/26768523/javafx-virtual-keyboard
     * source pour Pair : https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
     */
    private Pair<VBox, List<Button>> createClavier()
    {
        VBox clavierLayout = new VBox(10);  // On crée une VBox pour organiser les boutons verticalement avec un espacement de 10 px
        GridPane gridPane = new GridPane(); // On crée un GridPane pour organiser les boutons horizontalement et verticalement
        gridPane.setHgap(5); // Espacement horizontal uniforme entre les boutons
        gridPane.setVgap(5); // Espacement vertical uniforme entre les boutons
        gridPane.setPadding(new Insets(0, 0, 40, 0)); // On définit la marge du GridPane avec 40 px en bas pour le centrer dans la VBox (Haut, Droite, Bas, Gauche)

        final int BUTTON_WIDTH = 40; // Largeur uniforme pour tous les boutons
        final int BUTTON_HEIGHT = 40; // Hauteur uniforme pour tous les boutons

        // On crée un tableau de String pour les lettres du clavier virtuel
        String[][] lignesClavier = {
                {"A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"},
                {"Q", "S", "D", "F", "G", "H", "J", "K", "L", "M"},
                {null, "⌫", "W", "X", "C", "V", "B", "N", "⏎", null}
        };

        // On définit le style des boutons du clavier virtuel
        String buttonStyleBase = "-fx-text-fill: white; -fx-border-color: white; " +
                "-fx-border-radius: 5px; -fx-padding: 5px 10px; " +
                "-fx-font-size: 14px;";
		String buttonColorStyle = "-fx-background-color: #2E2E2E;";
		String hoverStyle = "-fx-background-color: #444444;";

        List<Button> allButtons = new ArrayList<>();   // On crée une liste de boutons

        for (int i = 0; i < lignesClavier.length; i++)  // On parcourt chaque ligne du tableau de String contenant les lettres du clavier virtuel
        {
            for (int j = 0; j < lignesClavier[i].length; j++)  // On parcourt chaque lettre de chaque ligne du tableau de String contenant les lettres du clavier virtuel
            {
            	if (lignesClavier[i][j] != null) {
                    Button bouton = new Button(lignesClavier[i][j]);
                    bouton.setFocusTraversable(false);
                    bouton.setStyle(buttonStyleBase + buttonColorStyle);
                    bouton.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
                    bouton.setMaxSize(BUTTON_WIDTH, BUTTON_HEIGHT);
                    bouton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
                    allButtons.add(bouton);

                    bouton.getProperties().put("color", buttonColorStyle);

                    bouton.setOnMouseEntered(e -> {
                        String currentColorStyle = (String) bouton.getProperties().get("color");
                        bouton.setStyle(buttonStyleBase + currentColorStyle + hoverStyle);
                    });

                    bouton.setOnMouseExited(e -> {
                        String currentColorStyle = (String) bouton.getProperties().get("color");
                        bouton.setStyle(buttonStyleBase + currentColorStyle);
                    });

                    gridPane.add(bouton, j, i);

                    if (!"⌫".equals(lignesClavier[i][j]) && !"⏎".equals(lignesClavier[i][j])) {
                        letterButtonMap.put(lignesClavier[i][j], bouton);
                    }
                }
            }
        }

        clavierLayout.getChildren().add(gridPane);  // On ajoute le GridPane à la VBox

        clavierLayout.setAlignment(Pos.CENTER); // On aligne le contenu de la VBox au centre

        gridPane.setMaxWidth(Double.MAX_VALUE); // On définit la largeur maximale du GridPane à la largeur maximale de la VBox

        gridPane.setAlignment(Pos.CENTER);  // On aligne le contenu du GridPane au centre

        return new Pair<>(clavierLayout, allButtons);   // On retourne la VBox et la liste de boutons du clavier virtuel dans un Pair
    }
    
    /**
     * Méthode blindClavierToGrid
     * Cette méthode permet de lier le clavier virtuel à la grille.
     * @param grid Il s'agit de la grille à lier au clavier virtuel
     * @param keyboardButtons Il s'agit de la liste de boutons du clavier virtuel
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	private void bindClavierToGrid(GridPane grid, List<Button> keyboardButtons)
	{
	    for (Button button : keyboardButtons) // Pour chaque bouton du clavier virtuel
	    {
	        button.setOnAction(event -> // On définit un écouteur d'événement pour le bouton qui va gérer l'événement
	        {
	            TextField selectedCell = getSelectedCell(grid); // On récupère la cellule sélectionnée dans la grille
	            if (selectedCell != null)   // Si une cellule est sélectionnée
	            {
	                if(button.getText().equals("⌫"))    // Si le bouton est le bouton de suppression
	                {
	                    updateGridCell(selectedCell, "");   // On appelle la méthode qui met à jour le texte de la cellule sélectionnée avec une lettre vide
	                }
	                else if(button.getText().equals("⏎"))   // Si le bouton est le bouton de validation
	                {
	                    validateGuess(grid);   // On appelle la méthode qui gère la validation
	                }
	                else
	                {
	                    updateGridCell(selectedCell, button.getText());  // On appelle la méthode qui met à jour le texte de la cellule sélectionnée avec la lettre du bouton
	                }
	            }
	        });
	    }
	}

	/********************************************************************************************
    Création et gestion de la grille
    ********************************************************************************************/
	/**
	 * Méthode createGrid
	 * Cette méthode permet de créer la grille de jeu Wordle.
	 * @param gridSize : Il s'agit de la taille de la grille
	 * @return GridPane
	 * @author: Dylan Gely et BOUDOUNT Youssef
	 * source pour GridPane : https://devstory.net/10641/javafx-gridpane
	 * source pour GridPane : http://www.java2s.com/Tutorials/Java/JavaFX/0340__JavaFX_GridPane.htm
	 */
	private GridPane createGrid(int gridSize)
    {
        GridPane grid = new GridPane(); // On crée un GridPane
        grid.setAlignment(Pos.CENTER);  // On aligne le contenu du GridPane au centre
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);

        int cellSize = doubleCellSize ? 100 : 60; // On définit la taille des cellules de la grille

        for (int row = 0; row < gridSize; row++)    // On parcourt chaque ligne de la grille
        {
            for (int col = 0; col < gridSize; col++)    // On parcourt chaque colonne de la grille
            {
                TextField cell = new TextField();  // On crée une cellule de la grille avec un TextField pour pouvoir entrer une lettre
                
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);
                cell.setAlignment(Pos.CENTER);

                cell.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));


                cell.setStyle("-fx-background-color: #2E2E2E; "
                             + "-fx-text-fill: white; "
                             + "-fx-border-color: white; "
                             + "-fx-border-radius: 5px; "
                             + "-fx-padding: 5px; ");

    
                
                cell.lengthProperty().addListener((observable, oldValue, newValue) ->   // On définit un écouteur de changement de longueur de la cellule
                {
                    if (newValue.intValue() > 1)    // Si la longueur de la cellule est supérieure à 1
                    {
                        cell.setText(cell.getText().substring(0, 1));   // On définit le texte de la cellule pour qu'il ne contienne que la première lettre
                        // La fonction substring permet de récupérer une partie d'une chaîne de caractères
                    }
                });

                cell.textProperty().addListener((observable, oldValue, newValue) ->  // On définit un écouteur de changement de texte de la cellule
                {
                    cell.setText(newValue.toUpperCase());   // On définit le texte de la cellule en majuscule pour que l'utilisateur ne puisse pas entrer de lettre en minuscule

                    int currentCol = GridPane.getColumnIndex(cell); // On récupère la colonne de la cellule
                    int currentRow = GridPane.getRowIndex(cell);    // On récupère la ligne de la cellule

                    if (!newValue.isEmpty() && currentCol < gridSize - 1) // Si la cellule n'est pas vide et que la colonne de la cellule est inférieure à la taille de la grille - 1
                    {
                        Node nextNode = getTextFieldAt(grid, currentCol + 1, currentRow);   // On récupère la cellule suivante
                        if (nextNode != null)   // Si la cellule suivante n'est pas null
                        {
                            nextNode.requestFocus();   // On donne le focus à la cellule suivante
                        }
                    }
                });

                cell.setOnKeyPressed(event ->   // On définit un écouteur d'événement pour la cellule
                {
                    int currentCol = GridPane.getColumnIndex(cell); // On récupère la colonne de la cellule
                    int currentRow = GridPane.getRowIndex(cell);    // On récupère la ligne de la cellule

                    switch (event.getCode())    // Pour chaque touche du clavier virtuel appuyée
                    {
                        case ENTER: // Si la touche est la touche Entrée
                            if (currentRow < gridSize - 1 && currentCol == gridSize - 1) // Si la ligne de la cellule est inférieure à la taille de la grille - 1 et que la colonne de la cellule est égale à la taille de la grille - 1
                            {
                                TextField nextRowFirstCell = getTextFieldAt(grid, 0, currentRow + 1); // On récupère la première cellule de la ligne suivante
                                nextRowFirstCell.requestFocus();    // On donne le focus à la première cellule de la ligne suivante
                            }
                            validateGuess(grid);    // On appelle la méthode validateGuess pour gérer la validation
                            break;  // On sort de la boucle
                        case BACK_SPACE:    // Si la touche est la touche Supprimer
                            if (cell.getText().isEmpty() && currentCol > 0) // Si la cellule est vide et que la colonne de la cellule est supérieure à 0
                            {
                                TextField previousCell = getTextFieldAt(grid, currentCol - 1, currentRow);  // On récupère la cellule précédente
                                previousCell.requestFocus();    // On donne le focus à la cellule précédente
                            }
                            else if (!cell.getText().isEmpty()) // Si la cellule n'est pas vide
                            {
                                cell.clear();   // On vide la cellule
                                TextField previousCell = getTextFieldAt(grid, currentCol - 1, currentRow);  // On récupère la cellule précédente
                                previousCell.requestFocus();    // On donne le focus à la cellule précédente
                            }
                            break;  // On sort de la boucle
                        default:    // Si la touche est une autre touche
                            break;  // On ne fait rien
                    }
                });
                                
                cell.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    System.out.println("Focus changed: " + isNowFocused); // Pour le débogage
                    if (isNowFocused) {
                        cell.setStyle("-fx-background-color: #444444; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3px; -fx-border-radius: 5px; -fx-padding: 5px;");
                    } else {
                        cell.setStyle("-fx-background-color: #2E2E2E; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5px; -fx-padding: 5px;");
                    }
                });                
                

                grid.add(cell, col, row);   // On ajoute la cellule au GridPane
            }
        }

        grid.setHgap(5);
        grid.setVgap(5);

        return grid;
    }
	
	/**
     * Methode udpateGridCell
     * Cette methode permet de mettre à jour le texte d'un TextField spécifié avec une lettre donnée.
     * @param cell le TextField à mettre à jour
     * @param letter la lettre à insérer dans le TextField ou un caractère de contrôle pour une action spéciale
     * @auhtor: BOUDOUNT Youssef
     */
    private void updateGridCell(TextField cell, String letter)
    {
        if ("⌫".equals(letter)) // Si le bouton est le bouton de suppression
        {
            String currentText = cell.getText();    // On récupère le texte de la cellule
            if (currentText.length() > 0)       // Si la longueur du texte de la cellule est supérieure à 0
            {
                cell.setText(currentText.substring(0, currentText.length() - 1)); // On supprime le dernier caractère du texte de la cellule
            }
        }
        else // Si le bouton n'est pas le bouton de suppression
        {
            cell.setText(letter);  // On définit le texte de la cellule avec la lettre du bouton
        }
    }
	
    /**
     * Méthode getSelectedCell
     * Cette méthode permet de récupère le TextField sélectionné dans un GridPane.
     * @param grid Il s'agit du GridPane à parcourir pour trouver le TextField sélectionné
     * @return TextField Il s'agit du TextField sélectionné, ou null si aucun n'est sélectionné
     * @auhtor: BOUDOUNT Youssef
     * source pour le TextField : https://stackoverflow.com/questions/30674612/get-selected-cell-value-in-jtable
     */
    private TextField getSelectedCell(GridPane grid)
    {
        for (Node node : grid.getChildren())    // On parcourt chaque enfant du GridPane qui correspond à une cellule de la grille
        {
            if (node instanceof TextField) // Si la cellule est un TextField c'est a dire si elle est sélectionnée
            {
                TextField cell = (TextField) node; // On récupère la cellule sélectionnée et on la stocke dans un TextField
                if (cell.isFocused())  // Si la cellule est sélectionnée
                {
                    return cell;   // On retourne la cellule sélectionnée
                }
            }
        }
        return null; // Si aucune cellule n'est sélectionnée on retourne null
    }
    
    /**
     * Méthode getTextFieldAt
     * Cette méthode permet de récupère un TextField situé à une position spécifique dans un GridPane.
     * @param grid le GridPane à parcourir
     * @param col l'index de la colonne du TextField recherché
     * @param row l'index de la ligne du TextField recherché
     * @return TextField le TextField trouvé, ou null si aucun n'est trouvé à l'emplacement spécifié
     * @author: BOUDOUNT Youssef
     * source pour le TextField : https://stackoverflow.com/questions/39184748/javafx-get-text-from-textfields-in-gridpane
     */
    private TextField getTextFieldAt(GridPane grid, int col, int row)
    {
        for (Node child : grid.getChildren())   // On parcourt chaque enfant du GridPane qui correspond à une cellule de la grille
        {
            int colIndex = GridPane.getColumnIndex(child) == null ? -1 : GridPane.getColumnIndex(child); // On récupère la colonne de la cellule
            int rowIndex = GridPane.getRowIndex(child) == null ? -1 : GridPane.getRowIndex(child);  // On récupère la ligne de la cellule

            // Si la colonne et la ligne de la cellule correspondent à la colonne et la ligne recherchées et que la cellule est un TextField
            if (colIndex == col && rowIndex == row && child instanceof TextField)
            {
                // On affiche un message dans la console avec la colonne et la ligne de la cellule et son contenu (pour le debug)
                System.out.println("getTextFieldAt ---- TextField trouvé à col: " + col + ", row: " + row + " avec contenu: " + ((TextField) child).getText());

                return (TextField) child;   // On retourne la cellule
            }
        }

        // On affiche un message dans la console si aucun TextField n'est trouvé à l'emplacement spécifié
        System.out.println("getTextFieldAt ---- TextField NON trouvé à col: " + col + ", row: " + row);

        return null;   // On retourne null car aucun TextField n'est trouvé à l'emplacement spécifié
    }

    /********************************************************************************************
    Validation et traitement des tentatives de l'utilisateur
    ********************************************************************************************/
    /**
     * Méthode validateGuess
     * Cette méthode permet de valider le mot proposé par l'utilisateur et les conséquences qui en découlent.
     * @param grid Il s'agit de la grille de jeu Wordle
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	private void validateGuess(GridPane grid)
	{
		String userGuess = "";  // On crée une chaîne de caractères pour stocker le mot proposé par l'utilisateur
	    int lastFilledRow = -1; // On définit la dernière ligne remplie par l'utilisateur à -1

	    for (int row = 0; row < gridSize; row++)   // On parcourt chaque ligne de la grille
	    {
	        boolean isRowEmpty = true;  // On définit la ligne comme vide
	        for (int col = 0; col < gridSize; col++)    // On parcourt chaque colonne de la grille
	        {
	            TextField cell = getTextFieldAt(grid, col, row); // On récupère la cellule à la colonne et la ligne spécifiées
	            if (!cell.getText().isEmpty())  // Si la cellule n'est pas vide
	            {
	                isRowEmpty = false; // On définit la ligne comme non vide
	                break;  // On sort de la boucle
	            }
	        }

	        if (!isRowEmpty) // Si la ligne n'est pas vide
	        {
	            lastFilledRow = row;   // On définit la dernière ligne remplie par l'utilisateur à la ligne actuelle
	        }
	    }

	    if (lastFilledRow == -1) // Si aucune ligne remplie n'est trouvée
	    {
	        System.out.println("validateGuess ---- Aucune ligne remplie trouvée");  // On affiche l'erreur dans la console pour le debug
	        errorLabel.setText("Veuillez entrer un mot de " + gridSize +" lettres"); // On définit le texte du label d'erreur
	    	errorLabel.setVisible(true);    // On définit le label d'erreur comme visible

	    	PauseTransition delay = new PauseTransition(Duration.seconds(2));   // On crée une pause de 2 secondes
	    	delay.setOnFinished(event -> errorLabel.setVisible(false));   // On définit l'action à effectuer à la fin de la pause
	    	delay.play();   // On lance la pause
	    	shakeGrid(grid);    // On appelle la méthode qui permet de secouer la grille
	        return; // On sort de la méthode
	    }

	    for (int col = 0; col < gridSize; col++)    // On parcourt chaque colonne de la grille
	    {
	        TextField cell = getTextFieldAt(grid, col, lastFilledRow);  // On récupère la cellule à la colonne et la dernière ligne remplie par l'utilisateur
	        userGuess += cell.getText();    // On ajoute le texte de la cellule à la chaîne de caractères du mot proposé par l'utilisateur
	    }

	    System.out.println("validateGuess --- Mot deviné extrait: " + userGuess);   // On affiche le mot proposé par l'utilisateur dans la console pour le debug

	    if (userGuess.isEmpty() || userGuess.length() != gridSize)  // Si le mot proposé par l'utilisateur est vide ou si sa longueur est différente de la taille de la grille
	    {
	    	errorLabel.setText("Veuillez entrer un mot de " + gridSize +" lettres");    // On définit le texte du label d'erreur
	    	errorLabel.setVisible(true);    // On définit le label d'erreur comme visible

	    	// Cachez le message d'erreur après quelques secondes.
	    	PauseTransition delay = new PauseTransition(Duration.seconds(2));
	    	delay.setOnFinished(event -> errorLabel.setVisible(false));
	    	delay.play();
	    	shakeGrid(grid);
	    }
	    else    // Si le mot proposé par l'utilisateur n'est pas vide et que sa longueur est égale à la taille de la grille
	    {

	    	if (FrenchWordChecker.isWordInFrenchDictionary(userGuess))  // Si le mot proposé par l'utilisateur appartient à la langue française
	    	{
			    List<Color> feedbackColors = WordleGame.compareWords(userGuess, targetWord); // On récupère la liste des couleurs de feedback pour chaque lettre du mot proposé par l'utilisateur

			    // Animation et mise à jour de la couleur des cases selon les feedbacks
			    for (int col = 0; col < gridSize; col++)    // On parcourt chaque colonne de la grille
			    {
			        TextField cell = getTextFieldAt(grid, col, lastFilledRow);  // On récupère la cellule la colonne et la dernière ligne remplie par l'utilisateur
			        Color feedbackColor = feedbackColors.get(col);  // On récupère la couleur de feedback pour la lettre de la cellule

			        animateFlipTile(cell, feedbackColor, null); // On appelle la méthode qui permet d'animer la cellule avec la couleur de feedback correspondante

			        Button correspondingButton = letterButtonMap.get(cell.getText().toUpperCase());
	                if (correspondingButton != null) {
	                    String colorStyle;
	                    if (feedbackColor.equals(Color.GREEN)) 
	                    {
	                        colorStyle = "-fx-background-color: green;";
	                        score.correctLetter();
	                    }
	                    else if (feedbackColor.equals(Color.ORANGE))
	                    {
	                        colorStyle = "-fx-background-color: orange;";
	                    }
	                    else
	                    {
	                        colorStyle = "-fx-background-color: red;";
	                    }

	                    correspondingButton.getProperties().put("color", colorStyle);
	                    String buttonStyleBase = "-fx-text-fill: white; -fx-border-color: white; " +
	                             "-fx-border-radius: 5px; -fx-padding: 5px 10px; " +
	                             "-fx-font-size: 14px;";
	                    correspondingButton.setStyle(buttonStyleBase + colorStyle);
	                }
			    }

			    disableRow(grid, lastFilledRow); // On appelle la méthode qui permet de désactiver la ligne remplie par l'utilisateur pour qu'il ne puisse plus la modifier

			    if (userGuess.equalsIgnoreCase(targetWord)) { // Si le mot proposé par l'utilisateur est égal au mot à deviner
			    	score.correctWord();
			        executeWinAnimations(grid, errorLabel, "Félicitations ! Vous avez trouvé le mot !")
			            .setOnFinished(event -> {
			                showNewGridWithMessage(grid, errorLabel, "Nouveau mot de " + (gridSize + 1) + " lettres.");
			                resetGame();
			            });
			    }
	    	}

	    	else    // Si le mot proposé par l'utilisateur n'appartient pas à la langue française
	    	{
	    		errorLabel.setText("Le mot n'appartien pas a la langue francaise");   // On définit le texte du label d'erreur
		    	errorLabel.setVisible(true);    // On définit le label d'erreur comme visible

		    	// Cachez le message d'erreur après quelques secondes.
		    	PauseTransition delay = new PauseTransition(Duration.seconds(2));
		    	delay.setOnFinished(event -> errorLabel.setVisible(false));
		    	delay.play();
		    	shakeGrid(grid);
	    	}
	    }
	    updateScoreDisplay();
	}

	/********************************************************************************************
    Gestion des niveaux et du score
    ********************************************************************************************/
	public void showDifficultySelection()
	{
		initializeGame();
	    VBox selectionLayout = new VBox(10);
	    selectionLayout.setAlignment(Pos.CENTER);

	    Label chooseDifficultyLabel = new Label("Choisissez la difficulté");
	    chooseDifficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

	    ToggleGroup difficultyGroup = new ToggleGroup();

	    RadioButton easyButton = new RadioButton("Facile");
	    easyButton.setUserData("5");
	    easyButton.setToggleGroup(difficultyGroup);
	    easyButton.setSelected(true);

	    RadioButton mediumButton = new RadioButton("Moyen");
	    mediumButton.setUserData("7");
	    mediumButton.setToggleGroup(difficultyGroup);

	    RadioButton hardButton = new RadioButton("Difficile");
	    hardButton.setUserData("9");
	    hardButton.setToggleGroup(difficultyGroup);

	    Button validateButton = new Button("Valider");
	    validateButton.setOnAction(e -> {
	        this.gridSize = Integer.parseInt(difficultyGroup.getSelectedToggle().getUserData().toString());
	        showWordleGame();
	        setupAndStartTimer();
	    });

	    selectionLayout.getChildren().addAll(chooseDifficultyLabel, easyButton, mediumButton, hardButton, validateButton);
	    
	    root.setCenter(selectionLayout);
	    
	    // Vérifiez si la scène est initialisée
	    Scene scene = primaryStage.getScene();
	    if (scene == null) {
	        scene = new Scene(root); // Création d'une nouvelle scène si elle n'existe pas
	        primaryStage.setScene(scene);
	    } else {
	        scene.setRoot(root); // Réutilisation de la scène existante
	    }
	    primaryStage.show();
	}
	
	private void updateScoreDisplay() {
	    scoreLabel.setText("Score: " + score.getScore());
	}
	
	private void updateTimeLabel() {
	    long now = System.currentTimeMillis();
	    long elapsedMillis = now - this.startTime;
	    long hours = elapsedMillis / 3600000;
	    long minutes = (elapsedMillis % 3600000) / 60000;
	    long seconds = (elapsedMillis % 60000) / 1000;
	    this.timeLabel.setText(String.format("Temps : %02d:%02d:%02d", hours, minutes, seconds));
	}

	private void setupAndStartTimer() {
	    this.startTime = System.currentTimeMillis();
	    this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeLabel()));
	    this.timeline.setCycleCount(Timeline.INDEFINITE);
	    this.timeline.play();
	}

	/********************************************************************************************
    Gestion des aides et des indices
    ********************************************************************************************/
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

    /**
     * Méthode showAide
     * Cette méthode permet d'afficher l'aide du jeu Wordle.
     * @return void
     * @author: Anthony Genti
     */
    private void showAide()
    {
        String instructions = "Règles du jeu Wordle :\n\n" +
                "- Le but est de deviner un mot secret en six essais.\n" +
                "- À chaque essai, essayez un mot et validez.\n" +
                "- Après chaque tentative, la couleur des lettres change pour montrer à quel point votre essai était proche du mot secret.\n" +
                "- Lettres vertes : bonnes lettres bien placées.\n" +
                "- Lettres oranges : bonnes lettres mal placées.\n" +
                "- Lettres grises : lettres non présentes dans le mot.\n" +
                "- Vous gagnez si vous devinez le mot secret en six essais ou moins.\n" +
                "- Vous perdez si vous n'arrivez pas à deviner après six essais.";

        // On crée un dialogue personnalisé avec le titre "Aide" et le message contenant les instructions.
        CustomDialog aideDialog = new CustomDialog("Aide", instructions);

        // On affiche le dialogue personnalisé et on attend qu'il soit fermé
        aideDialog.showAndWait();
    }

    /**
     * Méthode showIndice
     * Cette méthode permet d'afficher l'indice du jeu Wordle.
     * @param targetWord : Il s'agit du mot à deviner
     * @return void
     * @author: Anthony Genti
     */    
    private void showIndice(String targetWord)
    {
        String res;
        try {
            res = Server.getHint(targetWord).getKey();
        } catch (Exception e) {
            res = "Une erreur s'est produite lors de la requête vers le serveur: "+e;
        }
        // On crée un dialogue personnalisé qui va nous afficher l'indice du mot à deviner a fin de faciliter la tache au joueur
        CustomDialog indiceDialog = new CustomDialog("Indice", "Indice : " + res);

        // On affiche le dialogue personnalisé et on attend qu'il soit fermé
        indiceDialog.showAndWait();
    }
    
    public void stop() {
        Server.close();
    }

    /********************************************************************************************
    Couleurs
    ********************************************************************************************/
    /**
     * Méthode toRgbString
     * Cette méthode permet de convertir une couleur en chaîne de caractères RGB pour pouvoir l'utiliser dans le style CSS d'un élément.
     * @param color Il s'agit de la couleur à convertir en chaîne de caractères RGB
     * @return
     * @auhtor: BOUDOUNT Youssef
     * source : https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
    */
    private String toRgbString(Color color)
	{
	    return String.format("#%02X%02X%02X", // On définit le format de la chaîne de caractères RGB
	        (int) (color.getRed() * 255),   // On récupère la valeur du rouge de la couleur et on la multiplie par 255 pour avoir une valeur entre 0 et 255
	        (int) (color.getGreen() * 255), // On récupère la valeur du vert de la couleur et on la multiplie par 255 pour avoir une valeur entre 0 et 255
	        (int) (color.getBlue() * 255)); // On récupère la valeur du bleu de la couleur et on la multiplie par 255 pour avoir une valeur entre 0 et 255
	}
 
    /********************************************************************************************
    Effets et animations
    ********************************************************************************************/
	/**
     * Méthode disableRow
     * Cette méthode permet de désactiver une ligne de la grille.
     * @param grid Il s'agit de la grille de jeu Wordle
     * @param rowIndex Il s'agit de l'index de la ligne à désactiver
     * @return void
     * @auhtor: Anthony Genti
     */
	private void disableRow(GridPane grid, int rowIndex)
	{
	    for (int col = 0; col < gridSize; col++)    // On parcourt chaque colonne de la grille
	    {
	        TextField cell = getTextFieldAt(grid, col, rowIndex);   // On récupère la cellule à la colonne et la ligne spécifiées
	        cell.setDisable(true);  // On désactive la cellule
	    }
	}
	
    /**
     * Méthode animateFlipTile
     * Cette méthode permet d'animer une cellule avec une couleur de feedback.
     * @param cell Il s'agit de la cellule à animer
     * @param feedbackColor Il s'agit de la couleur de feedback à appliquer à la cellule
     * @param onAnimationEnd Il s'agit d'un Runnable qui sera appelé à la fin de l'animation
     * @return void
     * @auhtor: Anthony Genti
     */
	private void animateFlipTile(TextField cell, Color feedbackColor, Runnable onAnimationEnd)
	{
	    ScaleTransition st = new ScaleTransition(Duration.millis(100), cell);   // On crée une transition de mise à l'échelle pour la cellule
	    st.setFromY(1); // On définit la hauteur de départ de la cellule
	    st.setToY(0);   // On définit la hauteur d'arrivée de la cellule
	    st.setOnFinished(e ->   // On définit l'action à effectuer à la fin de la transition
	    {
            // On définit le style de la cellule avec la couleur de feedback
	        cell.setStyle("-fx-background-color: " + toRgbString(feedbackColor) + "; -fx-text-fill: white; -fx-border-color: black; -fx-border-radius: 5px; -fx-padding: 5px;");

	        ScaleTransition stBack = new ScaleTransition(Duration.millis(200), cell); // On crée une transition de mise à l'échelle pour la cellule
	        stBack.setFromY(0); // On définit la hauteur de départ de la cellule
	        stBack.setToY(1); // On définit la hauteur d'arrivée de la cellule
	        stBack.setOnFinished(e2 -> {
	            if (onAnimationEnd != null) {   // Si le Runnable onAnimationEnd n'est pas null c'est à dire si il y a une action à effectuer à la fin de l'animation
	                onAnimationEnd.run();   // On appelle le Runnable onAnimationEnd
	            }
	        });
	        stBack.play();  // On lance la transition
	    });
	    st.play();
	}
	
    /**
     * Méthode shakeGrid
     * Cette méthode permet de secouer la grille.
     * @param grid Il s'agit de la grille de jeu Wordle
     * @return void
     * @auhtor: Anthony Genti
     */
	private void shakeGrid(GridPane grid)
	{
	    TranslateTransition tt = new TranslateTransition(Duration.millis(100), grid);   // On crée une transition de translation pour la grille
	    tt.setFromX(0f);
	    tt.setByX(10f);
	    tt.setCycleCount(4);
	    tt.setAutoReverse(true);

	    tt.play();  // On lance la transition
	}
	
	/**
     * Méthode executeWinAnimations
     * Cette méthode permet d'exécuter les animations de victoire et d'afficher le message de succès.
     * @param grid  Il s'agit de la grille de jeu Wordle
     * @param errorLabel    Il s'agit du label d'erreur
     * @param successMessage    Il s'agit du message de succès
     * @return void
     * @auhtor: Anthony Genti
     */
	private ParallelTransition executeWinAnimations(GridPane grid, Label errorLabel, String successMessage)
    {
	    // Création des animations pour la grille
	    FadeTransition fadeTransitionGrid = new FadeTransition(Duration.seconds(0.5), grid);
	    fadeTransitionGrid.setFromValue(1.0);
	    fadeTransitionGrid.setToValue(0.3);
	    fadeTransitionGrid.setCycleCount(4);
	    fadeTransitionGrid.setAutoReverse(true);

	    // Création de l'animation d'agrandissement et de rétrécissement pour la grille
	    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), grid);
	    scaleTransition.setToX(1.1);
	    scaleTransition.setToY(1.1);
	    scaleTransition.setCycleCount(2);
	    scaleTransition.setAutoReverse(true);

	    // Animation de texte pour le message de succès
	    FadeTransition fadeTransitionLabel = new FadeTransition(Duration.seconds(0.5), errorLabel);
	    fadeTransitionLabel.setFromValue(1.0);
	    fadeTransitionLabel.setToValue(0.0); // Disparition
	    fadeTransitionLabel.setCycleCount(1);
	    fadeTransitionLabel.setAutoReverse(false);

	    // Afficher le message de succès
	    errorLabel.setText(successMessage);
	    errorLabel.setVisible(true);

	    // Combinaison des animations de la grille et du label
	    ParallelTransition parallelTransition = new ParallelTransition(fadeTransitionGrid, scaleTransition, fadeTransitionLabel);

	    // Jouer les animations
	    parallelTransition.play();

	    // Retourner l'animation pour y attacher un événement après son achèvement
	    return parallelTransition;
	}
	
	/**
     * Méthode showNewGridWithMessage
     * Cette méthode permet de montrer la nouvelle grille avec une animation et un message.
     * @param grid
     * @param errorLabel
     * @param newWordSizeMessage
     * @return void
     * @auhtor: Anthony Genti
     */
	private void showNewGridWithMessage(GridPane grid, Label errorLabel, String newWordSizeMessage) {
	    gridSize++;
	    targetWord = game.getRandomWord(gridSize);

	    FadeTransition fadeInNewGrid = new FadeTransition(Duration.seconds(0.5), grid);
	    fadeInNewGrid.setFromValue(0.0);
	    fadeInNewGrid.setToValue(1.0);
	    fadeInNewGrid.play();

	    fadeInNewGrid.setOnFinished(e -> {
	        resetGame();
	        // Pas besoin de définir une nouvelle scène, mise à jour de la grille existante
	        errorLabel.setText(newWordSizeMessage);
	        errorLabel.setVisible(true);

	        PauseTransition pauseBeforeHidingMessage = new PauseTransition(Duration.seconds(2));
	        pauseBeforeHidingMessage.setOnFinished(ev -> errorLabel.setVisible(false));
	        pauseBeforeHidingMessage.play();
	    });
	}

}
