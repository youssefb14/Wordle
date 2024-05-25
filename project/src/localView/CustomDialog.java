package localView;

import javafx.scene.Scene; // Pour utiliser les scènes
import javafx.scene.control.Button; // Pour utiliser les boutons
import javafx.scene.layout.VBox;    // Pour utiliser les VBox
import javafx.scene.paint.Color;    // Pour utiliser les couleurs
import javafx.scene.text.Text;  // Pour utiliser les textes
import javafx.stage.Modality;   // Pour utiliser les modalités
import javafx.stage.Stage;  // Pour utiliser les stages
import javafx.stage.StageStyle; // Pour utiliser les styles de stages


/*
 * Classe CustomDialog
 * Cette classe permet de créer une boîte de dialogue personnalisée.
 * author: BOUDOOUNT Youssef
 */
public class CustomDialog extends Stage {

    public CustomDialog(String title, String message) {
        setTitle(title);
        setWidth(500); // Augmentation de la largeur
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10); // Utilisation d'un VBox pour disposer les éléments verticalement
        layout.setStyle("-fx-background-color: #2E2E2E; -fx-text-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5px; -fx-padding: 10px;");

        Text messageText = new Text(message);
        messageText.setFill(Color.WHITE);

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> close());

        layout.getChildren().addAll(messageText, closeButton);
        layout.setPrefSize(500, 200); // Définition d'une taille préférée pour la VBox
        layout.setMinSize(500, 200); // Définition d'une taille minimale pour la VBox

        Scene scene = new Scene(layout);
        setScene(scene);

        String buttonStyle = "-fx-background-color: #2E2E2E; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5px; -fx-padding: 5px 10px;";
        closeButton.setStyle(buttonStyle);
    }
}
