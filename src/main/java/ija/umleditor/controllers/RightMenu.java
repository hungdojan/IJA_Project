/**
 * @brief Creates right menu if element is selected.
 * Displays name, attributes, operations and relations of selected element.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file RightMenu.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.*;
import ija.umleditor.template.Templates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

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
        addParamButton.setMaxWidth(Double.MAX_VALUE);
        addParamButton.setAlignment(Pos.CENTER);
        addParamButton.setStyle("-fx-background-radius: 15px");

        for (var item : operation.getOperationParameters()) {
            var itemHBox = createAttributeHBox(item, null, true);
            var deleteButton = (Button) itemHBox.getChildren().get(2);
            var typeField = (TextField) itemHBox.getChildren().get(0);
            var nameField = (TextField) itemHBox.getChildren().get(1);
            item.parent = operation; // assign parent operation to notify parent operation when name is updated
            // delete button action
            deleteButton.setOnAction(e -> {
                // TODO: remove boxes when empty
                operation.removeParameter(item);
                operation.update();
                attributesVBox.getChildren().remove(itemHBox);
            });
            // parameter type text field
            typeField.setOnAction(e -> {
                if (((TextField) itemHBox.getChildren().get(0)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                var type = baseElement.getOwner().getModel().getClassifier(typeField.getText());
                if (type == null) {
                    type = ClassDiagram.createClassifier(typeField.getText(), false);
                    baseElement.getOwner().getModel().addClassifier(type);
                }
                item.setType(type);
                // FIXME: operator is not updating
                // item.setType(new UMLClassifier(((TextField) itemHBox.getChildren().get(0)).getText()));
                // updates to string value
                operation.update();
            });
            // parameter name text field
            nameField.setOnAction(e -> {
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
            newAttr.parent = operation; // assign parent operation to notify parent operation when name is updated
            HBox attrItemHBox = createAttributeHBox(newAttr, null, false);
            var deleteButton = (Button) attrItemHBox.getChildren().get(2);
            var typeField = (TextField) attrItemHBox.getChildren().get(0);
            var nameField = (TextField) attrItemHBox.getChildren().get(1);
            attributesVBox.getChildren().add(attributesVBox.getChildren().size()-1, attrItemHBox);
            operation.addParameter(newAttr);

            deleteButton.setOnAction(e -> {
                // TODO: remove boxes when empty
                operation.removeParameter(newAttr);
                operation.update();
                attributesVBox.getChildren().remove(attrItemHBox);
            });
            // typeField.textProperty().bind(operation.getType().getNameProperty());
            typeField.setOnAction(e -> {
                if (((TextField) attrItemHBox.getChildren().get(0)).getText().isBlank()) {
                    // TODO: alert
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Blank space");
                    warning.setContentText("Text field cannot be empty");
                    warning.show();
                    return;
                }
                // FIXME: operator is not updating
                // newAttr.setType(new UMLClassifier(((TextField) attrItemHBox.getChildren().get(0)).getText()));
                // // updates to string value
                // operation.update();
                var type = baseElement.getOwner().getModel().getClassifier(typeField.getText());
                if (type == null) {
                    type = ClassDiagram.createClassifier(typeField.getText(), false);
                    baseElement.getOwner().getModel().addClassifier(type);
                }
                newAttr.setType(type);
                // FIXME: operator is not updating
                // item.setType(new UMLClassifier(((TextField) itemHBox.getChildren().get(0)).getText()));
                // updates to string value
                operation.update();
            });
            nameField.setOnAction(e -> {
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

    /**
     * Creates attribute box with its visibility, type, name and delete button. 
     * @param item Instance of UMLAttribute
     * @param parameterTitledPane TitlePane containing parameters of operation
     * @param setVisibility If anything other than parameter
     * @return
     */
    private HBox createAttributeHBox(UMLAttribute item, TitledPane parameterTitledPane, boolean setVisibility) {
        TextField typeField = new TextField(item.getType().getName());
        TextField textField = new TextField(item.getName());
        Button deleteButton = new Button("Delete");
        deleteButton.setMinWidth(65);
        HBox editBox = createHBox(typeField, textField, deleteButton);
        if (setVisibility) {
            ObservableList<Character> options =
                    FXCollections.observableArrayList(
                            '+',
                            '-',
                            '~',
                            '#'
                    );
            ComboBox visibilityCB = new ComboBox(options);
            visibilityCB.setValue(options.stream().filter(x -> item.getVisibility() == x.charValue()).findFirst().orElse(options.get(0)));
            visibilityCB.setMinWidth(60);
            visibilityCB.setOnAction(ev -> {
                item.setVisibility((Character) visibilityCB.getValue());
            });
            editBox.getChildren().add(0, visibilityCB);
        }
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
            if (parameterTitledPane != null) {
                ((VBox) parameterTitledPane.getParent()).getChildren().remove(parameterTitledPane);
            }
        });
        // set model type
        typeField.setOnAction(ev -> {
            if (Objects.equals(typeField.getText(), "#UNDEF"))
                return;
            if (typeField.getText().isBlank()) {
                // TODO: alert
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Blank space");
                warning.setContentText("Text field cannot be empty");
                warning.show();
                return;
            }
            // search for type instance in the class diagram
            // create new classifier if not found
            var type = baseElement.getOwner().getModel().getClassifier(typeField.getText());
            if (type == null) {
                type = ClassDiagram.createClassifier(typeField.getText(), false);
                baseElement.getOwner().getModel().addClassifier(type);
            }
            item.setType(type);
        });
        // set model name
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

    /**
     * Class constructor.
     * @param baseElement Reference gClassElement instance.
     * @param root Root pane to append right panel on.
     */
    public RightMenu(GClassElement baseElement, Pane root) {
        base = new Accordion();
        base.setMaxWidth(450);

        this.baseElement = baseElement;
        // TODO: create tiledPanes

        // edit name and stereotype
        TitledPane nameTP = new TitledPane();
        nameTP.setText("Name");
        GridPane nameGrid = createTopHeaderSection(baseElement);
        nameTP.setContent(nameGrid);
        base.getPanes().add(nameTP);

        createAttributeOperationTitledPanes(baseElement);

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
        addRelationButton.setMaxWidth(Double.MAX_VALUE);
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
            relationHBox.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(destField, Priority.ALWAYS);
            relationsVBox.getChildren().add(relationsVBox.getChildren().size()-1, relationHBox);
        });

        relationsVBox.getChildren().add(addRelationButton);
        relationTP.setContent(relationsVBox);
        base.getPanes().add(relationTP);

        HBox.setHgrow(base, Priority.ALWAYS);
        root.getChildren().add(base);
    }

    /**
     * Creates part of right menu containing attributes and operations
     * @param baseElement Selected instance of GClassElement
     */
    private void createAttributeOperationTitledPanes(GClassElement baseElement) {
        // attributes layout
        TitledPane attributeTP = new TitledPane();
        attributeTP.setText("Attributes");
        ScrollPane attributeSP = new ScrollPane();
        attributesBox = new VBox();

        // operations layout
        TitledPane operationTP = new TitledPane();
        operationTP.setText("Operations");
        ScrollPane operationSP = new ScrollPane();
        operationsBox = new VBox();


        // get list of attributes and operations
        List<UMLAttribute> lofAttributes = baseElement.getModel().getAttributes();
        for (var item : lofAttributes) {
            if (item instanceof UMLOperation) {
                TitledPane listOfAttributesPane = createAttributeTitledPane((UMLOperation) item);
                HBox editBox = createAttributeHBox(item, listOfAttributesPane, true);
                operationsBox.getChildren().addAll(editBox, listOfAttributesPane);
            } else {
                HBox editBox = createAttributeHBox(item, null, true);
                attributesBox.getChildren().add(editBox);
            }
        }

        // add attribute button
        Button addAttrButton = new Button("Add attribute");
        addAttrButton.setAlignment(Pos.CENTER);
        addAttrButton.setMaxWidth(Double.MAX_VALUE);
        addAttrButton.setStyle("-fx-background-radius: 15px");
        // button event that adds new event
        addAttrButton.setOnAction(ev -> {
            UMLAttribute attr = Templates.createAttribute(baseElement.getModel(), baseElement.getOwner().getModel());
            // check for collision
            if (baseElement.getModel().getAttribute(attr.getName()) != null)
                return;

            HBox newAttribute = createAttributeHBox(attr, null, true);
            // create undo and redo action for adding attribute
            baseElement.getOwner().getCommandBuilder().execute(new ICommand() {
                final int linePosition = attributesBox.getChildren().size()-1;
                @Override
                public void undo() {
                    baseElement.removeAttribute(attr);
                    baseElement.getModel().removeAttribute(attr);
                    attributesBox.getChildren().remove(newAttribute);
                }

                @Override
                public void redo() {
                    execute();
                }

                @Override
                public void execute() {
                    baseElement.addAttribute(attr);
                    baseElement.getModel().addAttribute(attr);
                    attributesBox.getChildren().add(linePosition, newAttribute);
                }
            });
        });

        // add operation button
        Button addOpButton = new Button("Add operation");
        addOpButton.setAlignment(Pos.CENTER);
        addOpButton.setMaxWidth(Double.MAX_VALUE);
        addOpButton.setStyle("-fx-background-radius: 15px");
        addOpButton.setOnAction(ev -> {
            UMLOperation attr = Templates.createOperation(baseElement.getModel(), baseElement.getOwner().getModel());
            // check for collision
            if (baseElement.getModel().getAttribute(attr.getName()) != null)
                return;
            TitledPane listOfTitleAttributes = createAttributeTitledPane(attr);
            HBox newAttribute = createAttributeHBox(attr, listOfTitleAttributes, true);
            // create undo and redo action
            baseElement.getOwner().getCommandBuilder().execute(new ICommand() {
                @Override
                public void undo() {
                    baseElement.removeAttribute(attr);
                    baseElement.getModel().removeAttribute(attr);
                    operationsBox.getChildren().removeAll(newAttribute, listOfTitleAttributes);
                }

                @Override
                public void redo() {
                    execute();
                }

                @Override
                public void execute() {
                    baseElement.addOperation(attr);
                    baseElement.getModel().addAttribute(attr);
                    operationsBox.getChildren().add(operationsBox.getChildren().size()-1, newAttribute);
                    operationsBox.getChildren().add(operationsBox.getChildren().size()-1, listOfTitleAttributes);
                }
            });
        });

        //connect attribute layout
        attributesBox.getChildren().add(addAttrButton);
        HBox.setHgrow(addAttrButton, Priority.ALWAYS);
        attributesBox.setFillWidth(true);
        attributeSP.setContent(attributesBox);
        attributeSP.setFitToWidth(true);
        attributeSP.setFitToHeight(true);
        attributeTP.setContent(attributeSP);

        // connect operation layout
        operationsBox.getChildren().add(addOpButton);
        HBox.setHgrow(addOpButton, Priority.ALWAYS);
        operationsBox.setFillWidth(true);
        operationSP.setContent(operationsBox);
        operationSP.setFitToWidth(true);
        operationSP.setFitToHeight(true);
        operationSP.fitToWidthProperty();
        operationTP.setContent(operationSP);
        base.getPanes().addAll(attributeTP, operationTP);
    }

    /**
     * Create layout with classifier's name and abstract options.
     *
     * @param baseElement Selected instance of GClassElement
     */
    private GridPane createTopHeaderSection(GClassElement baseElement) {
        GridPane nameGrid = new GridPane();

        // HBox with stereotype header, text field and save button
        Label stereotype = new Label("Stereotype:");
        stereotype.setStyle("-fx-font-weight: bold");
        // init stereotype text field
        TextField stereotypeField = new TextField(baseElement.getModel().getStereotype());
        stereotypeField.promptTextProperty().bindBidirectional(baseElement.getModel().getStereotypeProperty());
        stereotypeField.setOnAction(ev -> baseElement.getModel().setStereotype(stereotypeField.getText()));

        // check button for setting object abstract
        Label isAbstract = new Label("Abstract:");
        isAbstract.setStyle("-fx-font-weight: bold");
        CheckBox abstractCheck = new CheckBox();

        // object is abstract
        if (baseElement.getModel().isAbstract()) {
            stereotypeField.setDisable(false);
            abstractCheck.setSelected(true);
        } else {
            stereotypeField.setDisable(true);
        }
        abstractCheck.setOnAction(ev -> {
            baseElement.getModel().setAbstract(abstractCheck.isSelected());
            stereotypeField.setDisable(!abstractCheck.isSelected());
        });


        // name label
        Label name = new Label("Name:");
        name.setStyle("-fx-font-weight: bold");
        TextField nameField = new TextField(baseElement.getModel().getName());
        nameField.setOnAction(ev -> {
            var classDiagram = baseElement.getOwner().getModel();
            // check for collision of classifier names
            if (nameField.getText().equals(baseElement.getModel().getName())) {
                return;
            }
            if (classDiagram.getClassifier(nameField.getText()) != null) {
                // TODO: class can replace classifier
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Classifier with name " + nameField.getText() + " is already in use");
                alert.show();
            }

            // create undo and redo action for classifier name update
            baseElement.getOwner().getCommandBuilder().execute(new ICommand() {
                final String oldName = baseElement.getModel().getName();
                final String newName = nameField.getText();
                @Override
                public void undo() {
                    classDiagram.changeClassifierName(newName, oldName);
                }

                @Override
                public void redo() {
                    execute();
                }

                @Override
                public void execute() {
                    classDiagram.changeClassifierName(oldName, newName);
                }
            });
        });

        // add second row to grid
        nameGrid.add(isAbstract, 0, 0);
        nameGrid.add(abstractCheck, 1, 0);

        // add first row to grid
        nameGrid.add(stereotype, 0, 1);
        nameGrid.add(stereotypeField, 0, 2, 2, 1);

        // add third row to grid
        nameGrid.add(name, 0, 3);
        nameGrid.add(nameField, 0, 4, 2, 1);

        nameGrid.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameGrid, Priority.ALWAYS);
        return nameGrid;
    }

    /**
     * Removes right menu
     * @param root Pane to be removed
     */
    public void remove(Pane root) {
        root.getChildren().remove(base);
    }

}
