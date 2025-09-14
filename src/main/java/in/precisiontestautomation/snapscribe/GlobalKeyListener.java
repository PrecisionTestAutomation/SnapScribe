package in.precisiontestautomation.snapscribe;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.stage.Stage;

import java.awt.*;
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
                // Hide floating toolbar temporarily during screenshot
                if (ButtonDesign.getFloatingToolbar() != null) {
                    ButtonDesign.getFloatingToolbar().hideForScreenshot();
                }
                
                // Small delay to ensure toolbar is hidden
                Thread.sleep(150);
                
                BufferedImage screenFullImage = multiScreenNumberDisplay.takeScreenshot();
                
                if (screenFullImage != null) {
                    System.out.println("F9 Key Pressed: Screenshot captured successfully - Size: " + 
                                     screenFullImage.getWidth() + "x" + screenFullImage.getHeight());
                    storeDataWindow.pasteImageFromClipboard(ImageConverter.convertToFxImage(screenFullImage));
                    System.out.println("Pasted on VBOX");
                } else {
                    System.err.println("Screenshot capture failed - returned null image");
                }
                
                // Show toolbar again after screenshot
                if (ButtonDesign.getFloatingToolbar() != null) {
                    ButtonDesign.getFloatingToolbar().showAfterScreenshot();
                }
                
            } catch (HeadlessException ex) {
                System.err.println("Error capturing screen or copying to clipboard");
                ex.printStackTrace();
                // Ensure toolbar is shown again even if there's an error
                if (ButtonDesign.getFloatingToolbar() != null) {
                    ButtonDesign.getFloatingToolbar().showAfterScreenshot();
                }
            } catch (InterruptedException ex) {
                System.err.println("Thread interrupted during screenshot delay");
                ex.printStackTrace();
                // Ensure toolbar is shown again even if there's an error
                if (ButtonDesign.getFloatingToolbar() != null) {
                    ButtonDesign.getFloatingToolbar().showAfterScreenshot();
                }
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
