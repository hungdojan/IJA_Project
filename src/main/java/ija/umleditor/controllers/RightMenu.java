package ija.umleditor.controllers;

import ija.umleditor.models.UMLClassifier;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class RightMenu {
    private Accordion base;
    private GClassElement baseElement;

    public Accordion getBase() {
        return base;
    }

    public RightMenu(GClassElement baseElement, Pane root) {
        base = new Accordion();
        // TODO: set width
        this.baseElement = baseElement;

        // TODO: create tiledPanes
        TitledPane tp = new TitledPane();
        tp.setText("Name");
        Pane myPane = new Pane();
        tp.setContent(myPane);
        base.getPanes().add(tp);

        HBox.setHgrow(base, Priority.ALWAYS);
        root.getChildren().add(base);
    }

    public void remove(Pane root) {
        root.getChildren().remove(base);
    }
}
