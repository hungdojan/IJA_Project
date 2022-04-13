package ija.umleditor.controllers;

import ija.umleditor.models.*;
import ija.umleditor.template.Templates;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Objects;

public class RightMenu {
    private Accordion base;
    private GClassElement baseElement;
    private VBox operationsBox;
    private VBox attributesBox;

    public Accordion getBase() {
        return base;
    }

    private HBox createHBox(Node... nodes) {
        HBox hb = new HBox();
        hb.getChildren().addAll(nodes);
        return hb;
    }

    /**
     * Creates TitledPane that contains operation's parameters.
     * @param operation Base operation
     * @return Created TitledPane instance
     */
    private TitledPane createAttributeTitledPane(UMLOperation operation) {
        TitledPane attributeTP = new TitledPane();
        attributeTP.setText("Parameters");
        VBox.setVgrow(attributeTP, Priority.ALWAYS);
        VBox attributesVBox = new VBox();
        Button addParamButton = new Button("Add parameter");
        addParamButton.setAlignment(Pos.CENTER);
        addParamButton.setStyle("-fx-background-radius: 15px");

        for (var item : operation.getOperationParameters()) {
            var itemHBox = createAttributeHBox(item, null);
            ((Button) itemHBox.getChildren().get(2)).setOnAction(e -> {
                // TODO: remove boxes when empty
                operation.removeParameter(item);
                operation.update();
                attributesVBox.getChildren().remove(itemHBox);
            });
            ((TextField) itemHBox.getChildren().get(0)).setOnAction(e -> {
                if (((TextField) itemHBox.getChildren().get(0)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                // FIXME: operator is not updating
                item.setType(new UMLClassifier(((TextField) itemHBox.getChildren().get(0)).getText()));
                // updates to string value
                operation.update();
            });
            ((TextField) itemHBox.getChildren().get(1)).setOnAction(e -> {
                if (((TextField) itemHBox.getChildren().get(1)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                if (!operation.updateParameter(item.getName(), ((TextField) itemHBox.getChildren().get(1)).getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Attribute with given name already exists.");
                    alert.show();
                }
            });
            attributesVBox.getChildren().add(itemHBox);
        }
        attributesVBox.getChildren().add(addParamButton);
        addParamButton.setOnAction(ev -> {
            UMLAttribute newAttr = Templates.createParameter(operation, baseElement.getOwner().getModel());
            HBox attrItemHBox = createAttributeHBox(newAttr, null);
            attributesVBox.getChildren().add(attributesVBox.getChildren().size()-1, attrItemHBox);
            operation.addParameters(newAttr);

            ((Button) attrItemHBox.getChildren().get(2)).setOnAction(e -> {
                // TODO: remove boxes when empty
                operation.removeParameter(newAttr);
                operation.update();
                attributesVBox.getChildren().remove(attrItemHBox);
            });
            ((TextField) attrItemHBox.getChildren().get(0)).setOnAction(e -> {
                if (((TextField) attrItemHBox.getChildren().get(0)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                // FIXME: operator is not updating
                newAttr.setType(new UMLClassifier(((TextField) attrItemHBox.getChildren().get(0)).getText()));
                // updates to string value
                operation.update();
            });
            ((TextField) attrItemHBox.getChildren().get(1)).setOnAction(e -> {
                if (((TextField) attrItemHBox.getChildren().get(1)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                if (!operation.updateParameter(newAttr.getName(), ((TextField) attrItemHBox.getChildren().get(1)).getText())) {
                    // TODO: alert attribute already exists
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Attribute with given name already exists.");
                    alert.show();
                }
            });

        });
        attributeTP.setContent(attributesVBox);
        attributeTP.setExpanded(false);

        return attributeTP;
    }

    private HBox createAttributeHBox(UMLAttribute item, TitledPane listOTitledPane) {
        TextField typeField = new TextField(item.getType().getName());
        TextField textField = new TextField(item.getName());
        Button deleteButton = new Button("Delete");
        HBox editBox = createHBox(typeField, textField, deleteButton);
        HBox.setHgrow(textField, Priority.ALWAYS);
        // remove attribute event
        deleteButton.setOnAction(ev -> {
            // TODO: remove boxes when empty
            baseElement.removeAttribute(item);
            if (item instanceof UMLOperation) {
                operationsBox.getChildren().remove(editBox);
            } else {
                attributesBox.getChildren().remove(editBox);
            }
            if (listOTitledPane != null) {
                ((VBox) listOTitledPane.getParent()).getChildren().remove(listOTitledPane);
            }
        });
        typeField.setOnAction(ev -> {
            if (typeField.getText().isBlank()) {
                // TODO: alert
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Blank space");
                warning.setContentText("Text field cannot be empty");
                warning.show();
                return;
            }
            item.setType(new UMLClassifier(typeField.getText()));
        });
        textField.setOnAction(ev -> {
            if (textField.getText().isBlank()) {
                // TODO: alert
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Blank space");
                warning.setContentText("Text field cannot be empty");
                warning.show();
                return;
            }
            UMLClass modelClass = baseElement.getModel();

            if (!modelClass.updateAttributeName(item.getName(), textField.getText())) {
                // TODO: alert attribute already exists
            }
        });
        return editBox;
    }

    public RightMenu(GClassElement baseElement, Pane root) {
        base = new Accordion();
        base.setMaxWidth(450);

        this.baseElement = baseElement;
        // TODO: create tiledPanes

        // edit name and stereotype
        TitledPane nameTP = new TitledPane();
        nameTP.setText("Name");
        GridPane nameGrid = new GridPane();

        // HBox with stereotype header, text field and save button
        Label stereotype = new Label("Stereotype:");
        stereotype.setStyle("-fx-font-weight: bold");
        TextField stereotypeField = new TextField(baseElement.getModel().getStereotype());
        stereotypeField.promptTextProperty().bindBidirectional(baseElement.getModel().getStereotypeProperty());
        Button saveStereoButton = new Button("Save");
        // TODO: remove save button and add setOnAction for both textFields
        saveStereoButton.setOnAction(ev -> baseElement.getModel().setStereotype(stereotypeField.getText()));



        // check button for setting object abstract
        Label isAbstract = new Label("Abstract:");
        isAbstract.setStyle("-fx-font-weight: bold");
        CheckBox abstractCheck = new CheckBox();

        // object is abstract
        if (baseElement.getModel().isAbstract()) {
            stereotypeField.setDisable(false);
            saveStereoButton.setVisible(true);
            abstractCheck.setSelected(true);
        } else {
            stereotypeField.setDisable(true);
            saveStereoButton.setVisible(false);
        }
        abstractCheck.setOnAction(ev -> {
            baseElement.getModel().setAbstract(abstractCheck.isSelected());
            stereotypeField.setDisable(!abstractCheck.isSelected());
            saveStereoButton.setVisible(abstractCheck.isSelected());
        });

        // name label
        Label name = new Label("Name:");
        name.setStyle("-fx-font-weight: bold");
        TextField nameField = new TextField(baseElement.getModel().getName());
        Button saveNameButton = new Button("Save");
        saveNameButton.setOnAction(ev -> {
            ClassDiagram baseClassDiagramModel = baseElement.getOwner().getModel();
            if (!baseClassDiagramModel.changeClassifierName(baseElement.getModel().getName(), nameField.getText())) {
                // TODO: error
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Classifier with name " + nameField.getText() + " is already in use");
                alert.show();
            }
        });

        // add second row to grid
        nameGrid.add(isAbstract, 0, 0);
        nameGrid.add(abstractCheck, 1, 0);

        // add first row to grid
        nameGrid.add(stereotype, 0, 1);
        nameGrid.add(stereotypeField, 0, 2);
        nameGrid.add(saveStereoButton, 1, 2);

        // add third row to grid
        nameGrid.add(name, 0, 3);
        nameGrid.add(nameField, 0, 4);
        nameGrid.add(saveNameButton, 1, 4);

        nameTP.setContent(nameGrid);
        base.getPanes().add(nameTP);

        // attributes layout
        TitledPane attributeTP = new TitledPane();
        attributeTP.setText("Attributes");
        ScrollPane attributeSP = new ScrollPane();
        attributesBox = new VBox();

        // add attribute button
        Button addAttrButton = new Button("Add attribute");
        addAttrButton.setAlignment(Pos.CENTER);
        addAttrButton.setStyle("-fx-background-radius: 15px");
        addAttrButton.setOnAction(ev -> {
            UMLAttribute attr = Templates.createAttribute(baseElement.getModel(), baseElement.getOwner().getModel());
            HBox newAttribute = createAttributeHBox(attr, null);
            baseElement.addAttribute(attr);
            attributesBox.getChildren().add(attributesBox.getChildren().size()-1, newAttribute);
        });

        // operations layout
        TitledPane operationTP = new TitledPane();
        operationTP.setText("Operations");
        ScrollPane operationSP = new ScrollPane();
        operationsBox = new VBox();

        // add operation button
        Button addOpButton = new Button("Add operation");
        addOpButton.setAlignment(Pos.CENTER);
        addOpButton.setStyle("-fx-background-radius: 15px");
        addOpButton.setOnAction(ev -> {
            UMLOperation attr = Templates.createOperation(baseElement.getModel(), baseElement.getOwner().getModel());
            TitledPane listOfTitleAttributes = createAttributeTitledPane(attr);
            HBox newAttribute = createAttributeHBox(attr, listOfTitleAttributes);
            baseElement.addOperation(attr);
            operationsBox.getChildren().add(operationsBox.getChildren().size()-1, newAttribute);
            operationsBox.getChildren().add(operationsBox.getChildren().size()-1, listOfTitleAttributes);
        });


        // get list of attributes and operations
        List<UMLAttribute> lofAttributes = baseElement.getModel().getAttributes();
        for (var item : lofAttributes) {
            if (item instanceof UMLOperation) {
                TitledPane listOfAttributesPane = createAttributeTitledPane((UMLOperation) item);
                HBox editBox = createAttributeHBox(item, listOfAttributesPane);
                operationsBox.getChildren().addAll(editBox, listOfAttributesPane);
            } else {
                HBox editBox = createAttributeHBox(item, null);
                attributesBox.getChildren().add(editBox);
            }
        }

        //connect attribute layout
        attributesBox.getChildren().add(addAttrButton);
        HBox.setHgrow(addAttrButton, Priority.ALWAYS);
        attributesBox.setFillWidth(true);
        attributeSP.setContent(attributesBox);
        attributeSP.setFitToWidth(true);
        attributeSP.setFitToHeight(true);

        attributeTP.setContent(attributeSP);
        base.getPanes().add(attributeTP);

        // connect operation layout
        operationsBox.getChildren().add(addOpButton);
        HBox.setHgrow(addOpButton, Priority.ALWAYS);
        operationsBox.setFillWidth(true);
        operationSP.setContent(operationsBox);
        operationSP.setFitToWidth(true);
        operationSP.setFitToHeight(true);
        operationSP.fitToWidthProperty();
        operationTP.setContent(operationSP);
        base.getPanes().add(operationTP);

        // create relations layout
        TitledPane relationTP = new TitledPane();
        relationTP.setText("Relations");
        VBox relationsVBox = new VBox();

        // create relations
        for (var item : baseElement.getModel().getRelations()) {
            HBox relationHBox = new HBox();
            Button deleteRelationButton = new Button("Delete");
//            TextField srcField = new TextField(item.getSrc().getName());

            String content;
            if (Objects.equals(baseElement.getModel().getName(), item.getDest().getName())) {
                content = item.getSrc().getName();
            }
            else {
                content = item.getDest().getName();
            }

            TextField destField = new TextField(content);
            destField.setEditable(false);
            deleteRelationButton.setOnAction(ev -> {
                var relationClass = baseElement.getOwner().getModel().getClass(destField.getText());
                if (relationClass == null)
                    return;
                baseElement.getModel().removeRelationWithClass(relationClass);
                baseElement.getOwner().removeRelation(baseElement.getOwner().getRelation(baseElement.getModel(), relationClass));
                relationsVBox.getChildren().remove(relationHBox);
                // TODO: remove gRelation from the canvas
            });
            ClassDiagram baseClassDiagram = baseElement.getOwner().getModel();
            if (baseElement.getOwner().getRelation((UMLClass) item.getSrc(), (UMLClass) item.getDest()) == null) {
                baseElement.getOwner().addRelation(new GRelation(baseElement, baseElement.getOwner().getClassElement(baseClassDiagram.getClass(content)), baseElement.getOwner().getCanvas()));
            }
            relationHBox.getChildren().addAll(destField, deleteRelationButton);
            relationsVBox.getChildren().add(relationHBox);
        }

        Button addRelationButton = new Button("Add relation");
        addRelationButton.setAlignment(Pos.CENTER);
        addRelationButton.setStyle("-fx-background-radius: 15px");
        addRelationButton.setOnAction(ev -> {
            // create a constraint so that user can only add max one new relations
            // if the text field is still editable (meaning no relation was created)
            // no new row will be created
            if (relationsVBox.getChildren().size() > 1) {
                HBox hBox = (HBox) relationsVBox.getChildren().get(relationsVBox.getChildren().size() - 2);
                TextField lastTextField = (TextField) hBox.getChildren().get(0);
                if (lastTextField.isEditable())
                    return;
            }
            HBox relationHBox = new HBox();
//            TextField srcField = new TextField();
            TextField destField = new TextField();
            Button deleteRelationButton = new Button("Delete");
            destField.setOnAction(e -> {
                var classElement = baseElement.getOwner().getModel().getClass(destField.getText());
                if (classElement == null) {
                    // TODO: alert error
                } else {
                    if (!baseElement.getModel().addRelation(classElement, RelationType.ASSOCIATION)) {
                        // TODO: alert existing relation
                        return;
                    }
                    baseElement.getOwner().addRelation(new GRelation(baseElement, baseElement.getOwner().getClassElement(classElement), baseElement.getOwner().getCanvas()));
                    destField.setEditable(false);
                }
            });
            deleteRelationButton.setOnAction(e -> {
                if (destField.getText().isBlank() || destField.isEditable())
                    return;
                var relationClass = baseElement.getOwner().getModel().getClass(destField.getText());
                if (relationClass == null)
                    return;
                baseElement.getModel().removeRelationWithClass(relationClass);
                baseElement.getOwner().removeRelation(baseElement.getOwner().getRelation(baseElement.getModel(), relationClass));
                relationsVBox.getChildren().remove(relationHBox);
                // TODO: remove gRelation from the canvas
            });
            relationHBox.getChildren().addAll(destField, deleteRelationButton);
            relationsVBox.getChildren().add(relationsVBox.getChildren().size()-1, relationHBox);
        });

        relationsVBox.getChildren().add(addRelationButton);
        relationTP.setContent(relationsVBox);
        base.getPanes().add(relationTP);

        HBox.setHgrow(base, Priority.ALWAYS);
        root.getChildren().add(base);
    }


    public void remove(Pane root) {
        root.getChildren().remove(base);
    }

}
