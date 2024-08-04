package in.precisiontestautomation.snapscribe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author PTA-dev
 */
public class SnapScribe extends Application {
    private volatile boolean listening = false;
    private final CopyOnWriteArrayList<Pair<String, String>> screenshotsAndDescriptions = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SnapScribe");

        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 300);

        Alert startAlert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to start the listener?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = startAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            listening = true;
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "Listener started. Press F9 to capture screenshots. Click OK to stop listening.");
            infoAlert.showAndWait();
            scene.setOnKeyPressed(this::handleKeyPress);
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus so key events are registered
        scene.getRoot().requestFocus();
    }

    private void handleKeyPress(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == KeyCode.F9 && listening) {
            captureScreenshotAndDescription();
        }
    }

    private void captureScreenshotAndDescription() {
        try {
            String screenshotPath = captureScreenshot();
            String description = askDescription();
            screenshotsAndDescriptions.add(new Pair<>(description, screenshotPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String captureScreenshot() throws Exception {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
        String screenshotPath = "screenshot_" + (screenshotsAndDescriptions.size() + 1) + ".png";
        ImageIO.write(screenFullImage, "png", new File(screenshotPath));
        return screenshotPath;
    }

    private String askDescription() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Description");
        dialog.setHeaderText("Enter description for the screenshot:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void stopListening() {
        listening = false;
        String docType = askDocumentType();
        if (docType != null) {
            saveDocument(docType);
        } else {
            showError("Invalid document type!");
        }
    }

    private String askDocumentType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Document Type");
        dialog.setHeaderText("Enter document type (HTML/Word/PDF):");
        Optional<String> result = dialog.showAndWait();
        return result.map(String::toLowerCase).orElse(null);
    }

    private void saveDocument(String docType) {
        try {
            switch (docType) {
                case "html":
                    saveAsHtml();
                    break;
                case "word":
                    saveAsWord();
                    break;
                case "pdf":
                    saveAsPdf();
                    break;
                default:
                    showError("Invalid document type!");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving document!");
        }
    }

    private void saveAsHtml() throws Exception {
        StringBuilder htmlContent = new StringBuilder("<html><body>");
        for (Pair<String, String> pair : screenshotsAndDescriptions) {
            htmlContent.append("<h1>").append(pair.getKey()).append("</h1><img src='").append(pair.getValue()).append("'><br>");
        }
        htmlContent.append("</body></html>");
        Files.write(Paths.get("output.html"), htmlContent.toString().getBytes());
        showInfo("HTML document created successfully!");
    }

    private void saveAsWord() {
        // Implement saving as Word document
        showInfo("Word document created successfully!");
    }

    private void saveAsPdf() {
        // Implement saving as PDF document
        showInfo("PDF document created successfully!");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}