module ija.umleditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    exports ija.umleditor;
    exports ija.umleditor.models;
    exports ija.umleditor.controllers;
    exports ija.umleditor.template;

    opens ija.umleditor to javafx.fxml;
    opens ija.umleditor.controllers to javafx.fxml;
}