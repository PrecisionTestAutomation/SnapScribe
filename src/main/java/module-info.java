module in.precisiontestautomation.snapscribe {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.datatransfer;
    requires transitive java.desktop;
    requires transitive java.logging;
    requires transitive com.github.kwhat.jnativehook;
    requires transitive javafx.base;
    requires transitive javafx.web;
    requires transitive javafx.swing;
    requires transitive org.apache.poi.ooxml;
    requires transitive org.apache.poi.poi;


    opens in.precisiontestautomation.snapscribe to javafx.fxml;
    exports in.precisiontestautomation.snapscribe;
}