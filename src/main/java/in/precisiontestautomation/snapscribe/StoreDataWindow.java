package in.precisiontestautomation.snapscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import static in.precisiontestautomation.snapscribe.ExportVBoxContent.exportVBoxContentToWord;

public class StoreDataWindow extends Application {
    private VBox pane;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        pane = new VBox(10);  // VBox with a spacing of 10

        Button saveButton = new Button("Save to Word");
        saveButton.setOnAction(e -> exportVBoxContentToWord(pane,this.stage));
        pane.getChildren().add(saveButton);

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true); // Ensures VBox doesn't get squished horizontally

        Scene scene = new Scene(scrollPane, 640, 480);
        stage.setScene(scene);
        stage.setTitle("Image Paste Example");
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
            container.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10px;");

            // Create and configure a TextInputDialog
            TextInputDialog dialog = new TextInputDialog("Image below:");
            dialog.setTitle("Input Required");
            dialog.setHeaderText("Please enter the text for the image label:");
            dialog.setContentText("Label:");

            // Accessing the dialog's Stage to set properties
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.setAlwaysOnTop(true); // This makes the dialog always appear on top of other applications
            dialogStage.initModality(Modality.APPLICATION_MODAL); // This makes the dialog block input to other windows of the same application


            // Show the dialog and capture the input
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(labelText -> {
                TextField text = new TextField(labelText);
                text.setStyle("-fx-font-size: 14px; -fx-background-color: transparent; -fx-border-width: 0;");
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
        imageView.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

        return imageView;
    }
}

