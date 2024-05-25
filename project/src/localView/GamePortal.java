package localView;

import controller.WordleGame; // Pour utiliser la classe WordleGame
import javafx.application.Platform; // Pour utiliser la classe Platform
import javafx.geometry.Pos; // Pour utiliser la classe Pos
import javafx.scene.Scene; // Pour utiliser les scènes
import javafx.scene.control.Button; // Pour utiliser les boutons
import javafx.scene.control.Label; // Pour utiliser les étiquettes
import javafx.scene.layout.BorderPane; // Pour utiliser les BorderPane
import javafx.scene.layout.VBox; // Pour utiliser les VBox
import javafx.stage.Stage; // Pour utiliser les stages
import javafx.stage.StageStyle; // Pour utiliser les styles de stages
import online.Client; // Pour utiliser la classe Client
import online.Server; // Pour utiliser la classe Server
import onlineView.CreateGameUI; // Pour utiliser la classe CreateGameUI
import onlineView.GameLobbyUI; // Pour utiliser la classe GameLobbyUI
import onlineView.JoinLobbyUI; // Pour utiliser la classe JoinLobbyUI

/**
 * Classe GamePortal
 * Cette classe permet de créer un portail de jeux.
 * author: BOUDOOUNT Youssef
 */
public class GamePortal {
    private Stage primaryStage; // Stage principal pour afficher le portail de jeux
    private BorderPane root;  // Racine de la scène
    private WordleGame game; // Instance du jeu Wordle
    private static WordleGame staticGame; // a static pointer to the game, to allow all the players to access the same wordleGame

    /**
     * Constructeur GamePortal
     * Ce constructeur permet de créer un portail de jeux.
     * @param primaryStage Stage principal pour afficher le portail de jeux
     * @param game Instance du jeu Wordle
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public GamePortal(Stage primaryStage, WordleGame game) {
        this.primaryStage = primaryStage; // Initialisation du stage principal
        this.game = game; // Initialisation de l'instance du jeu Wordle 
        GamePortal.staticGame = game; // initialize the static pointer with the created instance
        initializeStage(); // Initialisation de la fenêtre principale
    }
    
    public Stage getPrimaryStage() {
    	return this.primaryStage;
    }
    
    
    // Static method to get the instantiated game
    public static WordleGame getStaticGame() {
    	return staticGame;
    }

    /**
     * Méthode initializeStage
     * Cette méthode permet d'initialiser la fenetre principale.
     * @return void
     * @author: BOUDOOUNT Youssef
     */
    private void initializeStage() {
        root = new BorderPane(); // Crée un BorderPane pour disposer les éléments
        Scene scene = new Scene(root, 600, 600);  // Crée une scène avec le BorderPane
        if (primaryStage == null) primaryStage = new Stage();
        primaryStage.setTitle("Portail de Jeux"); // Définit le titre de la fenetre
        primaryStage.setScene(scene); // Attribution de la scène à la fenetre
        primaryStage.setMaximized(true); // Maximise la fenetre 
        root.setStyle("-fx-background-color: grey"); // Définit la couleur de fond du BorderPane
        primaryStage.setMinWidth(375); // Définit la largeur minimale de la fenetre
        primaryStage.setMinHeight(700); // Définit la hauteur minimale de la fenetre
        initializeGamePortalContent(); // Initialise le contenu du portail de jeux
    }

    /**
     * Méthode createAndSetCenterContent
     * Cette méthode permet de créer et configurer le contenu du portail de jeux.
     * @return VBox
     * @author: BOUDOOUNT Youssef
     */
    /*private VBox initializeGamePortalContent() {

        root.setStyle("-fx-background-color: black");   // Définit la couleur de fond du Border

        Label titleLabel = new Label("Bienvenue au portail de jeux"); // Crée un titre pour le portail 
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;"); // Définit le style du titre 

        Button wordleSoloButton = createGameButton("Wordle Solo"); // Bouton pour le jeu Wordle en solo
        Button wordleOnlineButton = createGameButton("Wordle Online");  // Bouton pour le jeu Wordle en ligne
        Button otherGamesButton = createGameButton("A propos"); // Bouton pour les autres jeux
        
        
        wordleSoloButton.setOnAction(e -> {
            WordleView wordleView = new WordleView(primaryStage); // Crée une instance de WordleView 
            wordleView.setGame(game); // Définit le jeu Wordle
            wordleView.setGamePortal(this);  // Définit le portail de jeux
            wordleView.showWordleGame(); // Affiche le jeu Wordle
        });
        wordleOnlineButton.setOnAction(e -> displayOnlineOptions()); // Affiche les options en ligne
        otherGamesButton.setOnAction(e -> showPopupMessage("D'autres jeux seront proposés plus tard."));

        VBox buttonBox = new VBox(20, wordleSoloButton, wordleOnlineButton, otherGamesButton); // Crée une VBox pour les boutons
        buttonBox.setAlignment(Pos.CENTER);

        VBox titleAndButtons = new VBox(30, titleLabel, buttonBox); // Crée une VBox pour le titre et les boutons
        titleAndButtons.setAlignment(Pos.CENTER);   // Centre les éléments de la VBox
        root.setCenter(titleAndButtons);    // Définit la VBox comme contenu central du BorderPane
        return titleAndButtons; // Retourne la VBox
    }*/
    
