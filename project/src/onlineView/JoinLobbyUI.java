package onlineView;

import java.util.function.Consumer; // Pour passer des fonctions en tant que paramètres
import javafx.application.Application; // Pour créer une application JavaFX
import javafx.application.Platform; // Pour exécuter du code sur le thread JavaFX
import javafx.geometry.Insets; // Pour définir les marges des éléments
import javafx.geometry.Pos; // Pour définir l'alignement des éléments
import javafx.scene.Scene; // Pour définir le contenu de la fenêtre
import javafx.scene.control.Alert; // Pour afficher des messages d'erreur
import javafx.scene.control.Button; // Pour créer des boutons
import javafx.scene.control.Label; // Pour créer des étiquettes
import javafx.scene.control.TextField; // Pour créer des champs de texte
import javafx.scene.layout.GridPane; // Pour créer une grille
import javafx.scene.layout.VBox; // Pour créer une boîte verticale
import javafx.stage.Stage; // Pour créer une fenêtre
import online.Client; // Pour créer un client qui se connecte au serveur

/*
 * Classe JoinLobbyUI
 * Cette classe permet à un joueur de rejoindre un lobby en entrant l'adresse IP du serveur, le port du serveur et son nom.
 * Tache : 1.1.1
 * @author: BOUDOOUNT Youssef
 */
public class JoinLobbyUI extends Application {
    static TextField serverAddressField = new TextField("127.0.0.1"); // Adresse IP du serveur
    private TextField localServerPortField = new TextField("12345"); // Port du serveur
    private TextField playerNameField = new TextField("Joueur2"); // Nom du joueur
	private Client client;	// Client qui se connecte au serveur
	private static Consumer<Client> onLobbyReady; // Fonction à appeler lorsque le joueur a rejoint le lobby
		
	/**
	 * Setter setOnLobbyReady
	 * Permet de définir la fonction à appeler lorsque le joueur a rejoint le lobby
	 * @param onLobbyReady : Consumer<Client>
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
	public static void setOnLobbyReady(Consumer<Client> onLobbyReady) {
	    JoinLobbyUI.onLobbyReady = onLobbyReady; // On définit la fonction à appeler
	}

	/**
	 * Méthode start
	 * Permet de démarrer l'application JavaFX
	 * @param primaryStage : Stage
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
	@Override
	public void start(Stage primaryStage) {
	    primaryStage.setTitle("Rejoindre un Lobby"); // Titre de la fenêtre
	    VBox content = getContent(primaryStage, onLobbyReady); // Contenu de la fenêtre
	    Scene scene = new Scene(content, 400, 400); // On définit la taille de la fenêtre
	    primaryStage.setScene(scene); // On définit le contenu de la fenêtre
	    primaryStage.show(); // On affiche la fenêtre

	    this.client = new Client(serverAddressField.getText(), Integer.parseInt(localServerPortField.getText()), playerNameField.getText()); // On crée un client
	    this.client.connect(); // On connecte le client au serveur
	    listenForServerMessages();  // On écoute les messages du serveur
	}

	/**
	 * Méthode joinLobby
	 * Permet à un joueur de rejoindre un lobby en entrant l'adresse IP du serveur, le port du serveur et son nom.
	 * @param serverAddress : String
	 * @param playerName : String
	 * @param serverPort : int
	 * @return Client
	 * @autor: BOUDOOUNT Youssef
	 */
	
	private static Client joinLobby(String serverAddress, String playerName, int serverPort) {
	    Client client = new Client(serverAddress, serverPort, playerName); // On crée un client
	    Platform.runLater(() -> { // On exécute le code sur le thread JavaFX
	        try { // On essaie de se connecter au serveur
	            client.connect();  // On connecte le client au serveur
	            client.sendMessage("PLAYER_JOINED");
	            if (onLobbyReady != null) { // Si la fonction à appeler est définie
	                onLobbyReady.accept(client); // On appelle la fonction
	            }
	        } catch (Exception e) { // En cas d'erreur
	            e.printStackTrace(); // On affiche l'erreur
	        }
	    });
	    return client; // On retourne le client
	}

