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

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class GMessage {
    private final GObject model1;
    private final GObject model2;
    private double startYPos = 100;
    private double offsetYPos = 50;

    /**
     * Creates message between two objects.
     * @param root Canvas to draw message on
     * @param obj1 Starting object
     * @param obj2 Ending object
     */
    public GMessage(Pane root, GObject obj1, GObject obj2, double count, String text) {
        model1 = Objects.requireNonNull(obj1);
        model2 = Objects.requireNonNull(obj2);

        // create line
        Line msg = new Line();
        msg.setStrokeWidth(2);

        // bind line to objects
        msg.startXProperty().bind(model1.getObjectLabel().layoutXProperty()
                .add(model1.getObjectLabel().translateXProperty().add(model1.getObjectLabel().widthProperty().divide(2))));
        msg.setStartY(startYPos + offsetYPos * count);
        msg.endXProperty().bind(model2.getObjectLabel().layoutXProperty()
                .add(model2.getObjectLabel().translateXProperty().add(model2.getObjectLabel().widthProperty().divide(2))));
        msg.setEndY(startYPos + offsetYPos * count);

        // create arrow
        Polygon arrow = new Polygon();
        arrow.getPoints().addAll(0.0, 0.0, 20.0, 8.0, 8.0, 20.0);
        arrow.setFill(Color.BLACK);
        Rotate rotation = new Rotate(3.0/4.0*180);
        arrow.getTransforms().add(rotation);
        arrow.translateXProperty().bind(msg.endXProperty().add(msg.translateXProperty()));
        arrow.translateYProperty().bind(msg.endYProperty().add(msg.translateYProperty()));

        Label nameLabel = new Label(text);
        nameLabel.setLayoutY(startYPos + offsetYPos * count - 25);
        nameLabel.translateXProperty().bind(msg.endXProperty().add(nameLabel.widthProperty()).divide(2));

        root.getChildren().addAll(msg, arrow, nameLabel);
    }
}
