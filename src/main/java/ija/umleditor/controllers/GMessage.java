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
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.QuadCurve;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class GMessage {
    private final UMLMessage model;
    private GObject srcGObject;
    private GObject dstGObject;
    private final Polygon arrow;
    private final Line msgLine;
    private Line line1;
    private Line line2;
    private Line line3;
    private Label nameLabel = null;
    private final double startYPos = 100;
    private final double offsetYPos = 50;
    private final StringProperty labelText = new SimpleStringProperty();
    private boolean pointLeft;
    private Pane root;

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
        this.root = Objects.requireNonNull(root);
        String text = model.getName();

        // create line
        msgLine = new Line();
        msgLine.setStrokeWidth(2);

        arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, 20.0, 8.0, 8.0, 20.0);

        if (srcGObject == dstGObject) {

            // create three lines and bind them together
            line1 = new Line();
            line1.setStrokeWidth(2);
            line1.startXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                    .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty().divide(2))));
            line1.setStartY(startYPos + offsetYPos * count);
            line1.endXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                    .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty())));
            line1.setEndY(startYPos + offsetYPos * count);

            line2 = new Line();
            line2.setStrokeWidth(2);
            line2.startXProperty().bind(line1.translateXProperty().add(line1.endXProperty()));
            line2.setStartY(startYPos + offsetYPos * count);
            line2.endXProperty().bind(line1.translateXProperty().add(line1.endXProperty()));
            line2.setEndY(startYPos + offsetYPos * count + 20);

            line3 = new Line();
            line3.setStrokeWidth(2);
            line3.startXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                    .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty())));
            line3.setStartY(startYPos + offsetYPos * count + 20);
            line3.endXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                    .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty().divide(2))));
            line3.setEndY(startYPos + offsetYPos * count + 20);

            // bind text field to the first line
            nameLabel = new Label();
            nameLabel.setStyle("-fx-background-color: #f4f4f4");
            nameLabel.textProperty().bind(labelText);
            nameLabel.setLayoutY(startYPos + offsetYPos * count - 25);
            nameLabel.translateXProperty().bind((line1.endXProperty().add(line1.startXProperty())).divide(2).subtract(nameLabel.widthProperty().divide(2)));
            updateText();

            switch (model.getMessageType()){
                case ASYNC:
                    arrow.setFill(Color.TRANSPARENT);
                    arrow.setStrokeWidth(2);
                    arrow.getStrokeDashArray().addAll(20d);
                    arrow.setStroke(Color.BLACK);
                    break;
                case SYNC:
                    arrow.setFill(Color.BLACK);
                    break;
                case RETURN:
                    arrow.setFill(Color.TRANSPARENT);
                    arrow.setStrokeWidth(2);
                    arrow.getStrokeDashArray().addAll(20d);
                    arrow.setStroke(Color.BLACK);
                    line1.getStrokeDashArray().addAll(9d, 9d);
                    line2.getStrokeDashArray().addAll(9d, 9d);
                    line3.getStrokeDashArray().addAll(9d, 9d);
                    break;
            }

            Rotate rotation = new Rotate(-180/4.0);
            arrow.getTransforms().add(rotation);
            arrow.translateXProperty().bind(line3.endXProperty().add(line3.translateXProperty()));
            arrow.translateYProperty().bind(line3.endYProperty().add(line3.translateYProperty()));

            root.getChildren().addAll(line1, line2, line3, arrow, nameLabel);
        } else {
            // bind line to objects
            msgLine.startXProperty().bind(srcGObject.getObjectLabel().layoutXProperty()
                    .add(srcGObject.getObjectLabel().translateXProperty().add(srcGObject.getObjectLabel().widthProperty().divide(2))));
            msgLine.setStartY(startYPos + offsetYPos * count);
            msgLine.endXProperty().bind(dstGObject.getObjectLabel().layoutXProperty()
                    .add(dstGObject.getObjectLabel().translateXProperty().add(dstGObject.getObjectLabel().widthProperty().divide(2))));
            msgLine.setEndY(startYPos + offsetYPos * count);

            nameLabel = new Label();
            nameLabel.setStyle("-fx-background-color: #f4f4f4");
            nameLabel.textProperty().bind(labelText);
            nameLabel.setLayoutY(startYPos + offsetYPos * count - 25);
            nameLabel.translateXProperty().bind((msgLine.endXProperty().add(msgLine.startXProperty())).divide(2).subtract(nameLabel.widthProperty().divide(2)));
            updateText();

            switch (model.getMessageType()){
                case ASYNC:
                    arrow.setFill(Color.TRANSPARENT);
                    arrow.setStrokeWidth(2);
                    arrow.getStrokeDashArray().addAll(20d);
                    arrow.setStroke(Color.BLACK);
                    break;
                case SYNC:
                    arrow.setFill(Color.BLACK);
                    break;
                case RETURN:
                    arrow.setFill(Color.TRANSPARENT);
                    arrow.setStrokeWidth(2);
                    arrow.getStrokeDashArray().addAll(20d);
                    arrow.setStroke(Color.BLACK);
                    msgLine.getStrokeDashArray().addAll(10d, 10d);
                    break;
            }

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
        if (srcGObject == dstGObject) {
            canvas.getChildren().removeAll(line1, line2, line3, arrow, nameLabel);
        }
        else {
            canvas.getChildren().removeAll(msgLine, arrow, nameLabel);
        }
    }

    /**
     * Updates GMessage position.
     * If {@code offsetDown} value is negative number, new position will be higher than original
     * and otherwise. Position is represented as index in a list.
     * @param offsetDown Number of position to move down.
     */
    public void moveContent(int offsetDown) {
        if (msgLine != null)
            msgLine.setLayoutY(msgLine.getLayoutY() + offsetDown * offsetYPos);
        if (line1 != null)
            line1.setLayoutY(line1.getLayoutY() + offsetDown * offsetYPos);
        if (line2 != null)
            line2.setLayoutY(line2.getLayoutY() + offsetDown * offsetYPos);
        if (line3 != null)
            line3.setLayoutY(line3.getLayoutY() + offsetDown * offsetYPos);

        nameLabel.setLayoutY(nameLabel.getLayoutY() + offsetDown * offsetYPos);
        arrow.setLayoutY(arrow.getLayoutY() + offsetDown * offsetYPos);
    }

    public void update(String msg) {
        if (Objects.equals(msg, "line")) {
            root.getChildren().remove(arrow);
            if (srcGObject == dstGObject) {
                switch (model.getMessageType()){
                    case ASYNC:
                        arrow.setFill(Color.TRANSPARENT);
                        arrow.setStrokeWidth(2);
                        arrow.getStrokeDashArray().addAll(20d);
                        arrow.setStroke(Color.BLACK);
                        line1.getStrokeDashArray().clear();
                        line2.getStrokeDashArray().clear();
                        line3.getStrokeDashArray().clear();
                        break;
                    case SYNC:
                        arrow.setFill(Color.BLACK);
                        line1.getStrokeDashArray().clear();
                        line2.getStrokeDashArray().clear();
                        line3.getStrokeDashArray().clear();
                        break;
                    case RETURN:
                        arrow.setFill(Color.TRANSPARENT);
                        arrow.setStrokeWidth(2);
                        arrow.getStrokeDashArray().addAll(20d);
                        arrow.setStroke(Color.BLACK);
                        line1.getStrokeDashArray().addAll(9d, 9d);
                        line2.getStrokeDashArray().addAll(9d, 9d);
                        line3.getStrokeDashArray().addAll(9d, 9d);
                        break;
                }
            }
            else {
                switch (model.getMessageType()) {
                    case ASYNC:
                        arrow.setFill(Color.TRANSPARENT);
                        arrow.setStrokeWidth(2);
                        arrow.getStrokeDashArray().addAll(20d);
                        arrow.setStroke(Color.BLACK);
                        msgLine.getStrokeDashArray().clear();
                        break;
                    case SYNC:
                        arrow.setFill(Color.BLACK);
                        msgLine.getStrokeDashArray().clear();
                        break;
                    case RETURN:
                        arrow.setFill(Color.TRANSPARENT);
                        arrow.setStrokeWidth(2);
                        arrow.getStrokeDashArray().addAll(20d);
                        arrow.setStroke(Color.BLACK);
                        msgLine.getStrokeDashArray().addAll(10d, 10d);
                        break;
                }
            }
            root.getChildren().add(arrow);
        }
    }

    public void updateColor(boolean isError) {
        if (isError) {
            // TODO: red
        } else {
            // TODO: black
        }
    }
}
