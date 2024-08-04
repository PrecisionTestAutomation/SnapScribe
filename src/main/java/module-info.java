module in.precisiontestautomation.snapscribe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.datatransfer;
    requires java.desktop;
    requires java.logging;
    requires com.github.kwhat.jnativehook;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;


    opens in.precisiontestautomation.snapscribe to javafx.fxml;
    exports in.precisiontestautomation.snapscribe;
}