    private VBox initializeGamePortalContent() {
        root.setStyle("-fx-background-color: black;");   // Définit la couleur de fond du Border

     // Titre
        Label titleLabel = new Label("Wordle - Le Défi des Mots");
        titleLabel.setStyle("-fx-font-size: 48px; " // Taille de la police
           + "-fx-font-weight: bold; " // Gras
           + "-fx-text-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, lime 0%, deeppink 100%); " // Gradient de couleur
           + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0.5, 0, 0);" // Ombre portée
           + "-fx-font-family: 'Arial Rounded MT Bold';" // Police arrondie pour un aspect ludique
        );

        // Sous-titre
        Label subtitleLabel = new Label("Testez votre vocabulaire et votre esprit en un nombre limité de tentatives!");
        subtitleLabel.setStyle("-fx-font-size: 20px; "
           + "-fx-text-fill: white; " // Texte blanc
           + "-fx-font-style: italic;" // Italique
        );

        Button wordleSoloButton = createGameButton("Wordle Solo"); // Bouton pour le jeu Wordle en solo
        Button wordleOnlineButton = createGameButton("Wordle Online");  // Bouton pour le jeu Wordle en ligne
        Button otherGamesButton = createGameButton("A propos"); // Bouton pour les autres jeux
        
        wordleSoloButton.setOnAction(e -> {
            WordleView wordleView = new WordleView(primaryStage); // Crée une instance de WordleView 
            wordleView.setGame(game); // Définit le jeu Wordle
            wordleView.setGamePortal(this);  // Définit le portail de jeux
            wordleView.showWordleGame(); // Affiche le jeu Wordle
        });
        wordleOnlineButton.setOnAction(e -> displayOnlineOptions()); // Affiche les options en ligne
        otherGamesButton.setOnAction(e -> showPopupMessage("D'autres jeux seront proposés plus tard."));

        VBox buttonBox = new VBox(20, wordleSoloButton, wordleOnlineButton, otherGamesButton); // Crée une VBox pour les boutons
        buttonBox.setAlignment(Pos.CENTER); // Centre les éléments de la VBox

