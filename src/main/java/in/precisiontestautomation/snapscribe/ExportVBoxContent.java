package in.precisiontestautomation.snapscribe;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author PTA-dev
 */
public class ExportVBoxContent {

    public static void exportVBoxContentToWord(VBox pane, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Document");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Word Documents", "*.docx")
        );
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            XWPFDocument document = new XWPFDocument();
            processVBoxContents(pane, document);

            // Write the Document in file system
            try (FileOutputStream out = new FileOutputStream(file.getAbsolutePath())) {
                document.write(out);
                System.out.println("Document saved!");
            } catch (IOException e) {
                System.err.println("Error saving document: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void processVBoxContents(VBox vbox, XWPFDocument document) {
        for (Node node : vbox.getChildren()) {
            if (node instanceof VBox) {
                processVBoxContents((VBox) node, document);  // Recursive call for nested VBoxes
            } else if (node instanceof TextField) {
                TextField textField = (TextField) node;
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(textField.getText());
                System.out.println("Added text: " + textField.getText());  // Debugging output
            } else if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;

                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();

                try {
                    Image image = imageView.getImage();
                    int width = (int) image.getWidth();
                    int height = (int) image.getHeight();
                    WritableImage writableImage = new WritableImage(width, height);
                    PixelWriter pixelWriter = writableImage.getPixelWriter();


                    PixelReader pixelReader = image.getPixelReader();
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            pixelWriter.setArgb(x,
                                    y, pixelReader.getArgb(x, y));
                        }
                    }
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", baos);
                    byte[] imageBytes = baos.toByteArray();
                    
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);

                    // Set the image size to 16.51 cm x 10.66 cm
                    int widthInEMU = 5943600;   // 16.51 cm in EMUs
                    int heightInEMU = 3837600;  // 10.66 cm in EMUs

                    run.addPicture(bais, Document.PICTURE_TYPE_PNG, "image.png", widthInEMU, heightInEMU);
                    bais.close();
                    baos.close();
                    System.out.println("Added image from ImageView");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
