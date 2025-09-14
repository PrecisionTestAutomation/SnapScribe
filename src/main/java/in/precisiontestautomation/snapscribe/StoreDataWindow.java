package in.precisiontestautomation.snapscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

import static in.precisiontestautomation.snapscribe.ExportVBoxContent.exportVBoxContentToWord;

public class StoreDataWindow extends Application {
    private VBox pane;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        pane = new VBox(15);  // Increased spacing for better layout
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: #f8f9fa;");

        // Header with title
        Label titleLabel = new Label("Captured Screenshots");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        Button saveButton = new Button("Save to Word Document");
        saveButton.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        saveButton.setStyle("-fx-background-color: linear-gradient(to right, #3498db, #2980b9); -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 12px 20px;");
        saveButton.setOnAction(e -> exportVBoxContentToWord(pane, this.stage));

        // Add drop shadow to save button
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(8);
        saveButton.setEffect(shadow);

        pane.getChildren().addAll(titleLabel, saveButton);

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f8f9fa; -fx-background: #f8f9fa;");

        Scene scene = new Scene(scrollPane, 700, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle("SnapScribe - Screenshot Manager");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app_icon.png"))));
    }

    public void displayWindow() {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.show();
                stage.toFront(); // Bring window to front upon showing
            });
        }
    }



    public void pasteImageFromClipboard(Image image) {
        Platform.runLater(() -> {
            if (image != null) {
                VBox container = new VBox(10);
                container.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-padding: 15px; -fx-border-color: #e1e8ed; -fx-border-width: 1px; -fx-border-radius: 8px;");

                // Add drop shadow
                DropShadow shadow = new DropShadow();
                shadow.setOffsetX(0);
                shadow.setOffsetY(2);
                shadow.setColor(Color.web("#000000", 0.1));
                shadow.setRadius(4);
                container.setEffect(shadow);

                // Create and configure a TextInputDialog
                TextInputDialog dialog = new TextInputDialog("Screenshot " + (pane.getChildren().size()));
                dialog.setTitle("Add Screenshot Title");
                dialog.setHeaderText("Enter a title for this screenshot:");
                dialog.setContentText("Title:");

                // Accessing the dialog's Stage to set properties
                Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialogStage.setAlwaysOnTop(true);
                dialogStage.initModality(Modality.APPLICATION_MODAL);

                // Show the dialog and capture the input
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(labelText -> {
                    TextField text = new TextField(labelText);
                    text.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
                    text.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 4px; -fx-border-color: #bdc3c7; -fx-border-radius: 4px; -fx-padding: 8px; -fx-border-width: 1px;");
                    text.setMaxWidth(Double.MAX_VALUE);

                    container.getChildren().addAll(text, createImageView(image));
                    pane.getChildren().add(container);
                });
            }
        });
    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        imageView.setStyle("-fx-background-color: white; -fx-border-color: #e1e8ed; -fx-border-width: 1px; -fx-background-radius: 4px; -fx-border-radius: 4px;");

        // Add subtle shadow to image
        DropShadow imageShadow = new DropShadow();
        imageShadow.setOffsetX(0);
        imageShadow.setOffsetY(2);
        imageShadow.setColor(Color.web("#000000", 0.15));
        imageShadow.setRadius(3);
        imageView.setEffect(imageShadow);

        return imageView;
    }
}