        VBox titleAndButtons = new VBox(20, titleLabel, subtitleLabel, buttonBox); // Crée une VBox pour le titre et les boutons
        titleAndButtons.setAlignment(Pos.CENTER);   // Centre les éléments de la VBox
        root.setCenter(titleAndButtons);    // Définit la VBox comme contenu central du BorderPane
        return titleAndButtons; // Retourne la VBox
    }


    /**
     * Méthode createGameButton
     * Cette méthode permet de créer un bouton pour un jeu.
     * @param gameName Nom du jeu
     * @return Button
     * author: BOUDOOUNT Youssef
     */
	private Button createGameButton(String gameName) {
	   Button button = new Button(gameName);   // Crée un bouton avec le nom du jeu
	   button.setStyle(
	       "-fx-font-size: 30; " // Taille du texte
	   + "-fx-min-width: 200px; " // Largeur minimale
	   + "-fx-min-height: 100px; " // Hauteur minimale
	   + "-fx-background-color: white; " // Fond blanc
	   + "-fx-text-fill: black; "  // Texte noir
	   + "-fx-font-weight: bold;" // En gras
	   ); // Définit le style du bouton
	   return button;  // Retourne le bouton
	}

    /**
     * Méthode showPopupMessage
     * Cette méthode permet d'afficher un message pop-up.
     * @param message Message à afficher
     * @return void
     * author: BOUDOOUNT Youssef
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
     * Méthode getGame
     * Cette méthode permet de récupérer l'instance du jeu Wordle.
     * @return WordleGame
     * author: BOUDOOUNT Youssef
     */
	public WordleGame getGame() {
		return game; // Retourne l'instance du jeu Wordle
	}

    /**
     * Méthode setGame
     * Cette méthode permet de définir l'instance du jeu Wordle.
     * @param game Instance du jeu Wordle
     * @return void
     * author: BOUDOOUNT Youssef
     */
	public void setGame(WordleGame game) {
		this.game = game;
	}

    /**
     * Méthode showPortalContent
     * Cette méthode permet d'afficher le contenu du portail de jeux.
     * @return void
     * author: BOUDOOUNT Youssef
     */
	public void showPortalContent() {
	    root = new BorderPane(); // Crée un nouveau BorderPane
	    initializeGamePortalContent(); // Reconfigure le contenu du portail
	    Scene scene = primaryStage.getScene();  // Récupère la scène actuelle
	    if (scene == null) { // Vérifie si une scène existe
	        scene = new Scene(root); // Création d'une nouvelle scène si elle n'existe pas
	        primaryStage.setScene(scene);   // Attribution de la scène à la fenetre principale
	    } else {    // Si une scène existe
	        scene.setRoot(root); // Réutilisation de la scène existante
	    }
	    primaryStage.show();    // Affiche la fenetre principale
	}

    /********************************************* 
     * Partie de code pour le jeu en ligne
    **********************************************/
    /**
     * Méthode startServer
     * Cette méthode permet de démarrer un serveur.
     * @param port Port du serveur
     * @return void
     * author: BOUDOOUNT Youssef
     */
	public void startServer(int port) {
	    Thread serverThread = new Thread(() -> { // Crée un nouveau thread pour le serveur
	        Server server = new Server(port, 4, 30, 1);  // Crée une instance du serveur
	        server.startServer();  // Démarre le serveur
	    });
	    serverThread.start();  // Démarre le thread
	    System.out.println("Serveur démarré sur le port " + port); // Affiche un message pour confirmer le démarrage du serveur
	}

    /**
     * Méthode displayOnlineOptions
     * Cette méthode permet d'afficher les options en ligne.
     * @return void
     * author: BOUDOOUNT Youssef
     */
	private void displayOnlineOptions() {
		Label titleLabel = new Label("Jouer en Ligne");
	    titleLabel.setStyle("-fx-font-size: 64px; " // Taille de la police
	    	       + "-fx-font-weight: bold; " // Gras
	    	       + "-fx-text-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, lime 0%, deeppink 100%); " // Gradient de couleur
	    	       + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0.5, 0, 0);" // Ombre portée
	    	       + "-fx-font-family: 'Arial Rounded MT Bold';" // Police arrondie pour un aspect ludique
	    	    );

	    Button createGameButton = createGameButton("Créer une Partie"); // On crée un bouton pour créer une partie

        // Définit l'action à effectuer lors du clic sur le bouton "Créer une Partie"
        createGameButton.setOnAction(e -> {
	        CreateGameUI.setOnLobbyReady(this::switchToLobby); // On définit l'action à effectuer lorsqu'un lobby est prêt
	        VBox createGameContent = CreateGameUI.getContent(primaryStage, this::switchToLobby); // On récupère le contenu de la page de création de partie
	        root.setCenter(createGameContent); // On définit le contenu de la page de création de partie
	    });

	    Button joinLobbyButton = createGameButton("Rejoindre un Lobby"); // On crée un bouton pour rejoindre un lobby

        // Définit l'action à effectuer lors du clic sur le bouton "Rejoindre un Lobby"
	    joinLobbyButton.setOnAction(e -> {
	        JoinLobbyUI.setOnLobbyReady(this::switchToLobby); // On définit l'action à effectuer lorsqu'un lobby est prêt
	        VBox joinLobbyContent = JoinLobbyUI.getContent(primaryStage, this::switchToLobby);  // On récupère le contenu de la page de rejoindre un lobby
	        root.setCenter(joinLobbyContent);   // On définit le contenu de la page de rejoindre un lobby
	    });

	    Button returnButton = createGameButton("Retour");  // On crée un bouton pour retourner au portail de jeux

	    returnButton.setOnAction(e -> showPortalContent()); // Définit l'action à effectuer lors du clic sur le bouton "Retour"

	    VBox optionsBox = new VBox(30, titleLabel, createGameButton, joinLobbyButton, returnButton); // On crée une VBox pour les options
	    optionsBox.setAlignment(Pos.CENTER); // On centre les éléments de la VBox
	    optionsBox.setStyle("-fx-background-color: black;"); // On définit la couleur de fond de la VBox
	    root.setCenter(optionsBox); // On définit la VBox comme contenu central du BorderPane
	}

    /**
     * Méthode switchToLobby
     * Cette méthode permet de passer à un lobby.
     * @param client Client
     * @return void
     * author: BOUDOOUNT Youssef
     */
	public void switchToLobby(Client client) {
	    Platform.runLater(() -> { // Exécute le code dans le thread de l'interface utilisateur
	        GameLobbyUI lobbyUI = new GameLobbyUI(client); // On crée une instance de GameLobbyUI
	        BorderPane lobbyView = lobbyUI.getLobbyView(); // On récupère la vue du lobby
	        root.setCenter(lobbyView); // On définit la vue du lobby comme contenu central du BorderPane
	    });
	}

}

