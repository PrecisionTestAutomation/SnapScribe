package in.precisiontestautomation.snapscribe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MultiScreenNumberDisplay extends Application {

    private List<Rectangle> bounds = new ArrayList<>();
    private int selectedScreen = 0;
    private Runnable onConfirmationCallback;
    private final List<Stage> listStages = new ArrayList<>();
    private GraphicsDevice[] screens;

    @Override
    public void start(Stage primaryStage) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        screens = ge.getScreenDevices();

        if(screens.length>1){
            AlertHelper.showAlert("MultiScreen Detected","Please select the screen for which you want to take a screenshot.",10);
        } else {
            bounds.add(screens[0].getDefaultConfiguration().getBounds());
            return;
        }

        for (int i = 0; i < screens.length; i++) {
            final int screenNumber = i + 1;
            bounds.add(screens[i].getDefaultConfiguration().getBounds());

            Text text = new Text(String.valueOf(screenNumber));
            text.setFont(new Font(200));
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.BLUE),
                    new Stop(1, Color.LIGHTBLUE));
            text.setFill(gradient);
            text.setStyle("-fx-background-color: transparent; -fx-focus-color: transparent;");

            StackPane root = new StackPane(text);
            root.setStyle("-fx-background-color: transparent;");

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root, 300, 300);
            scene.setFill(Color.TRANSPARENT);
            stage.setX(bounds.get(i).x + 50);
            stage.setY(bounds.get(i).y + 50);
            stage.setScene(scene);
            stage.show();
            listStages.add(stage);
            text.setOnMouseClicked(event -> {
                selectedScreen = screenNumber - 1;
                showAlert("Screen " + screenNumber + " selected!");
            });
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Screen Selection");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Close the number display
                listStages.forEach(Stage::close);
                // Delay taking the screenshot to ensure the window has closed
                if (onConfirmationCallback != null) {
                    onConfirmationCallback.run();
                }
            }
        });
    }

    public BufferedImage takeScreenshot() {
        try {
            Robot robot = new Robot();
            return robot.createScreenCapture(bounds.get(selectedScreen));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setOnConfirmation(Runnable callback) {
        this.onConfirmationCallback = callback;
    }

    public Integer getScreenCount(){
        return screens.length;
    }
}
