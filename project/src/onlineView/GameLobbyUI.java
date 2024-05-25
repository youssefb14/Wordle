package onlineView;

import java.util.Arrays; // Pour utiliser la méthode Arrays.stream()
import java.util.HashMap; // Pour utiliser les HashMap
import java.util.Map;  // Pour utiliser les Maps 
import javafx.application.Application; // Pour utiliser la classe Application
import javafx.application.Platform; // Pour utiliser les plateformes
import javafx.collections.FXCollections; // Pour utiliser les collections FX
import javafx.collections.ObservableList; // Pour utiliser les listes observables
import javafx.geometry.Insets; // Pour utiliser les marges
import javafx.geometry.Pos; // Pour utiliser les positions
import javafx.scene.Node;
import javafx.scene.Scene; // Pour utiliser les scènes
import javafx.scene.control.Button; // Pour utiliser les boutons
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox; // Pour utiliser les ComboBox
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;  // Pour utiliser les labels
import javafx.scene.control.ListView; // Pour utiliser les listes
import javafx.scene.control.TextArea; // Pour utiliser les zones de texte
import javafx.scene.control.TextField; // Pour utiliser les champs de texte
import javafx.scene.layout.BorderPane; // Pour utiliser les BorderPane
import javafx.scene.layout.HBox; // Pour utiliser les HBox
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox; // Pour utiliser les VBox
import javafx.stage.Stage; // Pour utiliser les Stages
import online.Client; // Pour utiliser les clients
import online.GameInfo; // Pour utiliser les informations du jeu
import online.ServerMessageListener; // Pour utiliser les écouteurs de messages du serveur

public class GameLobbyUI extends Application implements ServerMessageListener {
    private ObservableList<String> playerList = FXCollections.observableArrayList();
    private Client client; 
    private ListView<String> listView = new ListView<>(playerList);
    private BorderPane layout; 

    private TextArea chatArea = new TextArea(); 
    private TextField chatInputField = new TextField();
    private Button sendChatButton = new Button("Envoyer"); 
    private Button startGameButton = new Button("Démarrer le Jeu");
    private Button readyButton = new Button("Ready");
    private Button addBotButton = new Button("Add Bot");
    private TextField gameDurationField = new TextField();
    private TextField numberOfWordsField = new TextField(); 
    private ComboBox<String> difficultyComboBox; 
    private Label[] playerSpaces; 

    private Label serverPortLabel = new Label("Port du serveur: N/A"); 
    private Label numberOfPlayersLabel = new Label("Nombre de joueurs: N/A");
    private Label hostNameLabel = new Label("Nom de l'hôte: N/A"); 

    private HBox playerRow1 = new HBox(10); 
    private HBox playerRow2 = new HBox(10); 

    private HBox infoBox; 
    private VBox playerContainer;
    private int maxPlayers; 
    private boolean isHost = false; 

    private Map<String, String> playerColors = new HashMap<>(); 
    private static final String[] COLORS = new String[]{"#FF5733", "#33FF57", "#3357FF", "#F333FF", "#33FFF3", "#F3FF33"};
    
    private Button modifySettingsButton;
    private GameInfo gameInfo;
    
    private Label gameDurationValueLabel;
    private Label numberOfWordsValueLabel;
    private Label difficultyValueLabel;
    private Label maxPlayersLabel;
    
    public GameLobbyUI(Client client) {
        this.client = client; // On initialise le client
        this.client.connect(); // On se connecte au serveur
        
        //this.gameInfo = new GameInfo(maxPlayers, maxPlayers, null, maxPlayers, maxPlayers, maxPlayers, null);
        this.gameInfo = new GameInfo(null, null, "Moyen", null, null, null, client.getPlayerName());


        this.client.listenForServerMessages(); // On écoute les messages du serveur
        this.client.setServerMessageListener(this); // On définit l'écouteur de messages du serveur
        setupUI(); // On initialise l'interface utilisateur
        setupPlayerSpacesUI(); // On initialise les espaces des joueurs
    }
    
