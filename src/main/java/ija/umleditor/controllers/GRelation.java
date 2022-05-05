/**
 * @brief Creates relation between two elements.
 * Relation is assembled from line and arrow.
 * Type of arrow depends on relation type.
 * Relation moves accordingly to elements it is binded to.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GRelation.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class GRelation {
    private final GClassElement model1;
    private final GClassElement model2;
    private final Line baseStructure;
    private Rotate rotation;
private Polygon arrow;

    /**
     * Gets first model of element.
     * @return Instance of GClassElement
     */
    public GClassElement getModel1() {
        return model1;
    }

    /**
     * Gets second model of element.
     * @return Instance of GClassElement
     */
    public GClassElement getModel2() {
        return model2;
    }
    /**
     * Gets line that connects two elements.
     * @return Instance of Line
     */
    public Line getBaseStructure() {
        return baseStructure;
    }

    /**
     * Creates relation between two elements.
     * @param e1 Starting element
     * @param e2 Ending element
     * @param basePane Canvas to put the relation on
     */
    public GRelation(GClassElement e1, GClassElement e2, Pane basePane, String type) {
        model1 = Objects.requireNonNull(e1);
        model2 = Objects.requireNonNull(e2);

        baseStructure = new Line();
        baseStructure.setStrokeWidth(3);

        if (Objects.equals(type, "Association")) {
            baseStructure.setStroke(Color.BLACK);
        } else if (Objects.equals(type, "Aggregation")) {
            baseStructure.setStroke(Color.DARKGREEN);
        } else if (Objects.equals(type, "Composition")) {
            baseStructure.setStroke(Color.DARKKHAKI);
        } else if (Objects.equals(type, "Generalization")) {
            baseStructure.setStroke(Color.DARKORANGE);
        } else {
            // TODO: dont draw relation
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Type must be specified!");
            alert.show();
        }

        // get coordination of class elements
//        Bounds boundsInSceneM1 = model1.getBaseLayout().localToScene(model1.getBaseLayout().getBoundsInLocal());
//        Bounds boundsInSceneM2 = model2.getBaseLayout().localToScene(model2.getBaseLayout().getBoundsInLocal());


        baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
                .add(model1.getBaseLayout().translateXProperty()
                        .add(model1.getBaseLayout().widthProperty().divide(2))));
        baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
                .add(model1.getBaseLayout().translateYProperty()
                        .add(model1.getBaseLayout().heightProperty().divide(2))));

        baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
                .add(model2.getBaseLayout().translateXProperty()
                        .add(model2.getBaseLayout().widthProperty().divide(2))));
        baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
                .add(model2.getBaseLayout().translateYProperty()
                        .add(model2.getBaseLayout().heightProperty().divide(2))));

//        arrow = new Polygon();
//        arrow.getPoints().addAll(0.0, 0.0, 30.0, 8.0, 8.0, 30.0);
//        arrow.setFill(Color.BLACK);
//
//        Bounds boundsInSceneArrow = arrow.localToScene(arrow.getBoundsInLocal());

//        // destination element is on the right of the source element
//        if (boundsInSceneM1.getMaxX() <= boundsInSceneM2.getMinX()) {
//            // create connection to the first class element
//            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
//                    .add(model1.getBaseLayout().translateXProperty()
//                            .add(model1.getBaseLayout().widthProperty())));
//            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
//                    .add(model1.getBaseLayout().translateYProperty()
//                            .add(model1.getBaseLayout().heightProperty().divide(2))));
//
//            // create arrow
//            arrow.translateXProperty().bind(model2.getBaseLayout().translateXProperty()
//                    .add(model2.getBaseLayout().layoutXProperty()));
//            arrow.translateYProperty().bind((model2.getBaseLayout().translateYProperty()
//                    .add(model2.getBaseLayout().layoutYProperty()
//                            .add(model2.getBaseLayout().heightProperty().divide(2)))));
//            rotation = new Rotate(180*3.0/4.0);
//            arrow.getTransforms().add(rotation);
//
//            // create connection to the second class element
//            baseStructure.endXProperty().bind(arrow.layoutXProperty()
//                    .add(arrow.translateXProperty()
//                            .subtract(boundsInSceneArrow.getMaxX()-5)));
//            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
//                    .add(model2.getBaseLayout().translateYProperty()
//                            .add(model2.getBaseLayout().heightProperty().divide(2))));
//        }
//        // destination element is on the left of the source element
//        else if (boundsInSceneM1.getMinX() > boundsInSceneM2.getMaxX()) {
//            // create connection to the first class element
//            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
//                    .add(model1.getBaseLayout().translateXProperty()));
//            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
//                    .add(model1.getBaseLayout().translateYProperty()
//                            .add(model1.getBaseLayout().heightProperty().divide(2))));
//
//            // create arrow
//            arrow.translateXProperty().bind(model2.getBaseLayout().translateXProperty()
//                    .add(model2.getBaseLayout().layoutXProperty()
//                            .add(model2.getBaseLayout().widthProperty())));
//            arrow.translateYProperty().bind((model2.getBaseLayout().translateYProperty()
//                    .add(model2.getBaseLayout().layoutYProperty()
//                            .add(model2.getBaseLayout().heightProperty().divide(2)))));
//            rotation = new Rotate(-45);
//            arrow.getTransforms().add(rotation);
//
//            // create connection to the second class element
//            baseStructure.endXProperty().bind(arrow.layoutXProperty()
//                    .add(arrow.translateXProperty()
//                            .add(boundsInSceneArrow.getMaxX()-5)));
//            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
//                    .add(model2.getBaseLayout().translateYProperty()
//                            .add(model2.getBaseLayout().heightProperty().divide(2))));
//        }
//        // destination element is below the source element
//        else if (boundsInSceneM1.getMaxY() <= boundsInSceneM2.getMinY()) {
//            // create connection to the first class element
//            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
//                    .add(model1.getBaseLayout().translateXProperty()
//                            .add(model1.getBaseLayout().widthProperty().divide(2))));
//            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
//                    .add(model1.getBaseLayout().translateYProperty()
//                            .add(model1.getBaseLayout().heightProperty())));
//
//            // create connection to the second class element
//            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
//                    .add(model2.getBaseLayout().translateXProperty()
//                            .add(model2.getBaseLayout().widthProperty().divide(2))));
//            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
//                    .add(model2.getBaseLayout().translateYProperty()));
//        }
//        // destination element is above the source element
//        else {
//            // create connection to the first class element
//            baseStructure.startXProperty().bind(model1.getBaseLayout().layoutXProperty()
//                    .add(model1.getBaseLayout().translateXProperty()
//                            .add(model1.getBaseLayout().widthProperty().divide(2))));
//            baseStructure.startYProperty().bind(model1.getBaseLayout().layoutYProperty()
//                    .add(model1.getBaseLayout().translateYProperty()));
//
//            // create connection to the second class element
//            baseStructure.endXProperty().bind(model2.getBaseLayout().layoutXProperty()
//                    .add(model2.getBaseLayout().translateXProperty()
//                            .add(model2.getBaseLayout().widthProperty().divide(2))));
//            baseStructure.endYProperty().bind(model2.getBaseLayout().layoutYProperty()
//                    .add(model2.getBaseLayout().translateYProperty()
//                            .add(model2.getBaseLayout().heightProperty())));
//        }

        basePane.getChildren().addAll(baseStructure);
        baseStructure.toBack();
    }
}
