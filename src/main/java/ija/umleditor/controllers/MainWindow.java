package ija.umleditor.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MainWindow {
    public ImageView object;
    public ImageView interface_final;
    public ImageView class_final;
    public Pane pane;

    @FXML
    private Label image;

    @FXML
    public void onClicked(MouseEvent event) {
        Group g;
        ImageView myImage = (ImageView) event.getSource();
        if (myImage == class_final) {
            g = ClassDiagramFactory.createClass();
        }
        else if (myImage == object) {
            g = ClassDiagramFactory.createObject();
        }
        else {
            g = ClassDiagramFactory.createInterface();
        }
        pane.getChildren().add(g);
    }

//    public void initialize() {
//        pane.setClip(new Rectangle(300, 500));
//    }

}