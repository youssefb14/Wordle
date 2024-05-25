package view;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
