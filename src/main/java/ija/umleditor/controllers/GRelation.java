package ija.umleditor.controllers;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

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

        // get coordination of class elements
        Bounds boundsInSceneM1 = model1.getBaseLayout().localToScene(model1.getBaseLayout().getBoundsInLocal());
        Bounds boundsInSceneM2 = model2.getBaseLayout().localToScene(model2.getBaseLayout().getBoundsInLocal());

        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, 30.0, 8.0, 8.0, 30.0);
//        Bounds arrowCords = arrow.localToScene(arrow.getBoundsInLocal());
        arrow.setFill(Color.BLACK);
        Rotate rotation = new Rotate(120);
        arrow.getTransforms().add(rotation);
//        Group group = new Group(baseStructure, arrow);
        arrow.layoutXProperty().bind(baseStructure.endXProperty().add(baseStructure.translateXProperty()));
        arrow.layoutYProperty().bind(baseStructure.endYProperty().add(baseStructure.translateYProperty()));

        // destination element is on the right of the source element
        if (boundsInSceneM1.getMaxX() <= boundsInSceneM2.getMinX()) {
            // create connection to the first class element
            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                    .add(model1.getBaseLayout().translateXProperty()
                            .add(model1.getBaseLayout().widthProperty())));
            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                    .add(model1.getBaseLayout().translateYProperty()
                            .add(model1.getBaseLayout().heightProperty().divide(2))));

            // create connection to the second class element
            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                    .add(model2.getBaseLayout().translateXProperty()));
            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                    .add(model2.getBaseLayout().translateYProperty()
                            .add(model2.getBaseLayout().heightProperty().divide(2))));
        }
        // destination element is on the left of the source element
        else if (boundsInSceneM1.getMinX() > boundsInSceneM2.getMaxX()) {
            // create connection to the first class element
            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                    .add(model1.getBaseLayout().translateXProperty()));
            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                    .add(model1.getBaseLayout().translateYProperty()
                            .add(model1.getBaseLayout().heightProperty().divide(2))));

            // create connection to the second class element
            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                    .add(model2.getBaseLayout().translateXProperty()
                            .add(model2.getBaseLayout().widthProperty())));
            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                    .add(model2.getBaseLayout().translateYProperty()
                            .add(model2.getBaseLayout().heightProperty().divide(2))));
        }
        // destination element is below the source element
        else if (boundsInSceneM1.getMaxY() <= boundsInSceneM2.getMinY()) {
            // create connection to the first class element
            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                    .add(model1.getBaseLayout().translateXProperty()
                            .add(model1.getBaseLayout().widthProperty().divide(2))));
            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                    .add(model1.getBaseLayout().translateYProperty()
                            .add(model1.getBaseLayout().heightProperty())));

            // create connection to the second class element
            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                    .add(model2.getBaseLayout().translateXProperty()
                            .add(model2.getBaseLayout().widthProperty().divide(2))));
            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                    .add(model2.getBaseLayout().translateYProperty()));
        }
        // destination element is above the source element
        else {
            // create connection to the first class element
            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                    .add(model1.getBaseLayout().translateXProperty()
                            .add(model1.getBaseLayout().widthProperty().divide(2))));
            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                    .add(model1.getBaseLayout().translateYProperty()));

            // create connection to the second class element
            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                    .add(model2.getBaseLayout().translateXProperty()
                            .add(model2.getBaseLayout().widthProperty().divide(2))));
            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                    .add(model2.getBaseLayout().translateYProperty()
                            .add(model2.getBaseLayout().heightProperty())));
        }

        basePane.getChildren().addAll(baseStructure, arrow);
        baseStructure.toBack();
    }
}
