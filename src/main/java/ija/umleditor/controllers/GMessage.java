package ija.umleditor.controllers;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.Objects;

public class GMessage {
//    private final GObject model1;
//    private final GObject model2;
    public GMessage(Pane root) {
//        model1 = Objects.requireNonNull(obj1);
//        model2 = Objects.requireNonNull(obj2);

        Line msg = new Line();
//        msg.startXProperty().bind(model1.);
        root.getChildren().add(msg);
    }
}
