module ija.umleditor {
    requires javafx.controls;
    requires javafx.fxml;


    opens ija.umleditor to javafx.fxml;
    exports ija.umleditor;
    exports ija.umleditor.models;
    exports ija.umleditor.exceptions;
    exports ija.umleditor.controllers;
    opens ija.umleditor.controllers to javafx.fxml;
}