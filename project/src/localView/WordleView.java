package localView;

import java.util.ArrayList; // Pour utiliser les listes
import java.util.HashMap;   // Pour utiliser les maps
import java.util.List; // Pour utiliser les listes
import java.util.Map; // Pour utiliser les maps
import controller.Score; // Pour utiliser la classe Score
import controller.WordleGame; // Pour utiliser la classe WordleGame
import javafx.animation.Animation; // Pour utiliser les animations
import javafx.animation.FadeTransition; // Pour utiliser les transitions de fondu
import javafx.animation.KeyFrame; // Pour utiliser les images clés
import javafx.animation.ParallelTransition; // Pour utiliser les transitions parallèles
import javafx.animation.PauseTransition; // Pour utiliser les transitions de pause
import javafx.animation.ScaleTransition; // Pour utiliser les transitions d'échelle
import javafx.animation.Timeline; // Pour utiliser les chronologies 
import javafx.animation.TranslateTransition; // Pour utiliser les transitions de translation
import javafx.application.Platform; // Pour utiliser la plateforme
import javafx.geometry.Insets; // Pour utiliser les marges
import javafx.geometry.Pos; // Pour utiliser les positions
import javafx.scene.Node; // Pour utiliser les noeuds
import javafx.scene.Scene; // Pour utiliser les scènes
import javafx.scene.control.Button; // Pour utiliser les boutons
import javafx.scene.control.Label; // Pour utiliser les labels
import javafx.scene.control.RadioButton; // Pour utiliser les boutons radio
import javafx.scene.control.TextField; // Pour utiliser les champs de texte
import javafx.scene.control.ToggleGroup; // Pour utiliser les groupes de bascule
import javafx.scene.layout.BorderPane; // Pour utiliser les panneaux de bordure
import javafx.scene.layout.GridPane; // Pour utiliser les grilles
import javafx.scene.layout.HBox; // Pour utiliser les HBox
import javafx.scene.layout.StackPane; // Pour utiliser les StackPane
import javafx.scene.layout.VBox; // Pour utiliser les VBox
import javafx.scene.paint.Color; // Pour utiliser les couleurs
import javafx.scene.text.Font; // Pour utiliser les polices
import javafx.scene.text.FontWeight; // Pour utiliser les graisses de police
import javafx.stage.Stage; // Pour utiliser les stages
import javafx.stage.StageStyle; // Pour utiliser les styles de stages
import javafx.util.Duration; // Pour utiliser les durées
import javafx.util.Pair; // Pour utiliser les paires 
import model.FrenchWordChecker; // Pour utiliser la classe FrenchWordChecker


public class WordleView implements GameInterface {

	private Stage primaryStage; // On définit la fenêtre principale
	private GamePortal gamePortal; // On définit le portail de jeux
    private BorderPane root;    // On définit le BorderPane pour la mise en page
    private StackPane gamePane; // On définit le StackPane pour le jeu Wordle
    private WordleGame game;   // On définit le jeu Wordle
    private boolean doubleCellSize = false; // On crée un boolean pour la taille des cellules de la grille qui va gérer le changement de taille des cellules
    private String targetWord;  // On définit le mot cible  pour le jeu Wordle
    private Label errorLabel;   // On définit le label d'erreur pour les messages d'erreur
    private Map<String, Button> letterButtonMap = new HashMap<>();
    public int gridSize = 5; // Valeur par défaut pour la taille de la grille

    private Score score;   // On définit le score
    private Label scoreLabel;  // On définit le label du score

    private Label timeLabel;   // On définit le label du temps
    private Timeline timeline; // On définit la chronologie
    private long startTime; // On définit le temps de départ


