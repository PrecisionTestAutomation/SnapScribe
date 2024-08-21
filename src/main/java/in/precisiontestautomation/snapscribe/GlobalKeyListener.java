package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;

import static in.precisiontestautomation.snapscribe.ButtonDesign.storeDataWindow;


/**
 * @author PTA-dev
 */
public class GlobalKeyListener implements NativeKeyListener {

    private int selectedScreenIndex = 0;  // Default to the first screen

    public GlobalKeyListener() {
        // Detect screens and prompt user if more than one is detected
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        if (screens.length > 1) {
            Platform.runLater(() -> {
                String[] screenOptions = new String[screens.length];
                for (int i = 0; i < screens.length; i++) {
                    screenOptions[i] = "Screen " + (i + 1) + ": " + screens[i].getIDstring();
                }
                String selectedScreen = (String) JOptionPane.showInputDialog(
                        null,
                        "Multiple screens detected. Please select the screen to capture:",
                        "Select Screen",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        screenOptions,
                        screenOptions[0]);

                if (selectedScreen != null) {
                    // Parse selected screen index
                    selectedScreenIndex = Integer.parseInt(selectedScreen.split(" ")[1]) - 1;
                    System.out.println("Selected Screen: " + selectedScreenIndex);
                }
            });
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            System.out.println("F9 Key Pressed Globally");
            try {

                BufferedImage screenFullImage = getBufferedImage();

                System.out.println("F9 Key Pressed: Screenshot captured and copied to clipboard");
                storeDataWindow.pasteImageFromClipboard(ImageConverter.convertToFxImage(screenFullImage));
                System.out.println("Pasted on VBOX");
            } catch (AWTException | HeadlessException ex) {
                System.err.println("Error capturing screen or copying to clipboard");
                ex.printStackTrace();
            }
        }
    }

    private BufferedImage getBufferedImage() throws AWTException {
        Robot robot = new Robot();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        Rectangle captureRect;

        if (screens.length > 1) {
            // Capture the selected screen
            captureRect = screens[selectedScreenIndex].getDefaultConfiguration().getBounds();
        } else {
            // Capture the primary screen
            captureRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }

        BufferedImage screenFullImage = robot.createScreenCapture(captureRect);
        return screenFullImage;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Do nothing
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Do nothing
    }
}
