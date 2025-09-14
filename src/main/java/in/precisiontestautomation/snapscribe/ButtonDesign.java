package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

import static in.precisiontestautomation.snapscribe.AlertHelper.showAlert;
import static in.precisiontestautomation.snapscribe.AlertHelper.showTransparentAlert;

public class ButtonDesign extends Application {
    GlobalKeyListener keyListener;
    public static StoreDataWindow storeDataWindow;
    public static MultiScreenNumberDisplay multiScreenNumberDisplay;
    private static FloatingToolbar floatingToolbar;

    public static FloatingToolbar getFloatingToolbar() {
        return floatingToolbar;
    }

    private Label statusLabel;
    private Circle statusIndicator;
    private Button playButton;
    private Button stopButton;

    @Override
    public void start(Stage primaryStage) throws NativeHookException {
        Image applicationIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app_icon.png")));
        primaryStage.getIcons().add(applicationIcon);

        // Create main layout with enhanced background
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa 0%, #ffffff 30%, #f5f7fa 70%, #f1f3f4 100%);");

        // Add subtle background pattern
        Rectangle backgroundPattern = new Rectangle(400, 280);
        backgroundPattern.setFill(Color.web("#ffffff", 0.02));
        backgroundPattern.setStroke(Color.web("#e1e8ed", 0.1));
        backgroundPattern.setStrokeWidth(1);
        backgroundPattern.setStrokeType(StrokeType.INSIDE);

    mainLayout.getChildren().add(backgroundPattern);
    backgroundPattern.toBack();

        // Create header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Create control panel
        VBox controlPanel = createControlPanel();
        mainLayout.setCenter(controlPanel);

        // Create status bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);

        // Setting up the scene and stage
        Scene scene = new Scene(mainLayout, 400, 280);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        primaryStage.setTitle("SnapScribe - Professional Screenshot Tool");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            
            // Clean up floating toolbar
            if (floatingToolbar != null) {
                floatingToolbar.hide();
            }
            
            stopGlobalKeyListener();
            Platform.exit();
            System.exit(0);
        });
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25, 25, 15, 25));
        header.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); -fx-background-radius: 0 0 20 20;");

        // App icon with glow effect
        ImageView iconView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/app_icon.png"))));
        iconView.setFitWidth(56);
        iconView.setFitHeight(56);

        // Add glow effect to icon
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#ffffff", 0.6));
        glow.setRadius(15);
        glow.setSpread(0.3);
        iconView.setEffect(glow);

        // App title with better typography
        Label titleLabel = new Label("SnapScribe");
        titleLabel.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(2, Color.web("#000000", 0.3)));

        // Subtitle with modern styling
        Label subtitleLabel = new Label("Professional Screenshot Capture");
        subtitleLabel.setFont(Font.font("SF Pro Text", FontWeight.NORMAL, 13));
        subtitleLabel.setTextFill(Color.web("#e8f4f8"));
        subtitleLabel.setOpacity(0.9);

        header.getChildren().addAll(iconView, titleLabel, subtitleLabel);
        return header;
    }

    private VBox createControlPanel() {
        VBox controlPanel = new VBox(30);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(25, 40, 30, 40));

        // Create buttons with modern styling and better spacing
        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

    playButton = createModernButton("START", "#10b981", "#059669", "Start capturing screenshots");
    playButton.getStyleClass().add("start-button");
        playButton.setOnAction(event -> startGlobalListener(playButton.getScene().getWindow()));

        // Add visual separator
        Region separator = new Region();
        separator.setPrefWidth(2);
        separator.setPrefHeight(40);
        separator.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-background-radius: 1;");

    stopButton = createModernButton("STOP", "#ef4444", "#dc2626", "Stop and save screenshots");
    stopButton.getStyleClass().add("stop-button");
        stopButton.setDisable(true);
        stopButton.setOnAction(event -> {
            if (storeDataWindow != null) {
                // Hide floating toolbar
                if (floatingToolbar != null) {
                    floatingToolbar.hide();
                }
                
                storeDataWindow.displayWindow();
                storeDataWindow = null;
                updateStatus(false);
            } else {
                showAlert("Screenshot Listener Not Started", "Please start the listener before stopping.", 1);
            }
        });

        Button helpButton = createModernHelpButton();

        buttonBox.getChildren().addAll(playButton, separator, stopButton, helpButton);
        controlPanel.getChildren().add(buttonBox);

        // Enhanced instructions with better styling
        VBox instructions = createModernInstructions();
        controlPanel.getChildren().add(instructions);

        return controlPanel;
    }

    private Button createModernButton(String text, String color1, String color2, String tooltipText) {
        Button button = new Button();
        button.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 14));
        button.setPrefWidth(130);
        button.setPrefHeight(50);
        button.setTooltip(new Tooltip(tooltipText));

        // Create icon based on button text
        if (text.contains("START")) {
            button.setGraphic(createPlayIcon());
            button.setText("START");
        } else if (text.contains("STOP")) {
            button.setGraphic(createStopIcon());
            button.setText("STOP");
        } else {
            button.setText(text.toUpperCase());
        }

        // Enhanced gradient with better color stops for more distinction
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(color1)),
                new Stop(0.3, Color.web(color1, 0.95)),
                new Stop(0.7, Color.web(color2, 0.9)),
                new Stop(1, Color.web(color2)));
        button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(15), Insets.EMPTY)));
        button.setTextFill(Color.WHITE);

        // Enhanced hover effects with better animation
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();

            // Add brightness effect
            FadeTransition brighten = new FadeTransition(Duration.millis(200), button);
            brighten.setFromValue(1.0);
            brighten.setToValue(1.2);
            brighten.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            FadeTransition darken = new FadeTransition(Duration.millis(200), button);
            darken.setFromValue(1.2);
            darken.setToValue(1.0);
            darken.play();
        });

        // Professional shadow with better depth
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(0);
        shadow.setOffsetY(8);
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(15);
        shadow.setSpread(0.1);

        button.setEffect(shadow);

        return button;
    }

    private StackPane createPlayIcon() {
        // Create a play triangle icon
        javafx.scene.shape.Polygon playTriangle = new javafx.scene.shape.Polygon();
        playTriangle.getPoints().addAll(new Double[]{
            0.0, 0.0,    // Top point
            12.0, 6.0,   // Right point  
            0.0, 12.0    // Bottom point
        });
        playTriangle.setFill(Color.WHITE);
        
        // Add subtle drop shadow to icon
        DropShadow iconShadow = new DropShadow();
        iconShadow.setOffsetX(1);
        iconShadow.setOffsetY(1);
        iconShadow.setColor(Color.web("#000000", 0.3));
        iconShadow.setRadius(2);
        playTriangle.setEffect(iconShadow);
        
        StackPane iconContainer = new StackPane(playTriangle);
        iconContainer.setPrefSize(16, 16);
        return iconContainer;
    }

    private StackPane createStopIcon() {
        // Create a stop square icon
        Rectangle stopSquare = new Rectangle(10, 10);
        stopSquare.setFill(Color.WHITE);
        stopSquare.setArcWidth(2);
        stopSquare.setArcHeight(2);
        
        // Add subtle drop shadow to icon
        DropShadow iconShadow = new DropShadow();
        iconShadow.setOffsetX(1);
        iconShadow.setOffsetY(1);
        iconShadow.setColor(Color.web("#000000", 0.3));
        iconShadow.setRadius(2);
        stopSquare.setEffect(iconShadow);
        
        StackPane iconContainer = new StackPane(stopSquare);
        iconContainer.setPrefSize(16, 16);
        return iconContainer;
    }    private Button createModernHelpButton() {
        // Create a more modern help button with icon
        Circle circle = new Circle(20, Color.web("#6366f1"));
        Text questionMark = new Text("?");
        questionMark.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 18));
        questionMark.setFill(Color.WHITE);

        StackPane helpButton = new StackPane(circle, questionMark);
        helpButton.setPadding(new Insets(8));

        Button button = new Button();
        button.setGraphic(helpButton);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-background-radius: 50%;");
        button.setTooltip(new Tooltip("Help & Instructions"));
        button.setPrefSize(40, 40);

        // Add hover effect
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        button.setOnAction(event -> showModernHelpDialog());

        return button;
    }

    private VBox createModernInstructions() {
        VBox instructions = new VBox(8);
        instructions.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 15; -fx-padding: 20; -fx-border-color: rgba(0,0,0,0.1); -fx-border-radius: 15; -fx-border-width: 1;");

        // Add subtle shadow
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setColor(Color.web("#000000", 0.1));
        shadow.setRadius(8);
        instructions.setEffect(shadow);

        Label instructionTitle = new Label("Quick Start Guide");
        instructionTitle.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 16));
        instructionTitle.setTextFill(Color.web("#1f2937"));

        Label step1 = new Label("1. Click START to begin listening");
        Label step2 = new Label("2. Press F9 to capture screenshots");
        Label step3 = new Label("3. Add custom titles to screenshots");
        Label step4 = new Label("4. Click STOP and export to Word");

        Label[] steps = {step1, step2, step3, step4};
        for (int i = 0; i < steps.length; i++) {
            steps[i].setFont(Font.font("SF Pro Text", FontWeight.NORMAL, 13));
            steps[i].setTextFill(Color.web("#4b5563"));

            // Add bullet point styling
            steps[i].setStyle("-fx-padding: 2 0 2 15; -fx-background-color: transparent;");
        }

        instructions.getChildren().addAll(instructionTitle, step1, step2, step3, step4);
        return instructions;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(12);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(15, 25, 15, 25));
        statusBar.setStyle("-fx-background-color: linear-gradient(to right, #f8fafc, #f1f5f9); -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");

        statusIndicator = new Circle(8, Color.web("#cbd5e1"));
        statusLabel = new Label("Ready to capture");
        statusLabel.setFont(Font.font("SF Pro Text", FontWeight.MEDIUM, 13));
        statusLabel.setTextFill(Color.web("#475569"));

        // Add keyboard shortcut hint
        Label shortcutLabel = new Label("F9");
        shortcutLabel.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 11));
        shortcutLabel.setTextFill(Color.web("#64748b"));
        shortcutLabel.setStyle("-fx-background-color: rgba(100, 116, 139, 0.1); -fx-padding: 4 8 4 8; -fx-background-radius: 12;");

        statusBar.getChildren().addAll(statusIndicator, statusLabel, shortcutLabel);
        return statusBar;
    }

    private void updateStatus(boolean listening) {
        Platform.runLater(() -> {
            if (listening) {
                statusIndicator.setFill(Color.web("#00d4aa"));
                statusLabel.setText("Listening for screenshots...");
                playButton.setDisable(true);
                stopButton.setDisable(false);

                // Enhanced pulse animation
                FadeTransition pulse = new FadeTransition(Duration.millis(800), statusIndicator);
                pulse.setFromValue(1.0);
                pulse.setToValue(0.4);
                pulse.setCycleCount(FadeTransition.INDEFINITE);
                pulse.setAutoReverse(true);
                pulse.play();
            } else {
                statusIndicator.setFill(Color.web("#cbd5e1"));
                statusLabel.setText("Ready to capture");
                playButton.setDisable(false);
                stopButton.setDisable(true);
            }
        });
    }

    private void showModernHelpDialog() {
        Stage helpStage = new Stage();
        helpStage.initStyle(StageStyle.UTILITY);
        helpStage.setTitle("SnapScribe - Help & Features");

        VBox helpContent = new VBox(20);
        helpContent.setPadding(new Insets(25));
        helpContent.setAlignment(Pos.TOP_LEFT);
        helpContent.setStyle("-fx-background-color: linear-gradient(to bottom, #f8fafc, #f1f5f9);");

        // Header
        Label helpTitle = new Label("SnapScribe Help Center");
        helpTitle.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 20));
        helpTitle.setTextFill(Color.web("#1e293b"));

        // Features section
        VBox featuresBox = new VBox(10);
        featuresBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;");

        Label featuresTitle = new Label("✨ Features");
        featuresTitle.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 16));
        featuresTitle.setTextFill(Color.web("#1e293b"));

        Label[] features = {
            new Label("• Professional screenshot capture with custom titles"),
            new Label("• Global hotkey (F9) for instant capture"),
            new Label("• Multi-screen support with screen selection"),
            new Label("• Export to Word documents with formatting"),
            new Label("• Modern, intuitive user interface"),
            new Label("• Real-time status indicators")
        };

        for (Label feature : features) {
            feature.setFont(Font.font("SF Pro Text", 13));
            feature.setTextFill(Color.web("#475569"));
        }

        featuresBox.getChildren().add(featuresTitle);
        featuresBox.getChildren().addAll(features);

        // Keyboard shortcuts section
        VBox shortcutsBox = new VBox(8);
        shortcutsBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 20;");

        Label shortcutsTitle = new Label("⌨️ Keyboard Shortcuts");
        shortcutsTitle.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 16));
        shortcutsTitle.setTextFill(Color.web("#1e293b"));

        Label shortcut1 = new Label("F9 - Capture Screenshot");
        shortcut1.setFont(Font.font("SF Pro Text", FontWeight.MEDIUM, 13));
        shortcut1.setTextFill(Color.web("#059669"));
        shortcut1.setStyle("-fx-background-color: rgba(5, 150, 105, 0.1); -fx-padding: 8 12 8 12; -fx-background-radius: 8;");

        shortcutsBox.getChildren().addAll(shortcutsTitle, shortcut1);

        // Close button
        Button closeButton = new Button("Got it!");
        closeButton.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 14));
        closeButton.setStyle("-fx-background-color: linear-gradient(to right, #6366f1, #8b5cf6); -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12 24 12 24;");
        closeButton.setOnAction(e -> helpStage.close());

        helpContent.getChildren().addAll(helpTitle, featuresBox, shortcutsBox, closeButton);

        Scene helpScene = new Scene(helpContent, 450, 500);
        helpStage.setScene(helpScene);
        helpStage.show();
    }

    private void stopGlobalKeyListener() {
        try {
            GlobalScreen.removeNativeKeyListener(keyListener);
            GlobalScreen.unregisterNativeHook();
            keyListener = null;
        } catch (NativeHookException ex) {
            System.err.println("There was a problem unregistering the native hook.");
            ex.printStackTrace();
        }
    }

    private void startGlobalListener(Object window) {
        Stage primaryStage = (Stage) window;
        
        // Minimize the main window first
        primaryStage.setIconified(true);
        updateStatus(true);

        Stage multiScreenStage = new Stage();
        if (multiScreenNumberDisplay == null) {
            multiScreenNumberDisplay = new MultiScreenNumberDisplay();
            multiScreenNumberDisplay.start(multiScreenStage);
        }

        if (multiScreenNumberDisplay.getScreenCount() > 1) {
            // Multi-screen: Show screen selection first, then floating toolbar
            multiScreenNumberDisplay.setOnConfirmation(() -> {
                startDataWindow(multiScreenStage);
                showFloatingToolbar();
            });
        } else {
            // Single screen: Go directly to floating toolbar
            startDataWindow(multiScreenStage);
            showFloatingToolbar();
        }
    }

    private void startDataWindow(Stage stage) {
        if (storeDataWindow == null) {
            storeDataWindow = new StoreDataWindow();
            storeDataWindow.start(stage);
        }
        // Hide the data window initially to avoid interference with screenshots
        stage.setIconified(true);
    }
    
    private void showFloatingToolbar() {
        // Create and show floating toolbar after screen selection
        if (floatingToolbar == null) {
            floatingToolbar = new FloatingToolbar();
        }
        floatingToolbar.show();
        showTransparentAlert("Screenshot tools are ready! Use the floating toolbar to capture and draw.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

