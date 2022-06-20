/**
 * @brief Creates relation between two elements.
 * Relation is assembled from line and arrow.
 * Type of arrow depends on relation type.
 * Relation moves accordingly to elements it is bound to.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GRelation.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.UMLRelation;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.util.Objects;

/**
 * Graphical representation of class relation.
 */
public class GRelation {
    private final UMLRelation model;
    private final GClassElement srcClass;
    private final GClassElement destClass;
    private final Line baseStructure;

    /**
     * Gets first model of element.
     * @return Instance of GClassElement
     */
    public GClassElement getSrcClass() {
        return srcClass;
    }

    public UMLRelation getModel() {
        return model;
    }

    /**
     * Gets second model of element.
     * @return Instance of GClassElement
     */
    public GClassElement getDestClass() {
        return destClass;
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
     * @param model UML model instance of GRelation.
     */
    public GRelation(GClassElement e1, GClassElement e2, Pane basePane, UMLRelation model) {
        srcClass = Objects.requireNonNull(e1);
        destClass = Objects.requireNonNull(e2);
        this.model = Objects.requireNonNull(model);

        baseStructure = new Line();
        baseStructure.setStrokeWidth(3);

        updateColor();

        baseStructure.startXProperty().bind(srcClass.getBaseLayout().layoutXProperty()
                .add(srcClass.getBaseLayout().translateXProperty()
                        .add(srcClass.getBaseLayout().widthProperty().divide(2))));
        baseStructure.startYProperty().bind(srcClass.getBaseLayout().layoutYProperty()
                .add(srcClass.getBaseLayout().translateYProperty()
                        .add(srcClass.getBaseLayout().heightProperty().divide(2))));

        baseStructure.endXProperty().bind(destClass.getBaseLayout().layoutXProperty()
                .add(destClass.getBaseLayout().translateXProperty()
                        .add(destClass.getBaseLayout().widthProperty().divide(2))));
        baseStructure.endYProperty().bind(destClass.getBaseLayout().layoutYProperty()
                .add(destClass.getBaseLayout().translateYProperty()
                        .add(destClass.getBaseLayout().heightProperty().divide(2))));

        basePane.getChildren().addAll(baseStructure);
        baseStructure.toBack();
    }

    /**
     * Swaps source and destination elements.
     */
    public void swapDirection() {
        model.swapDirection();
    }

    /**
     * Sets color of relation depending on selected type.
     */
    public void updateColor() {
        switch(model.getRelationType()) {
            case ASSOCIATION:
                baseStructure.setStroke(Color.BLACK);
                break;
            case AGGREGATION:
                baseStructure.setStroke(Color.DARKGREEN);
                break;
            case COMPOSITION:
                baseStructure.setStroke(Color.DARKBLUE);
                break;
            case INHERITANCE:
                baseStructure.setStroke(Color.DARKORANGE);
                break;
            default:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Type must be specified!");
                alert.show();
                break;
        }
    }
}
