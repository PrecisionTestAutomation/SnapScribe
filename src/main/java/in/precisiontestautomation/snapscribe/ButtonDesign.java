package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.util.Objects;

import static in.precisiontestautomation.snapscribe.AlertHelper.showAlert;
import static in.precisiontestautomation.snapscribe.AlertHelper.showTransparentAlert;

public class ButtonDesign extends Application {
    GlobalKeyListener keyListener;
    public static StoreDataWindow storeDataWindow;

    @Override
    public void start(Stage primaryStage) throws NativeHookException {
        Image applicationIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app_icon.png")));
        primaryStage.getIcons().add(applicationIcon);
        // Creating buttons
        Button playButton = new Button("▶");
        Button stopButton = new Button("■");

        // Creating a help button with a circle
        Circle helpCircle = new Circle(15, Color.LIGHTGRAY);
        Text helpText = new Text("?");
        StackPane helpButton = new StackPane();
        helpButton.getChildren().addAll(helpCircle, helpText);
        helpButton.setPadding(new Insets(5));
        helpButton.setAlignment(Pos.CENTER);
        helpText.setFont(Font.font(14));

        // Setting tooltips (optional)
        playButton.setTooltip(new Tooltip("Play"));
        stopButton.setTooltip(new Tooltip("Stop"));
        Tooltip helpTooltip = new Tooltip("Help");
        Tooltip.install(helpButton, helpTooltip);

        // Styling buttons
        String buttonStyle = "-fx-font-size: 14px; -fx-background-color: #e0e0e0; -fx-background-radius: 5px; -fx-padding: 5px;";
        playButton.setStyle(buttonStyle);
        stopButton.setStyle(buttonStyle);

        // Applying shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.GRAY);

        playButton.setEffect(shadow);
        stopButton.setEffect(shadow);
        helpButton.setEffect(shadow);

        // Creating a horizontal box layout
        HBox hbox = new HBox(10); // Spacing of 10 between buttons
        hbox.setPadding(new Insets(10)); // Padding around the box
        hbox.setAlignment(Pos.CENTER); // Center alignment
        hbox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: lightgray;");

        hbox.getChildren().addAll(playButton, stopButton, helpButton);

        // Setting up the scene and stage
        Scene scene = new Scene(hbox, 180, 70, Color.WHITESMOKE);
        primaryStage.setTitle("SnapScribe");
        primaryStage.setScene(scene);
        primaryStage.show();
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        // Adding action to play button to start global key listener
        playButton.setOnAction(event -> startGlobalListener(primaryStage));
        stopButton.setOnAction(event -> {
            if (storeDataWindow != null) {
                storeDataWindow.displayWindow(); // This line will display the current data
                storeDataWindow = null;
            } else {
                showAlert("Screenshot Listener Not Started", "Please start the listener before stopping.");
            }
        });

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            stopGlobalKeyListener();
            Platform.exit();
            System.exit(0);
        });
    }

    private void stopGlobalKeyListener() {
        try {
            GlobalScreen.removeNativeKeyListener(keyListener);
            GlobalScreen.unregisterNativeHook();
            keyListener = null;  // Clear the reference
        } catch (NativeHookException ex) {
            System.err.println("There was a problem unregistering the native hook.");
            ex.printStackTrace();
        }
    }

    private void startGlobalListener(Stage primaryStage) {
        primaryStage.setIconified(true);
        if (storeDataWindow == null) {
            storeDataWindow = new StoreDataWindow();
            Stage storeStage = new Stage();
            storeDataWindow.start(storeStage);
            showTransparentAlert("Screenshot listener started. Click F9 to take a screenshot.");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

