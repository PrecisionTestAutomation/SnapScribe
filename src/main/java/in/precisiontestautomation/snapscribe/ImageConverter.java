package in.precisiontestautomation.snapscribe;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;

public class ImageConverter {

    /**
     * Converts a BufferedImage to a JavaFX Image.
     * @param bufferedImage The BufferedImage to convert.
     * @return A JavaFX Image containing the pixels from the BufferedImage.
     */
    public static Image convertToFxImage(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return null;
        }

        // Create a new writable image
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        WritableImage writableImage = new WritableImage(width, height);

        // Get the PixelWriter of the writable image
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        // Copy pixel data
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                pixelWriter.setArgb(x, y, argb);
            }
        }

        return writableImage;
    }
}
