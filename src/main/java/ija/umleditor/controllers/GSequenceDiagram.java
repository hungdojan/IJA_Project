package ija.umleditor.controllers;

import ija.umleditor.models.SequenceDiagram;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

public class GSequenceDiagram {
    private SequenceDiagram model;
    public GSequenceDiagram(TabPane rootTab, SequenceDiagram model) {
        this.model = Objects.requireNonNull(model);
        Tab baseTab = new Tab("Sequence diagram");

        // TODO: create tab
        // content pane
        Pane basePane = new Pane();
        Label msg = new Label("Implementation in progress");
        msg.setFont(new Font("Arial", 100));
        msg.setTextFill(Color.RED);

        // button to remove class diagram instance
        Button delete = new Button("Delete diagram");
        delete.setOnAction(ev -> rootTab.getTabs().remove(baseTab));
        basePane.getChildren().addAll(delete, msg);

        // add created elements to base
        baseTab.setContent(basePane);
        rootTab.getTabs().add(baseTab);
        msg.layoutXProperty().bind(basePane.widthProperty().subtract(msg.widthProperty()).divide(2));
        msg.layoutYProperty().bind(basePane.heightProperty().subtract(msg.heightProperty()).divide(2));
    }
}
