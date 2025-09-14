package in.precisiontestautomation.snapscribe;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawingOverlay {
    private Stage overlayStage;
    private Canvas canvas;
    private GraphicsContext gc;
    
    private Color drawingColor = Color.RED;
    private double strokeWidth = 3.0;
    
    private boolean isDrawing = false;
    private double lastX, lastY;
    
    // Store drawing paths for clearing and screenshot integration
    private List<DrawingPath> drawingPaths = new ArrayList<>();
    
    public DrawingOverlay() {
        // Don't create overlay immediately - only when needed
    }
    
    public void show() {
        if (overlayStage == null) {
            createOverlay();
        }
        if (overlayStage != null) {
            overlayStage.show();
            overlayStage.toFront();
        }
    }
    
    private void createOverlay() {
        // Get the selected screen bounds from MultiScreenNumberDisplay
        Rectangle selectedScreenBounds = getSelectedScreenBounds();
        
        overlayStage = new Stage();
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        overlayStage.setAlwaysOnTop(true);
        overlayStage.setResizable(false);
        
        // Create canvas covering the entire selected screen
        canvas = new Canvas(selectedScreenBounds.width, selectedScreenBounds.height);
        gc = canvas.getGraphicsContext2D();
        
        // Set up drawing properties
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        
        // Make canvas transparent initially
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Set up mouse event handlers
        canvas.setOnMousePressed(this::startDrawing);
        canvas.setOnMouseDragged(this::draw);
        canvas.setOnMouseReleased(this::stopDrawing);
        
        // Create root pane without visible borders (fully transparent)
        Pane root = new Pane(canvas);
        root.setStyle("-fx-background-color: transparent;");
        root.setMouseTransparent(false); // Allow mouse interaction for drawing
        
        Scene scene = new Scene(root, selectedScreenBounds.width, selectedScreenBounds.height);
        scene.setFill(Color.TRANSPARENT);
        
        overlayStage.setScene(scene);
        
        // Position overlay to cover the entire selected screen
        overlayStage.setX(selectedScreenBounds.x);
        overlayStage.setY(selectedScreenBounds.y);
        overlayStage.setWidth(selectedScreenBounds.width);
        overlayStage.setHeight(selectedScreenBounds.height);
    }
    
    private Rectangle getSelectedScreenBounds() {
        // Get bounds from the MultiScreenNumberDisplay
        if (ButtonDesign.multiScreenNumberDisplay != null) {
            return ButtonDesign.multiScreenNumberDisplay.getSelectedScreenBounds();
        }
        
        // Fallback to primary screen if MultiScreenNumberDisplay is not available
        Screen screen = Screen.getPrimary();
        javafx.geometry.Rectangle2D screenBounds = screen.getBounds();
        return new Rectangle((int)screenBounds.getMinX(), (int)screenBounds.getMinY(), 
                           (int)screenBounds.getWidth(), (int)screenBounds.getHeight());
    }
    
    private void startDrawing(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            isDrawing = true;
            lastX = event.getX();
            lastY = event.getY();
            
            // Start a new drawing path
            DrawingPath newPath = new DrawingPath(drawingColor, strokeWidth);
            newPath.addPoint(lastX, lastY);
            drawingPaths.add(newPath);
            
            // Set drawing properties
            gc.setStroke(drawingColor);
            gc.setLineWidth(strokeWidth);
        }
    }
    
    private void draw(MouseEvent event) {
        if (isDrawing && event.isPrimaryButtonDown()) {
            double currentX = event.getX();
            double currentY = event.getY();
            
            // Draw line from last position to current position
            gc.strokeLine(lastX, lastY, currentX, currentY);
            
            // Add point to current path
            if (!drawingPaths.isEmpty()) {
                drawingPaths.get(drawingPaths.size() - 1).addPoint(currentX, currentY);
            }
            
            lastX = currentX;
            lastY = currentY;
        }
    }
    
    private void stopDrawing(MouseEvent event) {
        isDrawing = false;
    }
    
    public void setDrawingColor(Color color) {
        this.drawingColor = color;
    }
    
    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
    }
    
    public void clearDrawings() {
        drawingPaths.clear();
        if (canvas != null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }
    
    /**
     * Apply drawings to a BufferedImage (for screenshot integration)
     */
    public BufferedImage applyDrawingsToImage(BufferedImage originalImage) {
        if (drawingPaths.isEmpty() || overlayStage == null) {
            return originalImage;
        }
        
        // Create a copy of the original image
        BufferedImage result = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the original image
        g2d.drawImage(originalImage, 0, 0, null);
        
        // Since the overlay now covers the entire selected screen,
        // drawings should be applied directly without offset calculation
        for (DrawingPath path : drawingPaths) {
            if (path.getPoints().size() > 1) {
                g2d.setColor(convertFxColorToAwt(path.getColor()));
                g2d.setStroke(new BasicStroke((float) path.getStrokeWidth(), 
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                List<Point> points = path.getPoints();
                for (int i = 1; i < points.size(); i++) {
                    Point p1 = points.get(i - 1);
                    Point p2 = points.get(i);
                    
                    // Draw directly using canvas coordinates (no translation needed)
                    int x1 = (int) p1.getX();
                    int y1 = (int) p1.getY();
                    int x2 = (int) p2.getX();
                    int y2 = (int) p2.getY();
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
        
        g2d.dispose();
        return result;
    }
    
    private java.awt.Color convertFxColorToAwt(Color fxColor) {
        return new java.awt.Color(
            (float) fxColor.getRed(),
            (float) fxColor.getGreen(),
            (float) fxColor.getBlue(),
            (float) fxColor.getOpacity()
        );
    }
    
    public void hide() {
        if (overlayStage != null) {
            overlayStage.hide();
        }
    }
    
    public boolean isShowing() {
        return overlayStage != null && overlayStage.isShowing();
    }
    
    // Helper class to store drawing paths
    private static class DrawingPath {
        private final Color color;
        private final double strokeWidth;
        private final List<Point> points;
        
        public DrawingPath(Color color, double strokeWidth) {
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.points = new ArrayList<>();
        }
        
        public void addPoint(double x, double y) {
            points.add(new Point(x, y));
        }
        
        public Color getColor() { return color; }
        public double getStrokeWidth() { return strokeWidth; }
        public List<Point> getPoints() { return points; }
    }
    
    // Helper class for points
    private static class Point {
        private final double x, y;
        
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double getX() { return x; }
        public double getY() { return y; }
    }
}
