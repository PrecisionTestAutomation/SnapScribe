package in.precisiontestautomation.snapscribe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
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

        if(screens.length > 1){
            AlertHelper.showAlert("MultiScreen Detected","Please select the screen for which you want to take screenshots by clicking on the screen number.",10);
            
            for (int i = 0; i < screens.length; i++) {
                final int screenNumber = i + 1;
                bounds.add(screens[i].getDefaultConfiguration().getBounds());

                // Create larger, more visible screen number display
                Text text = new Text(String.valueOf(screenNumber));
                text.setFont(new Font(300)); // Larger font for better visibility
                LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.DODGERBLUE),
                        new Stop(0.5, Color.LIGHTBLUE),
                        new Stop(1, Color.CYAN));
                text.setFill(gradient);
                text.setStyle("-fx-background-color: transparent; -fx-focus-color: transparent;");
                
                // Add glow effect for better visibility
                DropShadow glow = new DropShadow();
                glow.setColor(Color.WHITE);
                glow.setRadius(20);
                glow.setSpread(0.5);
                text.setEffect(glow);

                // Create background circle for better contrast
                Circle background = new Circle(150, Color.web("#000000", 0.3));
                background.setStroke(Color.WHITE);
                background.setStrokeWidth(3);

                StackPane root = new StackPane(background, text);
                root.setStyle("-fx-background-color: transparent;");

                Stage stage = new Stage();
                stage.initStyle(StageStyle.TRANSPARENT);
                Scene scene = new Scene(root, 300, 300);
                scene.setFill(Color.TRANSPARENT);
                
                // Center the display on each screen
                Rectangle screenBounds = bounds.get(i);
                stage.setX(screenBounds.x + (screenBounds.width - 300) / 2);
                stage.setY(screenBounds.y + (screenBounds.height - 300) / 2);
                
                stage.setScene(scene);
                stage.show();
                stage.toFront();
                stage.setAlwaysOnTop(true);
                
                listStages.add(stage);
                
                // Make both the text and background clickable
                root.setOnMouseClicked(event -> {
                    selectedScreen = screenNumber - 1;
                    showAlert("Screen " + screenNumber + " selected!");
                });
                
                // Add hover effects
                root.setOnMouseEntered(e -> {
                    background.setFill(Color.web("#4CAF50", 0.5));
                    text.setScaleX(1.1);
                    text.setScaleY(1.1);
                });
                
                root.setOnMouseExited(e -> {
                    background.setFill(Color.web("#000000", 0.3));
                    text.setScaleX(1.0);
                    text.setScaleY(1.0);
                });
            }
        } else {
            // Single screen setup
            bounds.add(screens[0].getDefaultConfiguration().getBounds());
            selectedScreen = 0;
            System.out.println("Single screen detected. Using screen bounds: " + bounds.get(0));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Screen Selection");
        alert.setHeaderText("Screen Selected Successfully!");
        alert.setContentText(message + "\n\nThe floating toolbar will appear next. You can use it to draw on applications and take screenshots.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Close the number display
                listStages.forEach(Stage::close);
                // Execute the confirmation callback
                if (onConfirmationCallback != null) {
                    onConfirmationCallback.run();
                }
            }
        });
    }

    public BufferedImage takeScreenshot() {
        try {
            // Check if we have screen recording permissions (especially important on macOS)
            if (!testScreenCapture()) {
                System.err.println("Screen capture test failed. This may be due to permission issues.");
                return null;
            }
            
            Robot robot = new Robot();
            Rectangle captureArea = bounds.get(selectedScreen);
            
            System.out.println("Taking screenshot of screen " + (selectedScreen + 1) + 
                             " with bounds: " + captureArea.toString());
            
            // Ensure robot is ready
            robot.setAutoDelay(10);
            
            BufferedImage screenshot = robot.createScreenCapture(captureArea);
            
            if (screenshot != null) {
                System.out.println("Screenshot captured successfully - Size: " + 
                                 screenshot.getWidth() + "x" + screenshot.getHeight());
            } else {
                System.err.println("Robot.createScreenCapture returned null");
            }
            
            return screenshot;
        } catch (AWTException e) {
            System.err.println("AWTException while creating Robot: " + e.getMessage());
            System.err.println("On macOS, ensure 'Screen Recording' permission is granted in System Preferences > Security & Privacy > Privacy");
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            System.err.println("Security exception during screenshot - check permissions: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean testScreenCapture() {
        try {
            Robot testRobot = new Robot();
            // Try to capture a small 1x1 pixel area as a test
            Rectangle testArea = new Rectangle(0, 0, 1, 1);
            BufferedImage testImage = testRobot.createScreenCapture(testArea);
            return testImage != null;
        } catch (Exception e) {
            System.err.println("Screen capture test failed: " + e.getMessage());
            return false;
        }
    }

    public void setOnConfirmation(Runnable callback) {
        this.onConfirmationCallback = callback;
    }

    public Integer getScreenCount(){
        return screens.length;
    }
    
    public Rectangle getSelectedScreenBounds() {
        if (bounds != null && selectedScreen < bounds.size()) {
            return bounds.get(selectedScreen);
        }
        // Fallback to default screen
        return screens[0].getDefaultConfiguration().getBounds();
    }
}
