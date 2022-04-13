package ija.umleditor.controllers;

import ija.umleditor.models.UMLAttribute;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLOperation;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.util.List;
import java.util.Objects;

public class GClassElement {
    private static int initPosX = 0;
    private static int initPosY = 0;
    // position of mouse pressed
    private double posX;
    private double posY;
    private boolean selectable = true;

    private VBox attributesBox = null;
    private VBox operationsBox = null;
    private VBox nameBox;

    private final UMLClass model;
    private final GClassDiagram owner;
    private boolean selected;

    private final VBox baseLayout;

    public static void initPositions() {
        GClassElement.initPosX = 0;
        GClassElement.initPosY = 0;
    }

    public void selected(boolean b) {
        if (b) {
            if (attributesBox != null) {
                attributesBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            }
            if (operationsBox != null) {
                operationsBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            }
            nameBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
        } else {
            if (attributesBox != null) {
                attributesBox.setStyle("-fx-border-style: solid solid solid solid; -fx-border-width: 3; -fx-background-color: white");
            }
            if (operationsBox != null) {
                operationsBox.setStyle("-fx-border-style: solid solid solid solid; -fx-border-width: 3; -fx-background-color: white");
            }
            nameBox.setStyle("-fx-border-style: solid solid solid solid; -fx-border-width: 3; -fx-background-color: white");

        }
        selected = b;
    }

    public UMLClass getModel() {
        return model;
    }

    public VBox getBaseLayout() {
        return baseLayout;
    }

    public GClassDiagram getOwner() {
        return owner;
    }

    /**
     * Adds attribute to class element.
     * If class already contains given attribute or attribute with same name
     * error dialog window will be shown.
     * @param attr Instance of attribute
     */
    public void addAttribute(UMLAttribute attr) {
        if (attributesBox == null) {
            attributesBox = new VBox();
            attributesBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            baseLayout.getChildren().add(1, attributesBox);
        }
        // add attribute to set of attributes
        // when attribute with given name already exists
        // insertion is aborted and function returns false
        if (model.addAttribute(attr)) {
            Label attribute = new Label();
            attribute.textProperty().bind(attr.getToStringProperty());
            attribute.setAlignment(Pos.CENTER_LEFT);
            attribute.setPadding(new Insets(3, 3, 3, 3));
            attributesBox.getChildren().add(attribute);
        } else {
            // TODO: show warning dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText("Cannot create attribute. Attribute/operation with identical name already exists within selected class");
            a.show();
        }
    }

    public void addOperation(UMLOperation oper) {
        // TODO: check for duplicity
        if (operationsBox == null) {
            operationsBox = new VBox();
            operationsBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            baseLayout.getChildren().add(operationsBox);
        }
        // add operation to set of attributes
        // when operation with given name already exists
        // insertion is aborted and function returns false
        if (model.addAttribute(oper)) {
            Label operationLabel = new Label();
            operationLabel.textProperty().bind(oper.getToStringProperty());
            operationLabel.setAlignment(Pos.CENTER_LEFT);
            operationLabel.setPadding(new Insets(3, 3, 3, 3));
            operationsBox.getChildren().add(operationLabel);
        } else {
            // TODO: show warning dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText("Cannot create operation. Attribute/operation with identical name already exists within selected class");
            a.show();
        }
    }

    /**
     * Removes attribute from class element given attribute name.
     * Nothing happens if given attribute is not in class element.
     * @param operName Attribute's name
     */
    public void removeAttribute(String operName) {
        UMLAttribute attr = model.getAttribute(operName);
        if (attr != null)
            removeAttribute(attr);
    }

    /**
     * Removes attribute from class element with given attribute.
     * Nothing happens if given attribute is not in class element.
     * @param attr Instance of attribute/operation in the class element
     */
    public void removeAttribute(UMLAttribute attr) {
        UMLAttribute foundAttr = model.removeAttribute(attr.getName());
        if (foundAttr != null) {
            // select list of nodes depending on attribute type
            List<Node> children = foundAttr instanceof UMLOperation ?
                                    operationsBox.getChildren() :
                                    attributesBox.getChildren();

            // search for label in selected list of labels
            // and removes from the list
            Label foundLabel = (Label) children.stream()
                    .filter(x -> Objects.equals(((Label) x).getText(), attr.toString()))
                    .findFirst().orElse(null);

            children.remove(foundLabel);
        }
        clearVBoxes();
    }

    /**
     * Removes attribute or operation {@code VBoxes} when the layout is empty.
     */
    private void clearVBoxes() {
        if (operationsBox != null && operationsBox.getChildren().size() == 0) {
            baseLayout.getChildren().remove(operationsBox);
            operationsBox = null;
        }
        if (attributesBox != null && attributesBox.getChildren().size() == 0) {
            baseLayout.getChildren().remove(attributesBox);
            attributesBox = null;
        }
    }

    public GClassElement(Pane canvas, UMLClass model, GClassDiagram owner) {
        // TODO: better model handling
        this.model = Objects.requireNonNull(model);
        this.owner = Objects.requireNonNull(owner);

        // init base group layout and set mouse events
        // set negative spacing to unite borders between each sections
        baseLayout = new VBox(-3);
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

        // top rectangle (class name); bind text content with class name
        nameBox = new VBox();
        Label name = new Label();

        nameBox.setAlignment(Pos.CENTER);
        name.textProperty().bind(model.getNameProperty());
        // bind to abstraction -> abstract classes are italic
        name.fontProperty().bind(Bindings.createObjectBinding(() -> {
            Font font;
            if (model.isAbstract()) {
                font = Font.font("Arial", FontPosture.ITALIC, 15);
            } else {
                font = Font.font("Arial", 15);
            }
            return font;
        }, model.getAbstractProperty()));
        nameBox.setPadding(new Insets(3, 3, 3, 3));
        nameBox.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-border-width: 3");

        // TODO: how to remove if not abstract
        Label interfaceText = new Label("<<interface>>");
        interfaceText.textProperty().bind(model.getStereotypeProperty());
        interfaceText.visibleProperty().bind(model.getAbstractProperty());
        nameBox.getChildren().add(interfaceText);
        nameBox.getChildren().add(name);

        // add attributes and operations
        List<UMLAttribute> lofAttr = model.getAttributes();
        for (var item : lofAttr) {
            Label attribute = new Label();
            attribute.textProperty().bind(item.getToStringProperty());
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

        // put each sections to main box
        baseLayout.getChildren().add(nameBox);
        if (attributesBox != null)
            baseLayout.getChildren().add(attributesBox);
        if (operationsBox != null)
            baseLayout.getChildren().add(operationsBox);

        baseLayout.setTranslateX(GClassElement.initPosX * 80);
        baseLayout.setTranslateY(GClassElement.initPosY * 80);
        if (GClassElement.initPosX * 80 + 80> canvas.getWidth()) {
            GClassElement.initPosX = 0;
            GClassElement.initPosY++;
        } else {
            GClassElement.initPosX++;
        }

        // place it on canvas
        canvas.getChildren().addAll(baseLayout);
    }
}
