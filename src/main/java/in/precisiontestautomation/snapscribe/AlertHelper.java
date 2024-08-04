package in.precisiontestautomation.snapscribe;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * @author PTA-dev
 */
public class AlertHelper {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Show the alert and wait for it to be rendered
        alert.show();

        // Create a pause transition for 2 seconds before starting the fade
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            // Start fading the alert after 2 seconds
            fadeOutAlert(alert);
        });
        delay.play();
    }

    // Method to fade out the alert
    private static void fadeOutAlert(Alert alert) {
        FadeTransition fade = new FadeTransition(Duration.seconds(2), alert.getDialogPane());
        fade.setFromValue(1.0);  // Start fully opaque
        fade.setToValue(0.0);    // Fade to transparent
        fade.setOnFinished(event -> alert.close());  // Close the alert completely after fading
        fade.play();
    }

    public static void showTransparentAlert(String message) {
        Platform.runLater(() -> {
            // Create a new stage with transparent style
            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.TRANSPARENT);

            // Message label with transparent background
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);  // Enable text wrapping
            messageLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-padding: 10px;");
            messageLabel.setFont(new Font("Arial", 20));  // Set font size and family
            messageLabel.setTextFill(Color.BLACK);  // Set font color

            // StackPane as root node with transparent background
            StackPane root = new StackPane(messageLabel);
            root.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");

            // Scene with transparent fill
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            alertStage.setScene(scene);

            // Set stage properties
            alertStage.setAlwaysOnTop(true);
            alertStage.setWidth(300);
            alertStage.setHeight(100);
            alertStage.centerOnScreen();  // Center the stage

            // Show the alert
            alertStage.show();

            // Fade transition setup
            FadeTransition fade = new FadeTransition(Duration.seconds(5), root);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(event -> alertStage.close());

            // Delayed start of the fade effect
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> fade.play());
            delay.play();
        });
    }
}