    /********************************************************************************************
    Configuration initiale et paramètres de la vue
    ********************************************************************************************/
    /**
     * Constructeur WordleView
     * Ce constructeur permet de créer une instance de la classe WordleView.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public WordleView()
    {
    	root = new BorderPane(); // On crée un BorderPane
    	gamePane = new StackPane(); // On crée un StackPane
    }

    /**
     * Constructeur WordleView
     * Ce constructeur permet de créer une instance de la classe WordleView.
     * @param primaryStage : Il s'agit de la fenêtre principale
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public WordleView(Stage primaryStage)
    {
    	this.primaryStage = primaryStage; // On définit la fenêtre principale
        root = new BorderPane(); // On crée un BorderPane 
        gamePane = new StackPane(); // On crée un StackPane
        score = new Score(); // On crée une instance de la classe Score
        scoreLabel = new Label("Score: 0"); // On définit le label du score
        this.timeLabel = new Label("Temps : 00:00:00"); // On définit le label du temps
        setupAndStartTimer(); // On appelle la méthode qui permet de configurer et de démarrer le chronomètre
    }

    /**
     * Méthode setPrimaryStage
     * Cette méthode permet de définir la fenêtre principale.
     * @param primaryStage Il s'agit de la fenêtre principale à définir
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public void setPrimaryStage(Stage primaryStage)
    {
        this.primaryStage = primaryStage; // On définit la fenêtre principale
    }
    
    public Stage getPrimaryStage() {
    	return this.primaryStage;
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
		this.game = game; // On définit le jeu Wordle
	}

	/**
     * Méthode getGame
     * Cette méthode permet de récupérer le jeu Wordle.
     * @return
     */
	public WordleGame getGame()
	{
		return game; // On retourne le jeu Wordle
	}

    /**
     * Méthode initializeGame
     * Cette méthode permet d'initialiser le jeu Wordle.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	public void initializeGame() {
	    this.game = new WordleGame("C:\\Users\\pc\\Downloads\\project\\project\\data\\mots.json", "C:\\Users\\pc\\Downloads\\project\\project\\data\\diccoGeneral.json"); // On crée une instance du jeu Wordle
	}

    /**
     * Méthode setGamePortal
     * Cette méthode permet de définir le portail de jeux.
     * @param gamePortal Il s'agit du portail de jeux à définir
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	public void setGamePortal(GamePortal gamePortal) {
	    this.gamePortal = gamePortal; // On définit le portail de jeux
	}


	/********************************************************************************************
    Gestion du jeu
    ********************************************************************************************/
    /**
     * Méthode showGame
     * Cette méthode permet d'afficher le jeu Wordle.
     * @param stage Il s'agit de la scène à afficher
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */ 
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