    private void handlePlayerListUpdate(String message) {
        String playerListStr = message.substring("PLAYER_LIST_UPDATE:".length());
        String[] playersRaw = playerListStr.split(",");
        String[] playersFiltered = Arrays.stream(playersRaw)
                                         .filter(player -> player != null && !player.isEmpty() && !player.equals("null"))
                                         .distinct()
                                         .toArray(String[]::new);
        Platform.runLater(() -> {
            playerList.clear();
            playerList.addAll(Arrays.asList(playersFiltered));
            // numberOfPlayersLabel.setText("Joueurs Max: " + maxPlayers); // Commenté si non nécessaire
           
            setupPlayerSpaces(maxPlayers);
            updatePlayerSquares(playersFiltered);
        });
    }

    @Override
    public void onServerMessage(String message) {
        Platform.runLater(() -> { // On utilise la plateforme pour mettre à jour l'interface utilisateur
            if (message.startsWith("PLAYER_LIST_UPDATE:")) { // Si le message est une mise à jour de la liste des joueurs
                handlePlayerListUpdate(message); // On gère la mise à jour de la liste des joueurs
            }
            else if (message.startsWith("GAME_INFO:")) { // Si le message est une information du jeu
                handleGameInfoUpdate(message); // On gère l'information du jeu
            }
            else if (message.startsWith("CHAT:")) { // Si le message est un message de chat
            	 String chatMessage = message.substring("CHAT:".length()); // On récupère le message de chat
                 chatArea.appendText(chatMessage + "\n"); // On ajoute le message de chat à la zone de chat
            }
            else if (message.equals("GAME_START")) { // Si le message est un démarrage du jeu
                // On demarre le jeu
            }
            else { // Sinon c"est a dire si le message n'est pas reconnu
                System.out.println("Unrecognized message: " + message); // On affiche un message d'erreur
            }
        });
    }
    
    private void handleGameInfoUpdate(String message) {
        String info = message.substring("GAME_INFO:".length());
        GameInfo gameInfo = GameInfo.fromString(info);
        Platform.runLater(() -> {
            updateGameInfo(gameInfo);
            isHost = gameInfo.getHostName().equals(client.getPlayerName());
            maxPlayers = gameInfo.getMaxPlayers();
            setupPlayerSpaces(maxPlayers);
            updateUIForHost(isHost);
        });
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lobby du Jeu"); // Titre de la fenêtre
        primaryStage.setMinWidth(1600); // Taille minimale de la fenêtre
        primaryStage.setMinHeight(900); // Taille minimale de la fenêtre

        layout = new BorderPane();  // On crée un BorderPane
        layout.setPadding(new Insets(20));  // On définit les marges

        VBox centerContent = new VBox(10); // On crée une VBox
        centerContent.setAlignment(Pos.CENTER); // On définit l'alignement

        listView.setPrefSize(300, 400); // On définit la taille de la liste des joueurs
        centerContent.getChildren().add(listView); // On ajoute la liste des joueurs à la VBox

        startGameButton = new Button("Démarrer le Jeu"); // On crée un bouton de démarrage du jeu
        startGameButton.setOnAction(event -> client.sendMessage("START_GAME"));
        centerContent.getChildren().add(startGameButton); // On ajoute le bouton à la VBox

        layout.setCenter(centerContent); // On ajoute la VBox au centre du BorderPane

        Scene scene = new Scene(layout, 1600, 900); // On crée une scène
        primaryStage.setScene(scene); // On définit la scène
        primaryStage.show(); // On affiche la fenêtre
    }

