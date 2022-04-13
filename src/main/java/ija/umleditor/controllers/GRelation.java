package ija.umleditor.controllers;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.Objects;

public class GRelation {
    private final GClassElement model1;
    private final GClassElement model2;
    private Line baseStructure;

    public GClassElement getModel1() {
        return model1;
    }

    public GClassElement getModel2() {
        return model2;
    }

    public Line getBaseStructure() {
        return baseStructure;
    }

    public GRelation(GClassElement e1, GClassElement e2, Pane basePane) {
        model1 = Objects.requireNonNull(e1);
        model2 = Objects.requireNonNull(e2);

        baseStructure = new Line();
        baseStructure.setStrokeWidth(3);

        // create connection to second class element
        baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                .add(model1.getBaseLayout().translateXProperty()
                .add(model1.getBaseLayout().widthProperty().divide(2))));
        baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                .add(model1.getBaseLayout().translateYProperty()
                .add(model1.getBaseLayout().heightProperty().divide(2))));

        // create connection to second class element
        baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                .add(model2.getBaseLayout().translateXProperty()
                .add(model2.getBaseLayout().widthProperty().divide(2))));
        baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                .add(model2.getBaseLayout().translateYProperty()
                .add(model2.getBaseLayout().heightProperty().divide(2))));

        basePane.getChildren().add(baseStructure);
        baseStructure.toBack();
    }
}
