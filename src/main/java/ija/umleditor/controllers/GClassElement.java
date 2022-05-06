/**
 * @brief Creates class element.
 * Creates instance of GClassElement.
 * This instance can be moved or selected.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GClassElement.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.UMLAttribute;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLClassifier;
import ija.umleditor.models.UMLOperation;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.util.List;
import java.util.Objects;

/**
 * Graphical representation of class element.
 */
public class GClassElement {

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

    /**
     * Changes appearance of element if it is selected, otherwise sets default appearance.
     * @param b Element is selected
     */
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

    /**
     * Gets model of instance of UMLClass.
     * @return Model of instance of UMLClass
     */
    public UMLClass getModel() {
        return model;
    }

    /**
     * Gets base layout in form of VBox.
     * @return Instance of VBox.
     */
    public VBox getBaseLayout() {
        return baseLayout;
    }

    /**
     * Gets owner of GClassDiagram.
     * @return Owner of GClassDiagram
     */
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
        if (!model.addAttribute(attr)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText("Cannot create operation. Attribute/operation with identical name already exists within selected class");
            a.show();
            return;
        }
        if (attributesBox == null) {
            attributesBox = new VBox();
            attributesBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            baseLayout.getChildren().add(1, attributesBox);
        }
        // add attribute to set of attributes
        // when attribute with given name already exists
        // insertion is aborted and function returns false
        Label attribute = new Label();
        attribute.textProperty().bind(attr.getToStringProperty());
        attribute.setAlignment(Pos.CENTER_LEFT);
        attribute.setPadding(new Insets(3, 3, 3, 3));
        attributesBox.getChildren().add(attribute);
    }

    /**
     * Adds operation to class element.
     * If class already contains given operation or operation with same name
     * error dialog window will be shown.
     * @param oper Instance of operation
     */
    public void addOperation(UMLOperation oper) {
        if (!model.addAttribute(oper)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText("Cannot create operation. Attribute/operation with identical name already exists within selected class");
            a.show();
            return;
        }

        if (operationsBox == null) {
            operationsBox = new VBox();
            operationsBox.setStyle("-fx-border-style: dashed dashed dashed dashed; -fx-border-width: 3; -fx-background-color: rgb(173,216,230)");
            baseLayout.getChildren().add(operationsBox);
        }
        // add operation to set of attributes
        // when operation with given name already exists
        // insertion is aborted and function returns false
        Label operationLabel = new Label();
        operationLabel.textProperty().bind(oper.getToStringProperty());
        operationLabel.setAlignment(Pos.CENTER_LEFT);
        operationLabel.setPadding(new Insets(3, 3, 3, 3));
        operationsBox.getChildren().add(operationLabel);
    }

    /**
     * Removes attribute from class element with given attribute.
     * Nothing happens if given attribute is not in class element.
     * @param attr Instance of attribute/operation in the class element
     */
    public void removeAttribute(UMLAttribute attr) {
        // UMLAttribute foundAttr = model.removeAttribute(attr.getName());
        // if (foundAttr != null) {
            // select list of nodes depending on attribute type
        List<Node> children = attr instanceof UMLOperation ?
                operationsBox.getChildren() :
                attributesBox.getChildren();

        // search for label in selected list of labels
        // and removes from the list
        Label foundLabel = (Label) children.stream()
                .filter(x -> Objects.equals(((Label) x).getText(), attr.getToStringProperty().getValue()))
                .findFirst().orElse(null);

        children.remove(foundLabel);
        model.removeAttribute(attr);
        clearVBoxes();
    }

    /**
     * Removes attribute or operation {@code VBoxes} when the layout is empty.
     */
    public void clearVBoxes() {
        if (operationsBox != null && operationsBox.getChildren().size() == 0) {
            baseLayout.getChildren().remove(operationsBox);
            operationsBox = null;
        }
        if (attributesBox != null && attributesBox.getChildren().size() == 0) {
            baseLayout.getChildren().remove(attributesBox);
            attributesBox = null;
        }
    }

    /**
     * Create top box for name and stereotype.
     */
    private void createNameBox() {

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

        Label interfaceText = new Label("<<interface>>");
        interfaceText.textProperty().bind(model.getStereotypeProperty());
        interfaceText.visibleProperty().bind(model.getAbstractProperty());
        nameBox.getChildren().add(interfaceText);
        nameBox.getChildren().add(name);

    }

    /**
     * Class constructor.
     * @param canvas Main canvas to put GClassifier on.
     * @param model Model of this gClassifier.
     * @param owner Parent GClassDiagram instance.
     */
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
            model.setX(baseLayout.getTranslateX());
            model.setY(baseLayout.getTranslateY());
            // store initial coordinates of mouse press
            posX = ev.getX();
            posY = ev.getY();
            ev.consume();
        });
        baseLayout.setOnMouseDragged(ev -> {
            // ignore element selection when element is dragged
            selectable = false;
            // more relatively stored mouse coordination
            baseLayout.setTranslateX(baseLayout.getTranslateX() - posX + ev.getX());
            baseLayout.setTranslateY(baseLayout.getTranslateY() - posY + ev.getY());
            ev.consume();
        });
        // create undo and redo action for moving selected
        baseLayout.setOnMouseReleased(ev -> {
            // ignore clicks
            if (selectable)
                return;
            owner.getCommandBuilder().execute(new ICommand() {
                final double origX = model.getX();
                final double origY = model.getY();
                final double newX  = baseLayout.getTranslateX();
                final double newY  = baseLayout.getTranslateY();
                @Override
                public void undo() {
                    model.setX(origX);
                    model.setY(origY);
                    baseLayout.setTranslateX(origX);
                    baseLayout.setTranslateY(origY);
                }

                @Override
                public void redo() {
                    execute();
                    baseLayout.setTranslateX(newX);
                    baseLayout.setTranslateY(newY);
                }

                @Override
                public void execute() {
                    model.setX(newX);
                    model.setY(newY);
                }
            });
            ev.consume();
        });

        createNameBox();

        loadAttributes();

        // put each sections to main box
        baseLayout.getChildren().add(nameBox);
        if (attributesBox != null)
            baseLayout.getChildren().add(attributesBox);
        if (operationsBox != null)
            baseLayout.getChildren().add(operationsBox);

        baseLayout.setTranslateX(model.getX());
        baseLayout.setTranslateY(model.getY());

        // place it on canvas
        canvas.getChildren().addAll(baseLayout);
    }

    /**
     * Create box from attributes and operations.
     */
    private void loadAttributes() {
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
    }
}