	public void listenForServerMessages() {
	    new Thread(() -> { // On crée un nouveau thread
	        try { // On gère les exceptions
	            String message; // On crée une variable message
	            while ((message = client.readMessage()) != null) { // Tant qu'il y a un message
	                final String msg = message; // On récupère le message
	                Platform.runLater(() -> { // On utilise la plateforme pour mettre à jour l'interface utilisateur
	                    try {
	                        if (msg.startsWith("PLAYER_LIST_UPDATE:")) { // Si le message est une mise à jour de la liste des joueurs
	                            String playerListStr = msg.substring("PLAYER_LIST_UPDATE:".length()); // On récupère la liste des joueurs
	                            String[] players = playerListStr.split(","); // On sépare les joueurs
	                            updateLobbyUI(players); // On met à jour l'interface utilisateur
	                        } else if (msg.startsWith("CHAT")) { // Si le message est un message de chat
	                            String chatMessage = msg.substring("CHAT".length()).trim(); // On récupère le message de chat
	                            updateChatUI(chatMessage); // On met à jour l'interface utilisateur
	                        } else { // Sinon c'est à dire si le message n'est pas reconnu
	                            System.out.println("Unrecognized message: " + msg); // On affiche un message d'erreur
	                        }
	                    } catch (Exception e) { // On gère les exceptions
	                        System.err.println("Error processing server message: " + e.getMessage()); // On affiche un message d'erreur
	                    }
	                });
	            }
	        } catch (Exception e) { // On gère les exceptions
	            System.err.println("Error in message listening thread: " + e.getMessage()); // On affiche un message d'erreur
	        }
	    }).start();
	}

	private void updateChatUI(String chatMessage) {
	    String playerName = chatMessage.split(":")[0].trim(); // On récupère le nom du joueur
	    String message = chatMessage.substring(chatMessage.indexOf(':') + 1).trim(); // On récupère le message
	    String color = playerColors.getOrDefault(playerName, "white"); // On récupère la couleur du joueur
	    chatArea.appendText(String.format("[%s]: %s\n", playerName, message)); // On ajoute le message à la zone de chat
	    chatArea.setStyle("-fx-text-fill: " + color + ";"); // On définit la couleur du texte
	}

