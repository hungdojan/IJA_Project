package ija.umleditor.controllers;

import ija.umleditor.models.SequenceDiagram;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class GSequenceDiagram {
    private SequenceDiagram model;
    public GSequenceDiagram(TabPane rootTab, SequenceDiagram model) {
        this.model = Objects.requireNonNull(model);
        Tab baseTab = new Tab("Sequence diagram");

        // TODO: create tab
        // content pane
        Pane basePane = new Pane();

        // button to remove class diagram instance
        Button delete = new Button("Delete diagram");
        delete.setOnAction(ev -> rootTab.getTabs().remove(baseTab));
        basePane.getChildren().add(delete);

        // add created elements to base
        baseTab.setContent(basePane);
        rootTab.getTabs().add(baseTab);
    }
}
