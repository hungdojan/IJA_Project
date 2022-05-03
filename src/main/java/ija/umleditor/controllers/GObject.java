package ija.umleditor.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GObject {

    private double posX = 0;
    private boolean selectable = true;

    public GObject(Pane root, String type, String name, double count) {

        // create object
        Label object = new Label(type + ":" + name);
        object.setPadding(new Insets(10, 10, 10, 10));
        object.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");

        // set position
        if (count > 0) {
            object.setLayoutX(object.getMaxWidth() + 200 * count);
        }
        else {
            //    private double posY = 0;
            double layoutX = 20;
            object.setLayoutX(layoutX);
        }
        double layoutY = 20;
        object.setLayoutY(layoutY);

        // create lifeline and bind it to object
        Line line = new Line();
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);
        line.getStrokeDashArray().addAll(10d, 10d);

        line.startXProperty().bind(object.layoutXProperty()
                .add(object.translateXProperty()).add(object.widthProperty().divide(2)));
        line.startYProperty().bind(object.layoutYProperty().add(object.heightProperty()));
        line.endXProperty().bind(object.layoutXProperty()
                .add(object.translateXProperty()).add(object.widthProperty().divide(2)));
        line.endYProperty().bind(object.layoutYProperty().add(root.computeAreaInScreen()));

        // moving with object TODO: DELETE?
        object.setOnMousePressed(ev -> {
            // store initial coordinates of mouse press
            posX = ev.getX();
//            posY = ev.getY();
        });
        object.setOnMouseDragged(ev -> {
            // ignore element selection when element is dragged
            selectable = false;
            // more relatively stored mouse coordination
            object.setTranslateX(object.getTranslateX() - posX + ev.getX());
//            object.setTranslateY(object.getTranslateY() - posY + ev.getY());
            ev.consume();
        });

        root.getChildren().addAll(line, object);
        line.toBack();
    }
}
