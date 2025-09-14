package in.precisiontestautomation.snapscribe;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FloatingToolbar {
    private Stage toolbarStage;
    private DrawingOverlay drawingOverlay;
    private boolean isDrawingMode = false;
    private Color currentDrawingColor = Color.RED;
    private double currentStrokeWidth = 3.0;
    
    // Drag functionality
    private double xOffset = 0;
    private double yOffset = 0;
    
    public FloatingToolbar() {
        createToolbar();
    }
    
    private void createToolbar() {
        toolbarStage = new Stage();
        toolbarStage.initStyle(StageStyle.UNDECORATED);
        toolbarStage.setAlwaysOnTop(true);
        toolbarStage.setResizable(false);
        
        // Set higher Z-order to ensure toolbar stays above drawing overlay
        Platform.runLater(() -> {
            toolbarStage.toFront();
            toolbarStage.requestFocus();
        });
        
        // Main container
        VBox mainContainer = new VBox(8);
        mainContainer.setPadding(new Insets(12));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle(
            "-fx-background-color: rgba(45, 55, 72, 0.95); " +
            "-fx-background-radius: 12px; " +
            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 12px;"
        );
        
        // Add drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(0);
        shadow.setOffsetY(4);
        shadow.setColor(Color.web("#000000", 0.3));
        shadow.setRadius(12);
        mainContainer.setEffect(shadow);
        
        // Title bar for dragging
        HBox titleBar = createTitleBar();
        
        // Main buttons row
        HBox buttonRow = createMainButtons();
        
        // Drawing tools row (initially hidden)
        VBox drawingTools = createDrawingTools();
        drawingTools.setVisible(false);
        drawingTools.setManaged(false);
        
        mainContainer.getChildren().addAll(titleBar, buttonRow, drawingTools);
        
        // Store reference to drawing tools for toggling
        mainContainer.setUserData(drawingTools);
        
        Scene scene = new Scene(mainContainer);
        scene.setFill(Color.TRANSPARENT);
        toolbarStage.setScene(scene);
        
        // Position toolbar at top-right of screen
        Platform.runLater(() -> {
            toolbarStage.setX(javafx.stage.Screen.getPrimary().getVisualBounds().getMaxX() - 200);
            toolbarStage.setY(50);
        });
    }
    
    private HBox createTitleBar() {
        HBox titleBar = new HBox();
        titleBar.setPadding(new Insets(0, 0, 5, 0));
        titleBar.setAlignment(Pos.CENTER);
        titleBar.setCursor(Cursor.MOVE);
        
        javafx.scene.control.Label title = new javafx.scene.control.Label("SnapScribe Tools");
        title.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 12));
        title.setTextFill(Color.WHITE);
        
        titleBar.getChildren().add(title);
        
        // Make toolbar draggable
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
        
        return titleBar;
    }
    
    private HBox createMainButtons() {
        HBox buttonRow = new HBox(8);
        buttonRow.setAlignment(Pos.CENTER);
        
        // Drawing toggle button
        Button drawButton = createToolButtonWithIcon(createPencilIcon(), "Drawing");
        drawButton.setTooltip(new Tooltip("Toggle Drawing Mode"));
        drawButton.setOnAction(e -> toggleDrawingMode());
        
        // Screenshot button
        Button screenshotButton = createToolButtonWithIcon(createCameraIcon(), "Capture");
        screenshotButton.setTooltip(new Tooltip("Take Screenshot"));
        screenshotButton.setOnAction(e -> takeScreenshot());
        
        // Stop button
        Button stopButton = createToolButtonWithIcon(createStopIcon(), "Stop");
        stopButton.setTooltip(new Tooltip("Stop & Save"));
        stopButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ef4444, #dc2626); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold;"
        );
        stopButton.setOnAction(e -> stopCapture());
        
        buttonRow.getChildren().addAll(drawButton, screenshotButton, stopButton);
        return buttonRow;
    }
    
    private VBox createDrawingTools() {
        VBox toolsContainer = new VBox(6);
        toolsContainer.setAlignment(Pos.CENTER);
        toolsContainer.setPadding(new Insets(8, 0, 0, 0));
        
        // Instructions label
        javafx.scene.control.Label instructionLabel = new javafx.scene.control.Label("Draw anywhere on selected screen");
        instructionLabel.setTextFill(Color.web("#e2e8f0"));
        instructionLabel.setFont(Font.font(9));
        instructionLabel.setStyle("-fx-background-color: rgba(34, 197, 94, 0.1); -fx-padding: 4; -fx-background-radius: 4;");
        
        // Color picker
        ColorPicker colorPicker = new ColorPicker(currentDrawingColor);
        colorPicker.setPrefWidth(120);
        colorPicker.setOnAction(e -> {
            currentDrawingColor = colorPicker.getValue();
            if (drawingOverlay != null) {
                drawingOverlay.setDrawingColor(currentDrawingColor);
            }
        });
        
        // Stroke width slider
        HBox sliderBox = new HBox(5);
        sliderBox.setAlignment(Pos.CENTER);
        
        javafx.scene.control.Label strokeLabel = new javafx.scene.control.Label("Size:");
        strokeLabel.setTextFill(Color.WHITE);
        strokeLabel.setFont(Font.font(10));
        
        Slider strokeSlider = new Slider(1, 10, currentStrokeWidth);
        strokeSlider.setPrefWidth(80);
        strokeSlider.setShowTickMarks(true);
        strokeSlider.setMajorTickUnit(3);
        strokeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            currentStrokeWidth = newVal.doubleValue();
            if (drawingOverlay != null) {
                drawingOverlay.setStrokeWidth(currentStrokeWidth);
            }
        });
        
        sliderBox.getChildren().addAll(strokeLabel, strokeSlider);
        
        // Clear drawings button
        Button clearButton = createSmallButton("Clear", "Clear all drawings");
        clearButton.setOnAction(e -> {
            if (drawingOverlay != null) {
                drawingOverlay.clearDrawings();
            }
        });
        
        toolsContainer.getChildren().addAll(instructionLabel, colorPicker, sliderBox, clearButton);
        return toolsContainer;
    }
    
    private Button createSmallButton(String text, String tooltipText) {
        Button button = new Button(text);
        button.setFont(Font.font(10));
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6b7280, #4b5563); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 4px 8px;"
        );
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }
    
    private Button createToolButtonWithIcon(StackPane icon, String text) {
        Button button = new Button(text);
        button.setGraphic(icon);
        button.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 12));
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4f46e5, #3730a3); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-min-width: 80px;"
        );
        
        // Hover effects
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5b52f0, #4338ca); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-min-width: 80px;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4f46e5, #3730a3); " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 8px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-min-width: 80px;"
        ));
        
        return button;
    }
    
    private StackPane createPencilIcon() {
        // Create a pencil icon using JavaFX shapes
        javafx.scene.shape.Line pencilBody = new javafx.scene.shape.Line(2, 12, 10, 4);
        pencilBody.setStroke(Color.WHITE);
        pencilBody.setStrokeWidth(2);
        
        javafx.scene.shape.Line pencilTip = new javafx.scene.shape.Line(10, 4, 12, 2);
        pencilTip.setStroke(Color.WHITE);
        pencilTip.setStrokeWidth(2);
        
        javafx.scene.shape.Circle pencilPoint = new javafx.scene.shape.Circle(12, 2, 1);
        pencilPoint.setFill(Color.WHITE);
        
        StackPane iconContainer = new StackPane();
        iconContainer.getChildren().addAll(pencilBody, pencilTip, pencilPoint);
        iconContainer.setPrefSize(16, 16);
        return iconContainer;
    }
    
    private StackPane createCameraIcon() {
        // Create a camera icon using JavaFX shapes
        Rectangle cameraBody = new Rectangle(12, 8);
        cameraBody.setFill(Color.TRANSPARENT);
        cameraBody.setStroke(Color.WHITE);
        cameraBody.setStrokeWidth(1.5);
        cameraBody.setArcWidth(2);
        cameraBody.setArcHeight(2);
        
        javafx.scene.shape.Circle lens = new javafx.scene.shape.Circle(6, 4, 2.5);
        lens.setFill(Color.TRANSPARENT);
        lens.setStroke(Color.WHITE);
        lens.setStrokeWidth(1.5);
        
        Rectangle flash = new Rectangle(2, 1.5);
        flash.setFill(Color.WHITE);
        flash.setX(8);
        flash.setY(-1);
        
        StackPane iconContainer = new StackPane();
        iconContainer.getChildren().addAll(cameraBody, lens, flash);
        iconContainer.setPrefSize(16, 16);
        return iconContainer;
    }
    
    private StackPane createStopIcon() {
        // Create a stop square icon
        Rectangle stopSquare = new Rectangle(8, 8);
        stopSquare.setFill(Color.WHITE);
        stopSquare.setArcWidth(1);
        stopSquare.setArcHeight(1);
        
        StackPane iconContainer = new StackPane(stopSquare);
        iconContainer.setPrefSize(16, 16);
        return iconContainer;
    }
    
    private void toggleDrawingMode() {
        isDrawingMode = !isDrawingMode;
        VBox drawingTools = (VBox) toolbarStage.getScene().getRoot().getUserData();
        
        if (isDrawingMode) {
            // Show drawing tools
            drawingTools.setVisible(true);
            drawingTools.setManaged(true);
            
            // Create drawing overlay
            if (drawingOverlay == null) {
                drawingOverlay = new DrawingOverlay();
            }
            drawingOverlay.show();
            drawingOverlay.setDrawingColor(currentDrawingColor);
            drawingOverlay.setStrokeWidth(currentStrokeWidth);
            
            // Ensure toolbar stays on top of drawing overlay
            bringToFront();
        } else {
            // Hide drawing tools
            drawingTools.setVisible(false);
            drawingTools.setManaged(false);
            
            // Hide drawing overlay
            if (drawingOverlay != null) {
                drawingOverlay.hide();
            }
        }
    }
    
    private void takeScreenshot() {
        try {
            // Hide toolbar temporarily during screenshot to avoid capturing it
            hideForScreenshot();
            
            // Small delay to ensure toolbar is hidden
            Thread.sleep(150);
            
            // Take the base screenshot
            java.awt.image.BufferedImage baseScreenshot = null;
            if (ButtonDesign.multiScreenNumberDisplay != null) {
                baseScreenshot = ButtonDesign.multiScreenNumberDisplay.takeScreenshot();
            }
            
            if (baseScreenshot != null) {
                // Apply any drawings from the overlay to the screenshot
                java.awt.image.BufferedImage finalScreenshot = baseScreenshot;
                if (drawingOverlay != null && drawingOverlay.isShowing()) {
                    finalScreenshot = drawingOverlay.applyDrawingsToImage(baseScreenshot);
                }
                
                // Convert and store the annotated screenshot
                if (ButtonDesign.storeDataWindow != null) {
                    ButtonDesign.storeDataWindow.pasteImageFromClipboard(
                        ImageConverter.convertToFxImage(finalScreenshot)
                    );
                    System.out.println("Screenshot taken with annotations applied");
                } else {
                    System.err.println("StoreDataWindow is null");
                }
                
                // Show toolbar again after screenshot
                showAfterScreenshot();
            } else {
                System.err.println("Base screenshot capture failed");
                // Show toolbar again even if screenshot failed
                showAfterScreenshot();
            }
            
        } catch (Exception e) {
            System.err.println("Error during screenshot capture: " + e.getMessage());
            e.printStackTrace();
            // Ensure toolbar is shown again even if there's an error
            showAfterScreenshot();
        }
    }
    
    private void stopCapture() {
        // Hide drawing overlay
        if (drawingOverlay != null) {
            drawingOverlay.hide();
        }
        
        // Show data window and clean up
        if (ButtonDesign.storeDataWindow != null) {
            ButtonDesign.storeDataWindow.displayWindow();
            ButtonDesign.storeDataWindow = null;
        }
        
        // Hide toolbar
        hide();
    }
    
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }
    
    private void handleMouseDragged(MouseEvent event) {
        toolbarStage.setX(event.getScreenX() - xOffset);
        toolbarStage.setY(event.getScreenY() - yOffset);
    }
    
    public void show() {
        if (toolbarStage != null) {
            toolbarStage.show();
            toolbarStage.toFront();
            // Ensure toolbar stays above drawing overlay
            Platform.runLater(() -> {
                toolbarStage.toFront();
                toolbarStage.setAlwaysOnTop(true);
            });
        }
    }
    
    public void hide() {
        if (toolbarStage != null) {
            toolbarStage.hide();
        }
        if (drawingOverlay != null) {
            drawingOverlay.hide();
        }
    }
    
    /**
     * Temporarily hide toolbar for screenshot capture
     */
    public void hideForScreenshot() {
        if (toolbarStage != null && toolbarStage.isShowing()) {
            toolbarStage.hide();
        }
    }
    
    /**
     * Show toolbar after screenshot capture
     */
    public void showAfterScreenshot() {
        if (toolbarStage != null) {
            Platform.runLater(() -> {
                toolbarStage.show();
                toolbarStage.toFront();
                toolbarStage.setAlwaysOnTop(true);
            });
        }
    }
    
    /**
     * Ensure toolbar stays on top (call when drawing overlay is shown)
     */
    public void bringToFront() {
        if (toolbarStage != null && toolbarStage.isShowing()) {
            Platform.runLater(() -> {
                toolbarStage.toFront();
                toolbarStage.setAlwaysOnTop(true);
            });
        }
    }
    
    public boolean isShowing() {
        return toolbarStage != null && toolbarStage.isShowing();
    }
}
