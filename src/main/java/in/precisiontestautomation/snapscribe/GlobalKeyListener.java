package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;

import static in.precisiontestautomation.snapscribe.ButtonDesign.storeDataWindow;


/**
 * @author PTA-dev
 */
public class GlobalKeyListener implements NativeKeyListener {
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F9) {
            System.out.println("F9 Key Pressed Globally");
            try {
                // Create a Robot instance to capture screen
                Robot robot = new Robot();
                Rectangle captureRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage screenFullImage = robot.createScreenCapture(captureRect);

                // Put screenshot into clipboard
                TransferableImage transImage = new TransferableImage(screenFullImage);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(transImage, null);

                System.out.println("F9 Key Pressed: Screenshot captured and copied to clipboard");
                storeDataWindow.pasteImageFromClipboard();
                System.out.println("Pasted on VBOX");
            } catch (AWTException | HeadlessException ex) {
                System.err.println("Error capturing screen or copying to clipboard");
                ex.printStackTrace();
            }
        }
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