	/**
	 * Méthode getContent
	 * Permet de créer le contenu de la fenêtre pour rejoindre un lobby
	 * @param primaryStage : Stage
	 * @param onLobbyReady : Consumer<Client>
	 * @return VBox
	 * @autor: BOUDOOUNT Youssef
	 */
	public static VBox getContent(Stage primaryStage, Consumer<Client> onLobbyReady) {
	    VBox vbox = new VBox(10); // On crée une boîte verticale avec un espacement entre les éléments
	    vbox.setAlignment(Pos.CENTER); // On centre les éléments de la VBox
	    vbox.setPadding(new Insets(25, 25, 25, 25)); // On définit les marges

	    // Création du titre avec application du style
	    Label titleLabel = new Label("Rejoindre un Lobby");
	    titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #4facfe, #00f2fe);");
	    vbox.getChildren().add(titleLabel); // Ajout du titre à la VBox

	    GridPane grid = new GridPane(); // On crée une grille pour les champs de saisie
	    grid.setAlignment(Pos.CENTER); // Centrage des éléments dans la grille
	    grid.setHgap(10); // Espacement horizontal entre les composants
	    grid.setVgap(10); // Espacement vertical entre les composants
	    grid.setPadding(new Insets(25, 25, 25, 25)); // Marges autour de la grille

	    // Utilisation de la méthode addFormField pour créer et ajouter les champs de formulaire avec styles
	    addFormField(grid, "Port du serveur:", "12345", 0, "-fx-font-size: 14px;");
	    addFormField(grid, "Adresse du serveur:", "127.0.0.1", 1, "-fx-font-size: 14px;");
	    addFormField(grid, "Votre nom:", "Joueur2", 2, "-fx-font-size: 14px;");

	    // Ajout des boutons avec styles et fonctionnalités
	    Button backButton = new Button("Retour");
	    backButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 50px;");
	    backButton.setOnAction(e -> primaryStage.close());
	    grid.add(backButton, 0, 4);

	    Button joinLobbyButton = new Button("Rejoindre le Lobby");
	    joinLobbyButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 50px;");
	    joinLobbyButton.setOnAction(e -> {
	        String serverAddress = ((TextField) grid.getChildren().get(3)).getText();
	        String playerName = ((TextField) grid.getChildren().get(5)).getText();
	        String serverPortStr = ((TextField) grid.getChildren().get(1)).getText();

	        if (serverAddress.isEmpty() || playerName.isEmpty() || serverPortStr.isEmpty()) {
	            showAlert("Erreur", "Tous les champs doivent être remplis.");
	            return;
	        }

	        if (!serverPortStr.matches("\\d+")) {
	            showAlert("Erreur", "Le port du serveur doit être numérique.");
	            return;
	        }

	        int serverPort = Integer.parseInt(serverPortStr);
	        Client client = joinLobby(serverAddress, playerName, serverPort);
	        System.out.println("JoinLobbyUI - getContent(): Client créé et prêt à rejoindre le lobby : " + client);
	    });

	    grid.add(joinLobbyButton, 1, 3);

	    vbox.getChildren().add(grid); // Ajout de la grille à la VBox

	    return vbox; // Retourne la VBox complète
	}

	private static void addFormField(GridPane grid, String labelText, String defaultValue, int rowIndex, String labelStyle) {
        Label label = new Label(labelText);
        label.setStyle(labelStyle);
        TextField textField = new TextField(defaultValue);
        textField.setStyle("-fx-control-inner-background: #000000; "
                + "-fx-text-fill: white; "
                + "-fx-border-color: white; "
                + "-fx-border-width: 1px; "
                + "-fx-font-size: 14px;");
        grid.add(label, 0, rowIndex);
        grid.add(textField, 1, rowIndex);
    }
    
	/**
	 * Méthode showAlert
	 * Permet d'afficher une alerte
	 * @param title : String
	 * @param message : String
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
	private static void showAlert(String title, String message) {
	    Platform.runLater(() -> { // On exécute le code sur le thread JavaFX
	        Alert alert = new Alert(Alert.AlertType.ERROR); // On crée une alerte
	        alert.setTitle(title); // On définit le titre de l'alerte
	        alert.setHeaderText(null); // On définit le texte de l'en-tête de l'alerte
	        alert.setContentText(message); // On définit le contenu de l'alerte
	        alert.showAndWait(); // On affiche l'alerte
	    });
	}

	/**
	 * Méthode main
	 * Permet de lancer l'application JavaFX
	 * @param args : String[]
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
    public static void main(String[] args) {
        launch(args);
    }

	/**
	 * Méthode listenForServerMessages
	 * Permet d'écouter les messages du serveur
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
	private void listenForServerMessages() {
	    new Thread(() -> { // On crée un nouveau thread
	        String message;  // On crée une variable pour stocker les messages
	        while ((message = this.client.readMessage()) != null) { // Tant qu'on reçoit des messages du serveur
	            final String finalMessage = message; // On stocke le message dans une variable finale
	            Platform.runLater(() -> handleServerResponse(finalMessage)); // On exécute le code sur le thread JavaFX
	        } 
	    }).start(); // On démarre le thread
	}

	/**
	 * Méthode handleServerResponse
	 * Permet de gérer la réponse du serveur
	 * @param message : String
	 * @return void
	 * @autor: BOUDOOUNT Youssef
	 */
	private void handleServerResponse(String message) { 
	    if (message.equals("ERROR:NameTaken")) { // Si le nom du joueur est déjà pris
	        showAlert("Erreur", "Ce nom de joueur est déjà pris. Veuillez en choisir un autre."); // On affiche un message d'erreur
	    }
		else if (message.startsWith("NameAccepted")) { // Si le nom du joueur est accepté
	        showAlert("Succès", "Bienvenue dans le lobby!"); // On affiche un message de succès
	    } 
		else { // Si le message n'est pas reconnu
	        System.out.println("Message non reconnu: " + message); // On affiche un message dans la console
	    }
	}
	
}
