package ija.umleditor.controllers;

import ija.umleditor.models.UMLAttribute;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLClassifier;
import ija.umleditor.models.UMLOperation;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Objects;

public class GClassElement {
    // position of mouse pressed
    private double posX;
    private double posY;
    private boolean selectable = true;

    private final VBox classBox;
    private VBox attributesBox = null;
    private VBox operationsBox = null;

    private final UMLClassifier model;
    private final GClassDiagram owner;
    private boolean selected;

    private final Group baseLayout;

    public void selected(boolean b) {
        if (b) {
            // TODO: highlight box with color
        } else {
            // TODO: cancel highlighting
        }
        selected = b;
    }

    public UMLClassifier getModel() {
        return model;
    }

    public Group getBaseLayout() {
        return baseLayout;
    }

    public void changeName(String name) {

    }

    public void addOperation(UMLOperation oper) {
        // TODO: check for duplicity
        if (operationsBox == null) {
            operationsBox = new VBox();
            operationsBox.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");
            classBox.getChildren().add(operationsBox);
        }
        // add operation to set of attributes
        // when operation with given name already exists
        // insertion is aborted and function returns false
        if (((UMLClass) model).addAttribute(oper)) {
            Label operationLabel = new Label();
            operationLabel.textProperty().bind(new SimpleStringProperty(oper.toString()));
            operationLabel.setAlignment(Pos.CENTER_LEFT);
            operationLabel.setPadding(new Insets(3, 3, 3, 3));
            operationsBox.getChildren().add(operationLabel);
        } else {
            // TODO: show warning dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Duplicity found");
            a.setContentText("Cannot create attribute that already exists within selected class");
            a.show();
        }
    }

    public void removeOperation(String operName) {
        // TODO:
    }

    public void removeOperation(UMLOperation oper) {
        // TODO:
    }

    public void addAttribute(UMLAttribute attr) {
        if (attributesBox == null) {
            attributesBox = new VBox();
            attributesBox.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");
            classBox.getChildren().add(1, attributesBox);
        }
        // add attribute to set of attributes
        // when attribute with given name already exists
        // insertion is aborted and function returns false
        if (((UMLClass) model).addAttribute(attr)) {
            Label attribute = new Label();
            attribute.textProperty().bind(new SimpleStringProperty(attr.toString()));
            attribute.setAlignment(Pos.CENTER_LEFT);
            attribute.setPadding(new Insets(3, 3, 3, 3));
            attributesBox.getChildren().add(attribute);
        } else {
            // TODO: show warning dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Duplicity found");
            a.setContentText("Cannot create attribute that already exists within selected class");
            a.show();
        }
    }

    public void removeAttribute() {
        // TODO:
    }

    public void changeAttributeName(String oldName, String newName) {
        // TODO:
    }

    public GClassElement(Pane canvas, UMLClassifier model, GClassDiagram owner) {
        // if (model instanceof UMLClass) {
        //     this.model = Objects.requireNonNull((UMLClass) model);
        // }
        this.model = Objects.requireNonNull(model);
        this.owner = Objects.requireNonNull(owner);

        // init base group layout and set mouse events
        baseLayout = new Group();
        baseLayout.setOnMouseClicked(ev -> {
            // element was dragged
            if (selectable) {
                this.owner.setSelectedElement(this);
            }
            selectable = true;
            ev.consume();
        });
        baseLayout.setOnMousePressed(ev -> {
            // store initial coordinates of mouse press
            posX = ev.getX();
            posY = ev.getY();
        });
        baseLayout.setOnMouseDragged(ev -> {
            // ignore element selection when element is dragged
            selectable = false;
            // more relatively stored mouse coordination
            baseLayout.setTranslateX(baseLayout.getTranslateX() - posX + ev.getX());
            baseLayout.setTranslateY(baseLayout.getTranslateY() - posY + ev.getY());
            ev.consume();
        });

        classBox = new VBox(-3);

        // top rectangle; bind text content with class name
        Label name = new Label();
        name.textProperty().bind(new SimpleStringProperty(model.getName()));

        name.setAlignment(Pos.CENTER);
        // TODO: new padding
        name.setPadding(new Insets(10, 10, 10, 10));
        // TODO: end new text
        name.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3; -fx-border-margin: 0");

        List<UMLAttribute> lofAttr = ((UMLClass) model).getAttributes();
        for (var item : lofAttr) {
            Label attribute = new Label();
            attribute.textProperty().bind(new SimpleStringProperty(item.toString()));
            attribute.setAlignment(Pos.CENTER_LEFT);
            attribute.setPadding(new Insets(3, 3, 3, 3));
            if (item instanceof UMLOperation) {
                if (operationsBox == null) {
                    operationsBox = new VBox();
                    operationsBox.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");
                }
                operationsBox.getChildren().add(attribute);
            } else {
                if (attributesBox == null) {
                    attributesBox = new VBox();
                    attributesBox.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");
                }
                attributesBox.getChildren().add(attribute);
            }
        }

        // put rectangles in one vbox
        // classBox.getChildren().addAll(name, attributesBox, operationsBox);
        classBox.getChildren().add(name);
        if (attributesBox != null)
            classBox.getChildren().add(attributesBox);
        if (operationsBox != null)
            classBox.getChildren().add(operationsBox);

        // group them together
        baseLayout.getChildren().add(classBox);

        // place it on canvas
        canvas.getChildren().add(baseLayout);
    }
}