    /**
     * Méthode resetGame
     * Cette méthode permet de réinitialiser le jeu Wordle.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    private void resetGame()
    {
        boolean isMaximized = primaryStage.isMaximized(); // On récupère l'état de maximisation de la fenêtre
        double width = primaryStage.getWidth(); // On récupère la largeur de la fenêtre
        double height = primaryStage.getHeight(); // On récupère la hauteur de la fenêtre
        double xPosition = primaryStage.getX(); // On récupère la position x de la fenêtre
        double yPosition = primaryStage.getY(); // On récupère la position y de la fenêtre

        letterButtonMap.clear(); // On vide la map des boutons de lettres
        gamePane.getChildren().clear(); // On vide le StackPane
        gamePane.setStyle("-fx-background-color: black"); // On définit le style du StackPane

        targetWord = game.getRandomWord(gridSize); // On récupère un mot aléatoire pour le jeu Wordle
        setupGameView(root); // On appelle la méthode qui configure la vue du jeu

        Platform.runLater(() -> { // On exécute la logique suivante dans le thread de l'interface utilisateur
            if (isMaximized) { // Si la fenêtre était maximisée
                primaryStage.setMaximized(true); // On maximise la fenêtre
            } else { // Sinon
                primaryStage.setWidth(width); // On définit la largeur de la fenêtre
                primaryStage.setHeight(height); // On définit la hauteur de la fenêtre
                primaryStage.setX(xPosition); // On définit la position x de la fenêtre
                primaryStage.setY(yPosition); // On définit la position y de la fenêtre
            }
        });

        score.reset(); // On réinitialise le score
        updateScoreDisplay(); // On met à jour l'affichage du scores
    }

    /********************************************************************************************
    Interface utilisateur et affichage de la vue
    ********************************************************************************************/
    /**
     * Méthode showWordleGame
     * Cette méthode permet d'afficher le jeu Wordle.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public void showWordleGame() {
        BorderPane newRoot = new BorderPane(); // On crée un nouveau BorderPane
        setupGameView(newRoot); // On appelle la méthode qui configure la vue du jeu

        if (primaryStage.getScene() == null) { // Si la scène n'existe pas
            primaryStage.setScene(new Scene(newRoot, 800, 600)); // On crée une nouvelle scène
        } else { // Sinon
            primaryStage.getScene().setRoot(newRoot); // On définit le contenu de la scène
        }
        primaryStage.show();    // On affiche la fenêtre
    }


    /**
     * Méthode setupGameView
     * Cette méthode permet d'afficher la fenetre de jeu Wordle avec la grille, le clavier virtuel et les boutons et labels.
     * Elle permet aussi de gérer les événements des boutons et de la grille.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    void setupGameView(BorderPane root)
    {
		root.setTop(null); // On définit le haut du BorderPane comme null
		root.setCenter(null); // On définit le centre du BorderPane comme null
		root.setBottom(null); // On définit le bas du BorderPane comme null

//    	targetWord = game.getRandomWord(gridSize); // On récupère un mot aléatoire pour le jeu Wordle
		targetWord = game.getSelectedWord(); // get the selected word of the current game instead of getting a new one
        int wordLength = targetWord.length();  // On récupère la longueur du mot cible
        root.setStyle("-fx-background-color: black"); // On définit le style du BorderPane

        gamePane = new StackPane(); // On crée un StackPane
        gamePane.setStyle("-fx-background-color: black"); // On définit le style du StackPane

        // Grille de jeux
        gridSize = wordLength; // On définit la taille de la grille comme la longueur du mot cible
        GridPane grid = createGrid(gridSize); // On crée une grille de jeu

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
            if (this.gamePortal != null) {
                this.gamePortal.showPortalContent();
            }
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
     * @author: BOUDOUT Youssef
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

    /**
     * Méthode openDifficultySelection
     * Cette méthode permet d'ouvrir la fenêtre de sélection de la difficulté.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
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
     * @author: BOUDOUNT Youssef
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
	 * @author: BOUDOUNT Youssef
	 * source pour GridPane : https://devstory.net/10641/javafx-gridpane
	 * source pour GridPane : http://www.java2s.com/Tutorials/Java/JavaFX/0340__JavaFX_GridPane.htm
	 */
	private GridPane createGrid(int gridSize)
    {
        GridPane grid = new GridPane(); // On crée un GridPane
        grid.setAlignment(Pos.CENTER);  // On aligne le contenu du GridPane au centre
        grid.setAlignment(Pos.CENTER); // On aligne le contenu du GridPane au centre
        grid.setHgap(5);   // On définit l'espacement horizontal entre les cellules de la grille
        grid.setVgap(5);  // On définit l'espacement vertical entre les cellules de la grille

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
	/**
     * Méthode showDifficultySelection
     * Cette méthode permet d'afficher la fenêtre de sélection de la difficulté.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
    public void showDifficultySelection()
	{
		initializeGame();   // On appelle la méthode qui permet d'initialiser le jeu
	    VBox selectionLayout = new VBox(10); // On crée une VBox pour organiser les éléments verticalement avec un espacement de 10 px
	    selectionLayout.setAlignment(Pos.CENTER); // On aligne le contenu de la VBox au centre

	    Label chooseDifficultyLabel = new Label("Choisissez la difficulté"); // On crée un label avec le texte "Choisissez la difficulté"
	    chooseDifficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20)); // On définit la police du label

	    ToggleGroup difficultyGroup = new ToggleGroup(); // On crée un groupe de boutons radio pour les difficultés

	    RadioButton easyButton = new RadioButton("Facile"); // On crée un bouton radio pour la difficulté facile
	    easyButton.setUserData("5");  // On définit les données utilisateur du bouton radio
	    easyButton.setToggleGroup(difficultyGroup); // On ajoute le bouton radio au groupe de boutons radio
	    easyButton.setSelected(true);   // On définit le bouton radio comme sélectionné

	    RadioButton mediumButton = new RadioButton("Moyen");  // On crée un bouton radio pour la difficulté moyenne
	    mediumButton.setUserData("7"); // On définit les données utilisateur du bouton radio
	    mediumButton.setToggleGroup(difficultyGroup); // On ajoute le bouton radio au groupe de boutons radio

	    RadioButton hardButton = new RadioButton("Difficile"); // On crée un bouton radio pour la difficulté difficile
	    hardButton.setUserData("9");  // On définit les données utilisateur du bouton radio
	    hardButton.setToggleGroup(difficultyGroup); // On ajoute le bouton radio au groupe de boutons radio

	    Button validateButton = new Button("Valider"); // On crée un bouton pour valider la difficulté
	    validateButton.setOnAction(e -> {  // On définit un écouteur d'événement pour le bouton de validation
	        this.gridSize = Integer.parseInt(difficultyGroup.getSelectedToggle().getUserData().toString()); // On récupère la taille de la grille à partir du bouton radio sélectionné
	        showWordleGame();  // On appelle la méthode qui permet d'afficher le jeu Wordle
	        setupAndStartTimer();  // On appelle la méthode qui permet de configurer et de démarrer le minuteur
	    });

	    selectionLayout.getChildren().addAll(chooseDifficultyLabel, easyButton, mediumButton, hardButton, validateButton); // On ajoute les éléments à la VBox

	    root.setCenter(selectionLayout); // On définit la VBox comme contenu du centre de la racine

	    Scene scene = primaryStage.getScene(); // On récupère la scène actuelle
	    if (scene == null) {   // Si la scène n'existe pas
	        scene = new Scene(root);   // On crée une nouvelle scène avec la racine
	        primaryStage.setScene(scene); // On définit la scène de la fenêtre principale
	    } else {
	        scene.setRoot(root);   // On définit la racine de la scène
	    }
	    primaryStage.show(); // On affiche la fenêtre principale
	}

    /**
     * Méthode updateScoreDisplay
     * Cette méthode permet de mettre à jour l'affichage du score.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	private void updateScoreDisplay() {
	    scoreLabel.setText("Score: " + score.getScore()); // On définit le texte du label du score avec le score actuel
	}

    /********************************************************************************************
    Gestion du temps
    ********************************************************************************************/
    /**
     * Méthode updateTimeLabel
     * Cette méthode permet de mettre à jour l'affichage du temps écoulé.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	private void updateTimeLabel() { 
	    long now = System.currentTimeMillis(); // On récupère le temps actuel en millisecondes
	    long elapsedMillis = now - this.startTime; // On calcule le temps écoulé en millisecondes
	    long hours = elapsedMillis / 3600000; // On calcule le nombre d'heures écoulées 
	    long minutes = (elapsedMillis % 3600000) / 60000;   // On calcule le nombre de minutes écoulées
	    long seconds = (elapsedMillis % 60000) / 1000; // On calcule le nombre de secondes écoulées
	    this.timeLabel.setText(String.format("Temps : %02d:%02d:%02d", hours, minutes, seconds)); // On définit le texte du label du temps écoulé
	}

    /**
     * Méthode setupAndStartTimer
     * Cette méthode permet de configurer et de démarrer le minuteur.
     * @return void
     * @auhtor: BOUDOUNT Youssef
     */
	private void setupAndStartTimer() {
	    this.startTime = System.currentTimeMillis(); // On récupère le temps actuel en millisecondes
	    this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeLabel())); // On crée une timeline avec un KeyFrame qui appelle la méthode updateTimeLabel toutes les secondes
	    this.timeline.setCycleCount(Animation.INDEFINITE); // On définit le nombre de cycles de la timeline à l'infini
	    this.timeline.play();   // On démarre la timeline
	}

	/********************************************************************************************
    Gestion des aides et des indices
    ********************************************************************************************/
	/**
     * Méthode createGameButton
     * Cette méthode permet de créer un bouton pour un jeu.
     * @return Button
     * @author: BOUDOUNT Youssef
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
     * @author: BOUDOUNT Youssef
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
        // TODO : Ajouter le code de Samy pour l'indice
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
