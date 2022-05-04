/**
 * @brief Creates object and puts it on canvas.
 * Object is assembled from label and dashed line.
 * Object can be moved and renamed.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GObject.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLObject;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;

import java.util.Objects;

public class GObject {

    private double posX = 0;
    private boolean selected = false;
    private boolean selectable;
    private final Label objectLabel;
    private Line line;
    private GSequenceDiagram owner;
    private final UMLObject model;

    public void selected(boolean b) {
        if (b) {
            objectLabel.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
        }
        else {
            objectLabel.setStyle("-fx-border-style: solid solid solid solid; -fx-border-width: 3; -fx-background-color: white");
        }
        selected = b;
    }

    public UMLObject getModel() {
        return model;
    }

    /**
     * Gets object.
     * @return Instance of label.
     */
    public Label getObjectLabel() {
        return objectLabel;
    }

    /**
     * Class {@code GObject} constructor.
     * @param root Pane to put the object on
     * @param model Model of this GClass
     * @param count Count of objects on pane
     */
    public GObject(Pane root, UMLObject model, int count, GSequenceDiagram owner) {

        this.model = Objects.requireNonNull(model);
        this.owner = Objects.requireNonNull(owner);
        // create object
        objectLabel = new Label();
        objectLabel.textProperty().bind(model.getToStringProperty());

        objectLabel.setPadding(new Insets(10, 10, 10, 10));
        objectLabel.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");

        // set position
        if (count > 0) {
            objectLabel.setLayoutX(objectLabel.getMaxWidth() + 200 * count);
        }
        else {
            //    private double posY = 0;
            double layoutX = 20;
            objectLabel.setLayoutX(layoutX);
        }
        double layoutY = 20;
        objectLabel.setLayoutY(layoutY);

        // create lifeline and bind it to object
        line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);
        line.getStrokeDashArray().addAll(10d, 10d);

        line.startXProperty().bind(objectLabel.layoutXProperty()
                .add(objectLabel.translateXProperty()).add(objectLabel.widthProperty().divide(2)));
        line.startYProperty().bind(objectLabel.layoutYProperty().add(objectLabel.heightProperty()));
        line.endXProperty().bind(objectLabel.layoutXProperty()
                .add(objectLabel.translateXProperty()).add(objectLabel.widthProperty().divide(2)));
        line.endYProperty().bind(objectLabel.layoutYProperty().add(root.computeAreaInScreen()));

        // moving with object
        objectLabel.setOnMouseClicked(ev -> {
            if (selectable)
                owner.setSelectedObject(this);
            selectable = true;
            ev.consume();
        });
        objectLabel.setOnMousePressed(ev -> {
            // store initial coordinates of mouse press
            posX = ev.getX();
            ev.consume();
        });
        objectLabel.setOnMouseDragged(ev -> {
            // ignore element selection when element is dragged
            selectable = false;
            // more relatively stored mouse coordination
            objectLabel.setTranslateX(objectLabel.getTranslateX() - posX + ev.getX());
//            object.setTranslateY(object.getTranslateY() - posY + ev.getY());
            ev.consume();
        });

        root.getChildren().addAll(line, objectLabel);
        line.toBack();
    }

    public void setUndefinedColor(boolean isUndefined) {
        if (isUndefined) {
            // TODO: color red -> only border??
        } else {
            // TODO: color normal -> only border??
        }

    }
}
