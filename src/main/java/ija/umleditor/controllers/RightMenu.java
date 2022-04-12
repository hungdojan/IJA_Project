package ija.umleditor.controllers;

import ija.umleditor.models.UMLAttribute;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLClassifier;
import ija.umleditor.models.UMLOperation;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class RightMenu {
    private Accordion base;
    private GClassElement baseElement;

    public Accordion getBase() {
        return base;
    }

    public RightMenu(GClassElement baseElement, Pane root) {
        base = new Accordion();

        this.baseElement = baseElement;
        // TODO: create tiledPanes

        // edit name
        TitledPane nameTP = new TitledPane();
        nameTP.setText("Name");

        Pane nameP = new Pane();
        HBox editName = new HBox();
        TextField nameField = new TextField(baseElement.getModel().getName());
        Button saveNameButton = new Button("Save");

        editName.getChildren().addAll(nameField, saveNameButton);
        nameP.getChildren().add(editName);
        nameTP.setContent(nameP);
        base.getPanes().add(nameTP);

        // attributes layout
        TitledPane attributeTP = new TitledPane();
        attributeTP.setText("Attributes");
        Pane attributeP = new Pane();
        VBox attributesBox = new VBox();

        // add button
        Button addAttrButton = new Button("Add attribute");
        addAttrButton.setAlignment(Pos.CENTER);
        addAttrButton.setMaxWidth(Double.MAX_VALUE);
        addAttrButton.setStyle("-fx-background-radius: 15px");

        // operations layout
        TitledPane operationTP = new TitledPane();
        operationTP.setText("Operations");
        Pane operationP = new Pane();
        VBox operationsBox = new VBox();

        // add button
        Button addOpButton = new Button("Add operation");
        addOpButton.setAlignment(Pos.CENTER);
        addOpButton.setMaxWidth(Double.MAX_VALUE);
        addOpButton.setStyle("-fx-background-radius: 15px");


        // get list of attributes and operations
        List<UMLAttribute> lofAttributes = ((UMLClass) baseElement.getModel()).getAttributes();
        for (var item : lofAttributes) {
            // create HBox with editable name of attr/op, delete button and save button
            HBox editBox = new HBox();
            Button deleteButton = new Button("Delete");
            Button saveButton = new Button("Save");
            if (item instanceof UMLOperation) {
                TextField opField = new TextField(item.getName());
                editBox.getChildren().addAll(opField, deleteButton, saveButton);
                operationsBox.getChildren().add(editBox);
            } else {
                TextField attrField = new TextField(item.getName());
                editBox.getChildren().addAll(attrField, deleteButton, saveButton);
                attributesBox.getChildren().add(editBox);
            }
        }

        //connect attribute layout
        attributesBox.getChildren().add(addAttrButton);
        attributeP.getChildren().add(attributesBox);
        attributeTP.setContent(attributeP);
        base.getPanes().add(attributeTP);

        // connect operation layout
        operationsBox.getChildren().add(addOpButton);
        operationP.getChildren().add(operationsBox);
        operationTP.setContent(operationP);
        base.getPanes().add(operationTP);

        HBox.setHgrow(base, Priority.ALWAYS);
        root.getChildren().add(base);
    }

    public void remove(Pane root) {
        root.getChildren().remove(base);
    }
}