    private void updateLobbyUI(String[] players) {
        Platform.runLater(() -> { // On utilise la plateforme pour mettre à jour l'interface utilisateur
            for (Label playerSpace : playerSpaces) { // Pour chaque espace de joueur
                playerSpace.setText(""); // On vide l'espace de joueur
            }

            for (int i = 0; i < players.length && i < playerSpaces.length; i++) { // Pour chaque joueur
                playerSpaces[i].setText(players[i]); // On ajoute le joueur à l'espace de joueur
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    public BorderPane getContent() {
        return layout;
    }

    public BorderPane getLobbyView() {
        return getContent();
    }
    
    public void updateGameInfo(GameInfo gameInfo) {
        Platform.runLater(() -> {
            if (gameInfo.getServerPort() != null) {
                serverPortLabel.setText("Port du serveur: " + gameInfo.getServerPort());
            }
            if (gameInfo.getNumberOfPlayers() != null) {
                numberOfPlayersLabel.setText("Nombre de joueurs: " + gameInfo.getNumberOfPlayers());
            }
            if (gameInfo.getHostName() != null) {
                hostNameLabel.setText("Nom de l'hôte: " + gameInfo.getHostName());
            }
            if (gameInfo.getDifficulty() != null) {
                difficultyValueLabel.setText("Difficulté: " + gameInfo.getDifficulty());
            }
            if (gameInfo.getGameDuration() != null) {
                gameDurationValueLabel.setText("Durée de la partie: " + gameInfo.getGameDuration() + " min");
            }
            if (gameInfo.getNumberOfWords() != null) {
                numberOfWordsValueLabel.setText("Nombre de mots par joueur: " + gameInfo.getNumberOfWords());
            }
            if (gameInfo.getMaxPlayers() != null) {
                maxPlayers = gameInfo.getMaxPlayers();
                maxPlayersLabel.setText("Joueurs Max: " + maxPlayers);
                setupPlayerSpaces(maxPlayers);
            }
        });
    }

    private void setupPlayerSpaces(int numberOfPlayers) { 
        playerRow1.getChildren().clear(); // On vide la ligne des joueurs 1
        playerRow2.getChildren().clear(); // On vide la ligne des joueurs 2
        playerSpaces = new Label[numberOfPlayers]; // On crée un tableau de labels pour les joueurs

        for (int i = 0; i < numberOfPlayers; i++) { // Pour chaque joueur
            playerSpaces[i] = new Label("En attente..."); // On crée un label pour le joueur
            playerSpaces[i].setAlignment(Pos.CENTER); // On définit l'alignement
            playerSpaces[i].setStyle("-fx-border-color: black; -fx-background-color: white; -fx-alignment: center;"); // On définit le style
            playerSpaces[i].setMinSize(150, 150); // On définit la taille minimale
            if (i < numberOfPlayers / 2) { // Si le joueur est dans la première moitié
                playerRow1.getChildren().add(playerSpaces[i]);  // On ajoute le joueur à la ligne 1
            } else { // Sinon
                playerRow2.getChildren().add(playerSpaces[i]); // On ajoute le joueur à la ligne 2
            }
        }
        playerContainer = new VBox(10, playerRow1, playerRow2); // On crée un conteneur pour les joueurs
        playerContainer.setAlignment(Pos.CENTER); // On définit l'alignement

        layout.setCenter(playerContainer); // On ajoute le conteneur au centre de la mise en page

        playerContainer.widthProperty().addListener((observable, oldValue, newValue) -> { // On écoute les changements de la largeur du conteneur
            double width = playerContainer.getWidth(); // On récupère la largeur du conteneur
            double squareSize = Math.max(width / (numberOfPlayers / 2), 150); // On calcule la taille des carrés
            for (Label playerSpace : playerSpaces) { // Pour chaque espace de joueur
                playerSpace.setPrefSize(squareSize, squareSize); // On définit la taille préférée
            }
        });

        playerContainer.heightProperty().addListener((observable, oldValue, newValue) -> { // On écoute les changements de la hauteur du conteneur
            double height = playerContainer.getHeight(); // On récupère la hauteur du conteneur
            double squareSize = Math.max(height / 2, 150); // On calcule la taille des carrés
            for (Label playerSpace : playerSpaces) { // Pour chaque espace de joueur
                playerSpace.setPrefSize(squareSize, squareSize); // On définit la taille préférée
            }
        });
    }

    public void updatePlayerSquares(String[] players) {
        Platform.runLater(() -> {
            // Make sure the array is not accessed out of its bounds
            for (int i = 0; i < playerSpaces.length; i++) {
                if (i < players.length) {
                    playerSpaces[i].setText(players[i] != null ? players[i] : "En attente...");
                    if (players[i] != null && !players[i].isEmpty()) {
                        String color = playerColors.computeIfAbsent(players[i], k -> COLORS[playerColors.size() % COLORS.length]);
                        playerSpaces[i].setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-alignment: center; -fx-font-size: 16px; -fx-font-weight: bold;");
                    } else {
                        playerSpaces[i].setStyle("-fx-background-color: white; -fx-border-color: black; -fx-alignment: center; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupUI() {
        layout = new BorderPane(); // On crée un BorderPane
        layout.setPadding(new Insets(20)); // On définit les marges
 
        setupChatUI(); // On initialise le chat
        setupButtonUI(); // On initialise les boutons
        setupGameInfoUI();  // On initialise les informations du jeu
        setupPlayerSpacesUI(); // On initialise les espaces des joueurs

        VBox topSection = new VBox(10, infoBox); // On crée une VBox pour les informations
        topSection.setAlignment(Pos.CENTER); // On définit l'alignement

        VBox centerSection = new VBox(10, playerContainer); // On crée une VBox pour les joueurs
        centerSection.setAlignment(Pos.CENTER); // On définit l'alignement

        layout.setTop(topSection); // On ajoute les informations en haut
        layout.setCenter(centerSection); // On ajoute les joueurs au centre

        if (isHost) { // Si l'utilisateur est l'hôte
            difficultyComboBox.setOnAction(event -> sendGameConfiguration()); // On envoie la configuration du jeu
            gameDurationField.setOnKeyReleased(event -> sendGameConfiguration()); // On envoie la configuration du jeu
            numberOfWordsField.setOnKeyReleased(event -> sendGameConfiguration()); // On envoie la configuration du jeu
        }
        
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.setItems(FXCollections.observableArrayList("Facile", "Moyen", "Difficile"));
        difficultyComboBox.setValue("Moyen");  // Définir une valeur par défaut
        
     // Marges pour le chat pour s'assurer qu'il ne touche pas directement les côtés ou le top
        BorderPane.setMargin(chatArea, new Insets(10, 10, 10, 10));
    }

    private void setupChatUI() {
        chatArea = new TextArea(); // On crée une zone de texte pour le chat
        chatArea.setEditable(false);  // On définit la zone de texte comme non modifiable
        chatArea.setPrefHeight(200);  // On définit la hauteur préférée
        chatArea.setStyle("-fx-control-inner-background: #000000; "  // Fond noir
                + "-fx-text-fill: white; "  // Texte blanc pour la lisibilité
                + "-fx-font-weight: bold; "  // Texte en gras 
                + "-fx-font-size: 16px; "  // Taille du texte
                + "-fx-border-color: white; "  // Bordure blanche
                + "-fx-border-width: 1px;");  // Largeur de la bordure

		chatInputField = new TextField(); // On crée un champ de saisie pour le chat
		chatInputField.setPromptText("Écrivez votre message ici...");  // Texte d'invite
		chatInputField.setStyle("-fx-control-inner-background: #000000; "  // Fond noir pour la zone de texte
		                      + "-fx-text-fill: white; "  // Couleur du texte tapé
		                      + "-fx-prompt-text-fill: white; "  // Couleur du texte d'invite
		                      + "-fx-border-color: white; "  // Bordure blanche
		                      + "-fx-border-width: 1px; "  // Largeur de la bordure
		                      + "-fx-font-size: 14px;");  // Taille du texte

		sendChatButton = new Button("Envoyer"); // On crée un bouton d'envoi du chat
		sendChatButton.setStyle("-fx-background-color: white; "  // Fond blanc
		                      + "-fx-text-fill: black; "  // Texte noir
		                      + "-fx-font-weight: bold;");  // En gras

        sendChatButton.setOnAction(event -> { // On définit l'action du bouton
            String message = chatInputField.getText().trim(); // On récupère le message
            if (!message.isEmpty()) { // Si le message n'est pas vide
                client.sendChatMessage(message); // On envoie le message
                chatInputField.clear(); // On vide le champ de saisie
            }
        });

        HBox chatInputBox = new HBox(5, chatInputField, sendChatButton); // On crée une boîte de saisie pour le chat
        chatInputBox.setAlignment(Pos.CENTER); // On définit l'alignement
        chatInputBox.setPadding(new Insets(10)); // On définit les marges

        BorderPane chatLayout = new BorderPane(); // On crée un BorderPane pour le chat
        chatLayout.setCenter(chatArea); // On ajoute la zone de texte au centre
        chatLayout.setBottom(chatInputBox); // On ajoute la boîte de saisie en bas

        chatLayout.prefHeightProperty().bind(layout.heightProperty()); // On définit la hauteur préférée
        chatLayout.prefWidthProperty().bind(layout.widthProperty().divide(3)); // On définit la largeur préférée

        layout.setRight(chatLayout); // On ajoute le chat à droite
    }

    boolean isReady;
    private void setupButtonUI() {
        startGameButton = new Button("Démarrer le Jeu");
        startGameButton.setOnAction(event -> client.sendMessage("START_GAME"));
        
        startGameButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");

        Button quitGameButton = new Button("Quitter le lobby");
        quitGameButton.setOnAction(event -> {
            client.sendMessage("QUIT_GAME");
            Platform.exit();
        });
        
        quitGameButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        readyButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        isReady = true;
        readyButton.setOnAction(event -> {
	    	// Change the style of the button based on the player's state
	    	isReady = !isReady; // Change the state of the player after each click
	    	if (isReady == false) readyButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
	    	else readyButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
	    	
	    	// State change message
	    	client.sendMessage("PLAYER_READY");
    	});
        
        addBotButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 16px;");
        
        addBotButton.setOnAction(event -> client.sendMessage("ADD_BOT"));
        
        // Création d'un bouton pour modifier les paramètres ici pour assurer l'uniformité de la visibilité
        setupModifySettingsButton();

        // HBox pour aligner les boutons de jeu
        HBox gameButtonBox = new HBox(10, addBotButton, startGameButton, quitGameButton, readyButton);
        gameButtonBox.setAlignment(Pos.CENTER);

        // VBox pour empiler les boutons de modification des paramètres sous les boutons de jeu
        VBox buttonBox = new VBox(10, gameButtonBox, modifySettingsButton);
        buttonBox.setPadding(new Insets(20, 20, 20, 20));
        buttonBox.setAlignment(Pos.CENTER);

        layout.setBottom(buttonBox); // Ajouter la boîte de boutons en bas du layout
        startGameButton.setStyle( // On définit le style du bouton de démarrage du jeu
        		"-fx-background-color: white; " // Fond blanc
                + "-fx-text-fill: black; "  // Texte noir
                + "-fx-font-weight: bold;"  // En gras
        		+ "-fx-font-size: 16px; "  // Taille du texte
        		);

        quitGameButton.setStyle( // On définit le style du bouton de quitter le lobby
        		"-fx-background-color: white; " // Fond blanc
                + "-fx-text-fill: black; "  // Texte noir
                + "-fx-font-weight: bold;"  // En gras
        		+ "-fx-font-size: 16px; "  // Taille du texte
        		);
    }

    private void setupGameInfoUI() {
        String labelStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;";

        serverPortLabel = new Label("Port du serveur: N/A");
        serverPortLabel.setStyle(labelStyle);
        numberOfPlayersLabel = new Label("Nombre de joueurs: N/A");
        numberOfPlayersLabel.setStyle(labelStyle);
        hostNameLabel = new Label("Nom de l'hôte: N/A");
        hostNameLabel.setStyle(labelStyle);

        difficultyValueLabel = new Label("Difficulté: N/A");
        difficultyValueLabel.setStyle(labelStyle);
        gameDurationValueLabel = new Label("Durée de la partie (min): N/A");
        gameDurationValueLabel.setStyle(labelStyle);
        numberOfWordsValueLabel = new Label("Nombre de mots par joueur: N/A");
        numberOfWordsValueLabel.setStyle(labelStyle);
        
        maxPlayersLabel = new Label("Joueurs Max: N/A");
        maxPlayersLabel.setStyle(labelStyle);

        VBox fixedInfoBox = new VBox(10, serverPortLabel, numberOfPlayersLabel, hostNameLabel);
        fixedInfoBox.setAlignment(Pos.CENTER);
        fixedInfoBox.setPadding(new Insets(5));

        VBox gameInfoValuesBox = new VBox(10, difficultyValueLabel, gameDurationValueLabel, numberOfWordsValueLabel);
        gameInfoValuesBox.setAlignment(Pos.CENTER);

        setupModifySettingsButton();  // Initialise le bouton ici pour s'assurer qu'il est configuré dès le départ.
        
        HBox infoLayout = new HBox(20, fixedInfoBox, gameInfoValuesBox, modifySettingsButton);
        infoLayout.setAlignment(Pos.CENTER);
        infoLayout.setAlignment(Pos.CENTER);
        infoLayout.setStyle("-fx-border-color: white; -fx-border-width: 2px; -fx-background-color: #333333; -fx-padding: 10;");
        
        if (isHost) {
            setupModifySettingsButton();
        }
        
        infoBox = infoLayout;
        layout.setLeft(infoBox);
    }
    
    private void setupModifySettingsButton() {
        modifySettingsButton = new Button("Modifier paramètres");
        modifySettingsButton.setOnAction(event -> showSettingsDialog());
        modifySettingsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        modifySettingsButton.setVisible(isHost);  // Visible seulement pour l'hôte
    }

    private void showSettingsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifier les Paramètres du Jeu");
        dialog.getDialogPane().setStyle("-fx-background-color: black;");

        ComboBox<String> difficultyField = new ComboBox<>(FXCollections.observableArrayList("Facile", "Moyen", "Difficile"));
        difficultyField.setValue(gameInfo.getDifficulty());
        TextField gameDurationField = new TextField(gameInfo.getGameDuration() != null ? gameInfo.getGameDuration().toString() : "");
        TextField numberOfWordsField = new TextField(gameInfo.getNumberOfWords() != null ? gameInfo.getNumberOfWords().toString() : "");

        gameDurationField.setStyle("-fx-control-inner-background: black; -fx-text-fill: white; -fx-border-color: white;");
        numberOfWordsField.setStyle("-fx-control-inner-background: black; -fx-text-fill: white; -fx-border-color: white;");
        difficultyField.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        VBox dialogVBox = new VBox(10,
            new HBox(5, new Label("Difficulté:"), difficultyField),
            new HBox(5, new Label("Durée de la partie (min):"), gameDurationField),
            new HBox(5, new Label("Nombre de mots par joueur:"), numberOfWordsField)
        );
        dialogVBox.setStyle("-fx-background-color: black; -fx-text-fill: white;");

        for (Node child : dialogVBox.getChildren()) {
            HBox hbox = (HBox) child;
            for (Node innerChild : hbox.getChildren()) {
                if (innerChild instanceof Label) {
                    innerChild.setStyle("-fx-text-fill: white;");
                }
            }
        }

        dialog.getDialogPane().setContent(dialogVBox);
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                gameInfo.update(
                    difficultyField.getValue(),
                    gameDurationField.getText(),
                    numberOfWordsField.getText()
                );
                updateGameInfo(gameInfo);
                sendGameConfiguration();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void setupPlayerSpacesUI() {
        playerRow1.getChildren().clear();
        playerRow2.getChildren().clear();

        playerSpaces = new Label[maxPlayers]; // Redéfinition des labels des joueurs
        Button[] removeButtons = new Button[maxPlayers]; // Tableau pour les boutons de suppression

        for (int i = 0; i < maxPlayers; i++) {
            playerSpaces[i] = new Label("Aucun joueur");
            playerSpaces[i].setMinSize(150, 150);
            playerSpaces[i].setStyle("-fx-border-color: black; -fx-background-color: lightgray; -fx-alignment: center;");

            StackPane stack = new StackPane();
            stack.getChildren().add(playerSpaces[i]);

            if (isHost) {
                removeButtons[i] = new Button("X");
                removeButtons[i].setStyle("-fx-background-color: red; -fx-text-fill: white;");
                removeButtons[i].setMinSize(20, 20); // Assurez-vous que le bouton est visible et bien dimensionné
                int playerIndex = i;
                removeButtons[i].setOnAction(e -> removePlayer(playerIndex));
                stack.getChildren().add(removeButtons[i]);
                StackPane.setAlignment(removeButtons[i], Pos.TOP_RIGHT);
            }

            if (i < maxPlayers / 2) {
                playerRow1.getChildren().add(stack); // Ajout du StackPane et non du label seul
            } else {
                playerRow2.getChildren().add(stack); // Ajout du StackPane et non du label seul
            }
        }

        playerContainer = new VBox(10, playerRow1, playerRow2);
        playerContainer.setAlignment(Pos.CENTER);
        
        playerContainer.setPadding(new Insets(10, 20, 20, 20));
        layout.setCenter(playerContainer);
    }

    private void removePlayer(int playerIndex) {
        String playerName = playerSpaces[playerIndex].getText();
        client.sendMessage("REMOVE_PLAYER:" + playerName);
    }

    private void updateUIForHost(boolean isHost) {
        this.isHost = isHost;
        startGameButton.setVisible(isHost);  // Le bouton démarrer le jeu est visible seulement pour l'hôte.
        startGameButton.setDisable(!isHost);  // Désactive le bouton si l'utilisateur n'est pas l'hôte.
        
        if (isHost == true) {
        	readyButton.setVisible(false);
        	readyButton.setDisable(true);
        }
        
        addBotButton.setVisible(isHost);
    	addBotButton.setDisable(!isHost);
        
        if (modifySettingsButton != null) {
            modifySettingsButton.setVisible(isHost);  // Affiche le bouton de modification des paramètres seulement pour l'hôte.
        }
    }

    private void sendGameConfiguration() {
        if (!isHost) return;  // Seul l'hôte peut modifier les configurations

        String configMessage = String.format("CONFIG_UPDATE:difficulty=%s;duration=%s;words=%s;maxPlayers=%s;",
            difficultyComboBox.getValue(),
            gameDurationField.getText(),
            numberOfWordsField.getText(),
            maxPlayersLabel.getText().replace("Joueurs Max: ", ""));

        client.sendMessage(configMessage);  // Envoyer la configuration mise à jour au serveur
    }


}
