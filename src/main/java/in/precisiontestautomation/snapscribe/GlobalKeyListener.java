package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;

import static in.precisiontestautomation.snapscribe.ButtonDesign.multiScreenNumberDisplay;
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

                BufferedImage screenFullImage = multiScreenNumberDisplay.takeScreenshot();

                System.out.println("F9 Key Pressed: Screenshot captured and copied to clipboard");
                storeDataWindow.pasteImageFromClipboard(ImageConverter.convertToFxImage(screenFullImage));
                System.out.println("Pasted on VBOX");
            } catch (HeadlessException ex) {
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
