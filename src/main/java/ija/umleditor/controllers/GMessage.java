/**
 * @brief Creates message between two objects.
 * Message is assembled from line and arrow. Type of both depends on type of message.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GMessage.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.UMLMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class GMessage {
    private final UMLMessage model;
    private GObject srcGObject;
    private GObject dstGObject;
    private final Polygon arrow;
    private final Line msgLine;
    private final Label nameLabel;
    private final double startYPos = 100;
    private final double offsetYPos = 50;
    private final StringProperty labelText = new SimpleStringProperty();
    private boolean pointLeft;

    public UMLMessage getModel() {
        return model;
    }

    public void setSrcGObject(GObject srcGObject) {
        this.srcGObject = srcGObject;
    }

    public void setDstGObject(GObject dstGObject) {
        this.dstGObject = dstGObject;
    }

    public GObject getDstGObject() {
        return dstGObject;
    }

    public GObject getSrcGObject() {
        return srcGObject;
    }

    /**
     * Creates message between two objects.
     * @param root Canvas to draw message on
     * @param obj1 Starting object
     * @param obj2 Ending object
     */
    public GMessage(Pane root, GObject obj1, GObject obj2, double count, UMLMessage model) {
        srcGObject = Objects.requireNonNull(obj1);
        dstGObject = Objects.requireNonNull(obj2);
        this.model  = Objects.requireNonNull(model);
        String text = model.getName();

        // create line
        msgLine = new Line();
        msgLine.setStrokeWidth(2);

        // bind line to objects
        msgLine.startXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty().divide(2))));
        msgLine.setStartY(startYPos + offsetYPos * count);
        msgLine.endXProperty().bind(dstGObject.getObjectLabel().layoutXProperty()
                .add(dstGObject.getObjectLabel().translateXProperty().add(dstGObject.getObjectLabel().widthProperty().divide(2))));
        msgLine.setEndY(startYPos + offsetYPos * count);

        nameLabel = new Label();
        nameLabel.textProperty().bind(labelText);
        nameLabel.setLayoutY(startYPos + offsetYPos * count - 25);
        nameLabel.translateXProperty().bind(msgLine.endXProperty().add(nameLabel.widthProperty()).divide(2));
        updateText();

        arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, 20.0, 8.0, 8.0, 20.0);
        arrow.setFill(Color.BLACK);
        Rotate rotation = new Rotate(3.0/4.0*180);
        arrow.getTransforms().add(rotation);
        arrow.translateXProperty().bind(msgLine.endXProperty().add(msgLine.translateXProperty()));
        arrow.translateYProperty().bind(msgLine.endYProperty().add(msgLine.translateYProperty()));
        pointLeft = srcGObject.getObjectLabel().getLayoutX() > dstGObject.getObjectLabel().getLayoutX();
        // turn arrow if it goes to the left
        if (pointLeft) {
            Rotate rotation2 = new Rotate(180);
            arrow.getTransforms().add(rotation2);
        }

        root.getChildren().addAll(msgLine, arrow, nameLabel);
    }

    /**
     * Updates text.
     */
    public void updateText() {
        if (!model.getName().isBlank()) {
            labelText.unbind();
            labelText.setValue(model.getName());
        } else if (!model.getMessage().getName().isBlank()) {
            // labelText.setValue(model.getMessage().getName());
            labelText.bind(model.getMessage().getNameProperty());
        } else {
            labelText.unbind();
            labelText.setValue("");
        }
    }

    /**
     * Updates arrow head direction
     */
    public void updateArrowHead() {
        if (msgLine.getEndX() < msgLine.getStartX() && !pointLeft) {
            Rotate rotation2 = new Rotate(180);
            arrow.getTransforms().add(rotation2);
            pointLeft = true;
        } else if (msgLine.getEndX() > msgLine.getStartX() && pointLeft) {
            Rotate rotation2 = new Rotate(180);
            arrow.getTransforms().add(rotation2);
            pointLeft = false;
        }
    }

    public void removeFromCanvas(Pane canvas) {
        canvas.getChildren().removeAll(msgLine, arrow, nameLabel);
    }

    /**
     * Updates GMessage position.
     * If {@code offsetDown} value is negative number, new position will be higher than original
     * and otherwise. Position is represented as index in a list.
     * @param offsetDown Number of position to move down.
     */
    public void moveContent(int offsetDown) {
        msgLine.setLayoutY(msgLine.getLayoutY() + offsetDown * offsetYPos);
        nameLabel.setLayoutY(nameLabel.getLayoutY() + offsetDown * offsetYPos);
        arrow.setLayoutY(arrow.getLayoutY() + offsetDown * offsetYPos);
    }
}
