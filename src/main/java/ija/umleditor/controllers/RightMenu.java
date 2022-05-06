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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RightMenu {
    private Accordion base;
    private GClassElement baseElement;
    private VBox operationsBox;
    private VBox attributesBox;
    private ComboBox<String> typesCB;

    public Accordion getBase() {
        return base;
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node child : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(child) == col && GridPane.getRowIndex(child) == row) {
                return child;
            }
        }
        return null;
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
        var commandBuilder = baseElement.getOwner().getCommandBuilder();

        // load parameters
        for (var item : operation.getOperationParameters()) {
            HBox itemHBox = createParameterHBox(operation, attributesVBox, commandBuilder, item);
            attributesVBox.getChildren().add(itemHBox);
        }

        // create add parameter button
        Button addParamButton = new Button("Add parameter");
        addParamButton.setMaxWidth(Double.MAX_VALUE);
        addParamButton.setAlignment(Pos.CENTER);
        addParamButton.setStyle("-fx-background-radius: 15px");
        addParamButton.setOnAction(ev -> {
            UMLAttribute newAttr = Templates.createParameter(operation, baseElement.getOwner().getModel());
            HBox attrItemHBox = createParameterHBox(operation, attributesVBox, commandBuilder, newAttr);
            // create undo and redo action for parameter related actions
            commandBuilder.execute(new ICommand() {
                @Override
                public void undo() {
                    operation.removeParameter(newAttr);
                    attributesVBox.getChildren().remove(attrItemHBox);
                }

                @Override
                public void redo() {
                    operation.addParameter(newAttr);
                    attributesVBox.getChildren().add(attributesVBox.getChildren().size()-1, attrItemHBox);
                }

                @Override
                public void execute() {
                    operation.addParameter(newAttr);
                    attributesVBox.getChildren().add(attributesVBox.getChildren().size()-1, attrItemHBox);
                }
            });
        });
        attributesVBox.getChildren().add(addParamButton);
        attributeTP.setContent(attributesVBox);
        attributeTP.setExpanded(false);

        return attributeTP;
    }

    private HBox createParameterHBox(UMLOperation operation, VBox attributesVBox, CommandBuilder commandBuilder, UMLAttribute item) {
        var itemHBox = createAttributeHBox(item, null, true);
        var deleteButton = (Button) itemHBox.getChildren().get(3);
        var typeField = (TextField) itemHBox.getChildren().get(1);
        var nameField = (TextField) itemHBox.getChildren().get(2);
        // var visibleComboBox = (ComboBox) itemHBox.getChildren().get(0);
        // TODO: set visible combo box to right visibility
        item.parent = operation; // assign parent operation to notify parent operation when name is updated
        // delete button action
        deleteButton.setOnAction(e -> {
            // add undo and redo action of deleting parameter
            commandBuilder.execute(new ICommand() {
                final int index = attributesVBox.getChildren().indexOf(itemHBox);
                final HBox paramHBox = itemHBox;
                final UMLAttribute param = item;
                @Override
                public void undo() {
                    operation.getOperationParameters().add(index, param);
                    operation.update();
                    attributesVBox.getChildren().add(index, paramHBox);
                }

                @Override
                public void redo() {
                    operation.removeParameter(param);
                    operation.update();
                    attributesVBox.getChildren().remove(paramHBox);
                }

                @Override
                public void execute() {
                    operation.removeParameter(param);
                    operation.update();
                    attributesVBox.getChildren().remove(itemHBox);
                }
            });
            baseElement.clearVBoxes();
        });
        // parameter type text field
        typeField.setOnAction(e -> {
            // ignore black or unchanged fields
            if (typeField.getText().isBlank() || Objects.equals(typeField.getText(), item.getType().getName()))
                return;

            var type = baseElement.getOwner().getModel().getClassifier(typeField.getText());
            if (type == null) {
                type = ClassDiagram.createClassifier(typeField.getText(), false);
                baseElement.getOwner().getModel().addClassifier(type);
            }
            UMLClassifier finalType = type;
            commandBuilder.execute(new ICommand() {
                final UMLClassifier oldType = item.getType();
                final UMLClassifier newType = finalType;
                @Override
                public void undo() {
                    item.setType(oldType);
                    operation.update();
                    // TODO: sequence diagram update??
                }

                @Override
                public void redo() {
                    item.setType(newType);
                    operation.update();
                }

                @Override
                public void execute() {
                    item.setType(newType);
                    operation.update();
                }
            });
        });
        // parameter name text field
        nameField.setOnAction(e -> {
            // ignore black or unchanged fields
            if (nameField.getText().isBlank() || Objects.equals(nameField.getText(), item.getName()))
                return;

            // found parameter with same name
            if (operation.getParameterByName(nameField.getText()) != null)
                return;

            commandBuilder.execute(new ICommand() {
                final String oldName = item.getName();
                final String newName = nameField.getText();
                @Override
                public void undo() {
                    operation.updateParameter(newName, oldName);
                }

                @Override
                public void redo() {
                    operation.updateParameter(oldName, newName);
                }

                @Override
                public void execute() {
                    operation.updateParameter(oldName, newName);
                }
            });
        });
        return itemHBox;
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
            ComboBox<Character> visibilityCB = new ComboBox<>(options);
            visibilityCB.setValue(options.stream().filter(x -> item.getVisibility() == x.charValue()).findFirst().orElse(options.get(0)));
            visibilityCB.setMinWidth(60);
            visibilityCB.setOnAction(ev -> {
                item.setVisibility(visibilityCB.getValue());
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

        // already existing relations
        for (var item : baseElement.getModel().getRelations()) {
            GridPane relationGrid = new GridPane();
            Button deleteRelationButton = new Button("Delete");
//            TextField srcField = new TextField(item.getSrc().getName());

            String content;
            if (Objects.equals(baseElement.getModel().getName(), item.getDest().getName())) {
                content = item.getSrc().getName();
            }
            else {
                content = item.getDest().getName();
            }

            Label directionLabel = new Label();
            if (item.getSrc() == baseElement.getModel()) {
                directionLabel.setText("Destination:");
            } else {
                directionLabel.setText("Source:");
            }
            TextField directionField = new TextField(content);
            directionField.setEditable(false);
            deleteRelationButton.setOnAction(ev -> {
                var relationClass = baseElement.getOwner().getModel().getClass(directionField.getText());
                if (relationClass == null)
                    return;
                baseElement.getModel().removeRelationWithClass(relationClass);
                baseElement.getOwner().removeRelation(baseElement.getOwner().getRelation(baseElement.getModel(), relationClass));
                relationsVBox.getChildren().remove(relationGrid);
            });

            Label typeLabel = new Label("Type:");
//            Label pickedTypeLabel = new Label("selected type");
            ObservableList<String> relationTypes =
                    FXCollections.observableArrayList(EnumSet.allOf(RelationType.class).stream()
                            .map(RelationType::name).collect(Collectors.toList())
                    );
            typesCB = new ComboBox<>(relationTypes);
            typesCB.setValue(item.getRelationType().toString());
            typesCB.setOnAction(ev -> {
                // TODO:
                var gRelation = baseElement.getOwner().getRelation((UMLClass) item.getSrc(), (UMLClass) item.getDest());
                item.setRelationType(RelationType.valueOf(typesCB.getValue()));
                gRelation.updateColor();
            });

            Button swapButton = new Button("Swap");
            swapButton.setOnAction(ev -> {
                var gRelation = baseElement.getOwner().getRelation((UMLClass) item.getSrc(), (UMLClass) item.getDest());
                if (gRelation == null)
                    return;
                // TODO: update label
                if (Objects.equals(directionLabel.getText(), "Source:")) {
                    directionLabel.setText("Destination:");
                } else if (Objects.equals(directionLabel.getText(), "Destination:")) {
                    directionLabel.setText("Source:");
                }
                gRelation.swapDirection();
            });
            relationGrid.add(directionLabel, 0, 0);
            relationGrid.add(typeLabel, 1, 0);
            relationGrid.add(directionField, 0, 1);
            relationGrid.add(typesCB, 1, 1);
            relationGrid.add(swapButton, 2, 1);
            relationGrid.add(deleteRelationButton, 3, 1);

            ClassDiagram baseClassDiagram = baseElement.getOwner().getModel();
            if (baseElement.getOwner().getRelation((UMLClass) item.getSrc(), (UMLClass) item.getDest()) == null) {
                var src = (UMLClass) item.getSrc();
                var dst = (UMLClass) item.getDest();
                src.addRelation(dst, RelationType.valueOf(typesCB.getValue()));
                var rel = src.getRelation(dst);
                baseElement.getOwner().addRelation(new GRelation(
                        baseElement, baseElement.getOwner().getClassElement(baseClassDiagram.getClass(content)), baseElement.getOwner().getCanvas(), rel));
            }

//            relationGrid.getChildren().addAll(destField, deleteRelationButton);
            relationsVBox.getChildren().add(relationGrid);
        }

        // create brand-new relation
        Button addRelationButton = new Button("Add relation");
        addRelationButton.setAlignment(Pos.CENTER);
        addRelationButton.setMaxWidth(Double.MAX_VALUE);
        addRelationButton.setStyle("-fx-background-radius: 15px");
        relationsVBox.getChildren().add(addRelationButton);
        addRelationButton.setOnAction(ev -> {
            // create a constraint so that user can only add max one new relations
            // if the text field is still editable (meaning no relation was created)
            // no new row will be created
            if (relationsVBox.getChildren().size() > 1) {
                GridPane gridPane = (GridPane) relationsVBox.getChildren().get(relationsVBox.getChildren().size() - 2);
                TextField lastTextField = (TextField) getNodeFromGridPane(gridPane, 0, 1);
                if (lastTextField.isEditable())
                    return;
            }
            GridPane relationGrid = new GridPane();
            Label destLabel = new Label("Destination:");
            TextField destField = new TextField();
            Button deleteRelationButton = new Button("Delete");
            Button drawRelationButton = new Button("Draw");

            Label typeLabel = new Label("Select type:");
            ObservableList<String> relationTypes =
                    FXCollections.observableArrayList(EnumSet.allOf(RelationType.class).stream()
                            .map(RelationType::name).collect(Collectors.toList())
                    );
            ComboBox<String> typesCB = new ComboBox<>(relationTypes);
            typesCB.setOnAction(e -> {
                // TODO:
                var gRelation = baseElement.getOwner().getRelation(
                        baseElement.getModel(),
                        baseElement.getOwner().getModel().getClass(destField.getText()));
                if (gRelation != null) {
                    baseElement.getModel().getRelation(
                            baseElement.getOwner().getModel().getClass(destField.getText())
                    ).setRelationType(RelationType.valueOf(typesCB.getValue()));
                    gRelation.updateColor();
                }
            });

            relationGrid.add(destLabel, 0, 0);
            relationGrid.add(typeLabel, 1, 0);
            relationGrid.add(destField, 0, 1);
            relationGrid.add(typesCB, 1, 1);
            relationGrid.add(drawRelationButton, 2, 1);
            relationGrid.add(deleteRelationButton, 3, 1);

            drawRelationButton.setOnAction(e -> {
                var destinationClass = baseElement.getOwner().getModel().getClass(destField.getText());
                if (destinationClass == null) {
                    // TODO: alert error
                    return;
                }
                if (!baseElement.getModel().addRelation(destinationClass, RelationType.valueOf(typesCB.getValue()) )) {
                    // TODO: alert existing relation
                    return;
                }
                var relation = baseElement.getModel().getRelation(destinationClass);
                // var src = (UMLClass) .getSrc();
                baseElement.getOwner().addRelation(
                        new GRelation(baseElement, baseElement.getOwner().getClassElement(destinationClass), baseElement.getOwner().getCanvas(), relation));
                destField.setEditable(false);
                destField.setDisable(true);
            });
            deleteRelationButton.setOnAction(e -> {
                if (destField.getText().isBlank() || destField.isEditable())
                    return;
                var relationClass = baseElement.getOwner().getModel().getClass(destField.getText());
                if (relationClass == null)
                    return;
                baseElement.getModel().removeRelationWithClass(relationClass);
                baseElement.getOwner().removeRelation(baseElement.getOwner().getRelation(baseElement.getModel(), relationClass));
                relationsVBox.getChildren().remove(relationGrid);
                // TODO: remove gRelation from the canvas
            });

//            relationGrid.getChildren().addAll(destField, deleteRelationButton);
            relationGrid.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(destField, Priority.ALWAYS);
            relationsVBox.getChildren().add(relationsVBox.getChildren().size()-1, relationGrid);
        });

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
                    // update sequence diagrams
                    baseElement.getOwner().notify("class");
                }

                @Override
                public void redo() {
                    execute();
                }

                @Override
                public void execute() {
                    classDiagram.changeClassifierName(oldName, newName);
                    // update sequence diagrams
                    baseElement.getOwner().notify("class");
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
