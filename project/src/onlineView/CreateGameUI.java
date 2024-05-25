package onlineView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import online.Client;
import online.Server;
import java.util.function.Consumer;
import javafx.scene.control.ListCell;

public class CreateGameUI extends Application {
	private static Consumer<Client> onLobbyReady; // Consommateur pour gerer l'evenment de creation de lobby

	public static void setOnLobbyReady(Consumer<Client> onLobbyReady) {
	    CreateGameUI.onLobbyReady = onLobbyReady;
	}

	@Override
    public void start(Stage primaryStage) {
		primaryStage.setTitle("Créer un Serveur"); // Titre de la fenêtre
		VBox content = getContent(primaryStage, onLobbyReady); // Contenu de la fenêtre
		Scene scene = new Scene(content, 400, 400); // Taille de la fenêtre
		primaryStage.setScene(scene); // Ajout du contenu à la fenêtre 
		primaryStage.show(); // Affichage de la fenêtre 
    }

	private static Client createServer(int port, String playerName, String difficulty, int maxPlayers, int gameDuration, int numberOfWords) {
	    // Création du serveur avec des paramètres configurables
	    Server server = new Server(port, maxPlayers, gameDuration, mapDifficultyToLevel(difficulty));

	    Client client = new Client("localhost", port, playerName, server);

	    new Thread(server::startServer).start(); // Démarrer le serveur

	    Platform.runLater(() -> {
	        try {
	            client.connect(); // Se connecter au serveur
	            if (onLobbyReady != null) {
	                onLobbyReady.accept(client);  // Accepter le client
	            }
	        } catch (Exception e) {
	            e.printStackTrace(); // Afficher l'erreur
	        }
	    });

	    // Appel de updateLobbyConfig directement ici pour que les paramètres de l'UI soient utilisés
	    server.updateLobbyConfig(maxPlayers, gameDuration, mapDifficultyToLevel(difficulty), numberOfWords);

	    return client;
	}

    private static int mapDifficultyToLevel(String difficulty) { 
        switch (difficulty) { // Selon la difficulté
            case "Facile": // Cas de difficulté facile
                return 1; // Niveau de difficulté 1
            case "Moyen":
                return 2;
            case "Difficile":
                return 3;
            default:
                return 1;
        }
    }
    
    public static VBox getContent(Stage primaryStage, Consumer<Client> onLobbyReady) {
        VBox vbox = new VBox(20); // Espacement vertical entre les éléments
        vbox.setAlignment(Pos.CENTER); // Centrer les éléments dans la VBox
        vbox.setPadding(new Insets(50)); // Marges autour de la VBox

        // Style pour les labels
        String labelStyle = "-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;";

        // Création et style du titre
        Label titleLabel = new Label("Créer une Partie en Ligne");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #4facfe, #00f2fe);");
        vbox.getChildren().add(titleLabel);

        // Configuration de la grille pour les entrées utilisateur
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);

        // Ajout des composants dans la grille
        addFormField(grid, "Adresse IP du serveur:", "192.168.60.1", 0, labelStyle);
        addFormField(grid, "Port du serveur:", "12345", 1, labelStyle);
        addFormField(grid, "Votre nom:", "Joueur1", 2, labelStyle);
        addFormField(grid, "Difficulté:", new ComboBox<>(FXCollections.observableArrayList("Facile", "Moyen", "Difficile")), 3, labelStyle);
        addFormField(grid, "Durée de jeu (min):", "30", 4, labelStyle);
        addFormField(grid, "Nombre de mots:", "5", 5, labelStyle);
        addFormField(grid, "Joueurs Max:", "4", 6, labelStyle);

        // Bouton de création du serveur
        Button createServerButton = new Button("Créer le Serveur");
        createServerButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 20px; -fx-min-width: 200px; -fx-min-height: 50px;");
		
        createServerButton.setOnAction(e -> {
			String ip = ((TextField) grid.getChildren().get(1)).getText();
			String portStr = ((TextField) grid.getChildren().get(3)).getText();
			String playerName = ((TextField) grid.getChildren().get(5)).getText();
			@SuppressWarnings("unchecked")
			String difficulty = ((ComboBox<String>) grid.getChildren().get(7)).getValue();
			String gameDurationStr = ((TextField) grid.getChildren().get(9)).getText();
			String numberOfWordsStr = ((TextField) grid.getChildren().get(11)).getText();
			String maxPlayersStr = ((TextField) grid.getChildren().get(13)).getText();

			if (ip.isEmpty() || portStr.isEmpty() || playerName.isEmpty() || difficulty == null
					|| gameDurationStr.isEmpty() || numberOfWordsStr.isEmpty() || maxPlayersStr.isEmpty()) {
				showAlert("Erreur", "Tous les champs doivent être remplis.");
				return;
			}

			if (!ip.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
				showAlert("Erreur", "Adresse IP invalide. Veuillez entrer une adresse IP valide.");
				return;
			}

			if (!portStr.matches("\\d+") || !gameDurationStr.matches("\\d+") || !numberOfWordsStr.matches("\\d+")
					|| !maxPlayersStr.matches("\\d+")) {
				showAlert("Erreur", "Les champs doivent contenir uniquement des chiffres.");
				return;
			}

			int port = Integer.parseInt(portStr);
		    int maxPlayers = Integer.parseInt(maxPlayersStr);
		    int gameDuration = Integer.parseInt(gameDurationStr);
		    int numberOfWords = Integer.parseInt(numberOfWordsStr);

		    Client client = createServer(port, playerName, difficulty, maxPlayers, gameDuration, numberOfWords);
			//System.out.println("CreateGameUI - getContent : Client " + client);
			
			// Mise à jour de la configuration du lobby après la création du client
		    Server server = client.getServer();
		    if (server != null) {
		        server.updateLobbyConfig(maxPlayers, gameDuration, mapDifficultyToLevel(difficulty), numberOfWords);
		    } else {
		        showAlert("Erreur", "Le serveur n'a pas été initialisé correctement.");
		    }
		});

        // Bouton de retour
        Button backButton = new Button("Retour");
        backButton.setStyle(
        		"-fx-background-color: white; "
        		+ "-fx-text-fill: black;"
        		+ " -fx-font-weight: bold; "
        		+ "-fx-font-size: 20px; "
        		+ "-fx-min-width: 200px; "
        		+ "-fx-min-height: 50px;");
        backButton.setOnAction(e -> primaryStage.close());

        // Ajout des boutons à la grille
        HBox buttonBox = new HBox(20, createServerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 7, 2, 1);

        vbox.getChildren().add(grid); // Ajout de la grille à la VBox

        return vbox; // Retour de la VBox complète
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
    
    private static void addFormField(GridPane grid, String labelText, ComboBox<String> comboBox, int rowIndex, String labelStyle) {
        Label label = new Label(labelText);
        label.setStyle(labelStyle);
        comboBox.setItems(FXCollections.observableArrayList("Facile", "Moyen", "Difficile")); // Définir les éléments
        comboBox.setValue("Facile"); // Définir la valeur par défaut à "Facile"
        comboBox.setStyle("-fx-font-size: 20px;"
                + "-fx-background-color: black;"
                + "-fx-text-fill: white;");
        comboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-background-color: black; -fx-text-fill: white;");
                }
            }
        });
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-background-color: black; -fx-text-fill: white;");
                }
            }
        });
        grid.add(label, 0, rowIndex);
        grid.add(comboBox, 1, rowIndex);
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Créer une alerte de type erreur
        alert.setTitle(title); // Ajouter un titre à l'alerte
        alert.setHeaderText(null); // Ajouter un en-tête à l'alerte
        alert.setContentText(message); // Ajouter un contenu à l'alerte
        alert.showAndWait(); // Afficher l'alerte
    }
}

