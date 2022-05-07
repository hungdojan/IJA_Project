/**
 * @brief Creates sequence diagram.
 * HBox layout contains canvas to draw the diagram on and right menu to edit objects.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GSequenceDiagram.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.SequenceDiagram;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLObject;
import ija.umleditor.template.Templates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Graphical representation of sequence diagram.
 */
public class GSequenceDiagram {

    private final AnchorPane rightMenu;
    private final List<GObject> gObjectList = new ArrayList<>();
    private final List<GMessage> gMessageList = new ArrayList<>();
    private final List<GMessageSettings> gMessageSettingsList = new ArrayList<>();
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final ObservableList<String> observableClassNames = FXCollections.observableArrayList();
    private final ObservableList<String> observableObjects = FXCollections.observableArrayList();
    private final GClassDiagram owner;
    private GObject selectedObject;
    private Pane canvas = null;
    private GObject object = null;
    private GMessageSettings newMessage = null;
    private final Tab baseTab;

    private final SequenceDiagram model;
    private int countObj = 0;
    public int countMsg = 0;

    /**
     * Updates direction of arrows.
     */
    public void updateArrow() {
        for (var gm : gMessageList) {
            gm.updateArrowHead();
        }
    }

    /**
     * Gets command builder.
     * @return Instance of CommandBuilder
     */
    public CommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    /**
     * Adds message to the list of all messages.
     * @param gMessage Message to be added
     */
    public void addGMessage(GMessage gMessage) {
        if (gMessage == null)
            return;
        gMessageList.add(gMessage);
    }

    /**
     * Adds instance of GMessage into the canvas.
     * @param gMessage Message to be added
     * @param position Position to put the message at
     */
    public void addGMessage(GMessage gMessage, int position) {
        if (gMessage == null)
            return;
        // shift of messages present on canvas
        for (int i = position; i < gMessageList.size(); i++) {
            gMessageList.get(i).moveContent(1);
        }
        countMsg++;
        gMessageList.add(position, gMessage);
        model.getMessages().add(position, gMessage.getModel());
        gMessage.addBackToCanvas(canvas);
    }

    /**
     * Removes instance of GMessage from the canvas.
     * @param gMessage Message to be removed
     * @return Position of removed message
     */
    public int removeGMessage(GMessage gMessage) {
        if (gMessage == null)
            return -1;
        int position = gMessageList.indexOf(gMessage);
        if (position < 0)
            return -1;
        int pos = position;
        gMessage.removeFromCanvas(canvas);
        gMessageList.remove(gMessage);
        // shift of messages present on canvas
        for (; position < gMessageList.size(); position++)
            gMessageList.get(position).moveContent(-1);
        countMsg--;
        model.removeMessage(position);
        return pos;
    }

    /**
     * Gets model.
     * @return Instance of SequenceDiagram
     */
    public SequenceDiagram getModel() {
        return model;
    }

    /**
     * Gets list of all objects that are currently on canvas.
     * @return List of objects.
     */
    public List<GObject> getgObjectList() {
        return gObjectList;
    }

    /**
     * Sets new message grid.
     * @param newMessage Instance of GMessageSettings to be set.
     */
    public void setNewMessage(GMessageSettings newMessage) {
        this.newMessage = newMessage;
    }

    /**
     * Gets new message.
     * @return Instance of GMessageSettings
     */
    public GMessageSettings getNewMessage() {
        return newMessage;
    }

    /**
     * Gets canvas.
     * @return Instance of Pane.
     */
    public Pane getCanvas() {
        return canvas;
    }

    /**
     * Class {@code GSequenceDiagram} constructor.
     * @param rootTab Pane to put the diagram on
     * @param model Base model
     * @param owner Parent GClassDiagram
     */
    public GSequenceDiagram(TabPane rootTab, SequenceDiagram model, GClassDiagram owner) {

        this.model = Objects.requireNonNull(model);
        this.owner = Objects.requireNonNull(owner);

        baseTab = new Tab(model.getName());

        // setup key shortcuts and actions in current tab
        baseTab.setOnSelectionChanged(e -> {
            if (baseTab.isSelected()) {
                // delete selected object
                rootTab.setOnKeyPressed(ev -> {
                    if (ev.getCode() == KeyCode.DELETE && selectedObject != null) {
                        commandBuilder.execute(new ICommand() {
                            final GObject object = selectedObject;
                            final UMLClass classOfInstance = object.getModel().getClassOfInstance();
                            final List<GMessage> listOfMessages = new ArrayList<>();
                            @Override
                            public void undo() {
                                object.getModel().setClassOfInstance(classOfInstance);
                                for (var m : listOfMessages) {
                                    m.updateColor(false);
                                }
                                model.addObject(object.getModel());
                                gObjectList.add(object);
                                canvas.getChildren().add(object.getBaseGroup());
                                update("object");
                            }

                            @Override
                            public void redo() {
                                object.getModel().close();
                                for (var m : listOfMessages) {
                                    m.updateColor(true);
                                }
                                model.removeObject(object.getModel());
                                gObjectList.remove(object);
                                canvas.getChildren().remove(object.getBaseGroup());
                                update("object");
                            }

                            @Override
                            public void execute() {
                                object.getModel().close();
                                for (var m : gMessageList.stream().filter(x -> x.getSrcGObject() == object ||
                                        x.getDstGObject() == object).collect(Collectors.toList())) {
                                    m.updateColor(true);
                                    listOfMessages.add(m);
                                }
                                model.removeObject(object.getModel());
                                gObjectList.remove(object);
                                canvas.getChildren().remove(object.getBaseGroup());
                                update("object");
                            }
                        });
                        setSelectedObject(null);
                    } else if (ev.isControlDown() && ev.getCode() == KeyCode.Z) {
                        commandBuilder.undo();
                    } else if (ev.isControlDown() && ev.getCode() == KeyCode.Y) {
                        commandBuilder.redo();
                    }
                });
            }
        });

        // content pane
        HBox baseHBox = new HBox();

        // create right menu
        rightMenu = new AnchorPane();
        rightMenu.setMinWidth(200);
        VBox menuVBox = createRightMenu(rootTab, owner, baseTab);

        ScrollPane drawable = createCanvas();

        // add created elements to base
        rightMenu.getChildren().add(menuVBox);
        baseHBox.getChildren().addAll(drawable, rightMenu);
        baseTab.setContent(baseHBox);
        rootTab.getTabs().add(baseTab);
        for (var obj : model.getObjects()) {
            var object = new GObject(canvas, obj, countObj, this);
            gObjectList.add(object);
            observableObjects.add(obj.getName());
            countObj++;
        }

        for (var msg : model.getMessages()) {
            var gMessageSettings = new GMessageSettings(observableObjects, menuVBox, this);
            gMessageSettings.loadModel(msg);
            gMessageSettingsList.add(gMessageSettings);
        }
    }

    /**
     * Creates pane to put diagram on.
     * @return Instance of ScrollPane
     */
    private ScrollPane createCanvas() {
        // create canvas
        ScrollPane drawable = new ScrollPane();
        drawable.setOnMouseClicked(ev -> setSelectedObject(null));
        drawable.setFitToWidth(true);
        drawable.setFitToHeight(true);

        canvas = new AnchorPane();
        drawable.setContent(canvas);
        HBox.setHgrow(drawable, Priority.ALWAYS);
        return drawable;
    }

    /**
     * Creates right menu witch name and type of object and messages.
     * @param rootTab Base TabPane
     * @param owner Parent of GClassDiagram
     * @param baseTab Base pane
     * @return Created right menu as VBox.
     */
    private VBox createRightMenu(TabPane rootTab, GClassDiagram owner, Tab baseTab) {
        update("class");
        VBox menuVBox = new VBox();
        menuVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        menuVBox.setFillWidth(true);

        // set name
        Label nameLabel = new Label("Select name:");
        nameLabel.setStyle("-fx-font-weight: bold");
        TextField nameTF = new TextField();
        nameTF.setDisable(true);
        nameTF.setOnAction(ev -> {
            // ignore no selected object or no name edit
            if (selectedObject == null || nameTF.getText().isBlank() ||
                    Objects.equals(nameTF.getText(), selectedObject.getModel().getName()))
                return;
            // return collision of name
            var obj = model.getObject(nameTF.getText());
            if (obj != null)
                return;
            selectedObject.getModel().setName(nameTF.getText());
            update("object");
        });

        // set type
        Label typeObjLabel = new Label("Select type:");
        typeObjLabel.setStyle("-fx-font-weight: bold");
        ComboBox<String> typeCB = new ComboBox<>(observableClassNames);
        typeCB.setDisable(true);
        typeCB.setMaxWidth(Double.MAX_VALUE);
        typeCB.setOnAction(ev -> {
            if (selectedObject != null) {
                var objType = owner.getModel().getClass(typeCB.getValue());
                selectedObject.getModel().setClassOfInstance(objType);
                for (var i : gMessageSettingsList) {
                    i.update("operation");
                }
            }
        });

        // add object button
        Button addObject = new Button("Add object");
        addObject.setMaxWidth(Double.MAX_VALUE);
        addObject.setStyle("-fx-background-radius: 15px");
        addObject.setOnAction(e -> {
            var objectInstance = Templates.createObject();
            object = new GObject(canvas, objectInstance, countObj, this);
            model.addObject(objectInstance);
            gObjectList.add(object);
            observableObjects.add(objectInstance.getName());
            countObj++;
        });

        Label msgLabel = new Label("Messages:");
        msgLabel.setStyle("-fx-font-weight: bold");

        // create button for adding messages
        Button addMessage = new Button("Add message");
        addMessage.setMaxWidth(Double.MAX_VALUE);
        addMessage.setStyle("-fx-background-radius: 15px");
        addMessage.setOnAction(e -> {
            // add new object
            if (newMessage == null) {
                var gMessageSettings = new GMessageSettings(observableObjects, menuVBox, this);
                gMessageSettingsList.add(gMessageSettings);
                newMessage = gMessageSettings;
            }
        });
        
        // fill vbox to anchor pane and add border
        AnchorPane.setBottomAnchor(menuVBox, 0.0);
        AnchorPane.setTopAnchor(menuVBox, 0.0);
        AnchorPane.setLeftAnchor(menuVBox, 0.0);
        AnchorPane.setRightAnchor(menuVBox, 0.0);
        menuVBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: hidden hidden hidden solid");
        menuVBox.setPadding(new Insets(5, 10, 5, 10));

        // create separator button x objects
        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.setHalignment(HPos.CENTER);

        // button to remove class diagram instance
        Button deleteDiagram = new Button("Delete diagram");
        deleteDiagram.setMaxWidth(Double.MAX_VALUE);
        deleteDiagram.setStyle("-fx-background-radius: 15px");
        deleteDiagram.setOnAction(ev -> rootTab.getTabs().remove(baseTab));

        // set margin between items in vbox
        VBox.setMargin(nameTF, new Insets(0, 0, 5, 0));
        VBox.setMargin(typeCB, new Insets(0, 0, 5, 0));
        VBox.setMargin(addObject, new Insets(0, 0, 5, 0));
        VBox.setMargin(addMessage, new Insets(0, 0, 5, 0));
        VBox.setMargin(sep, new Insets(0, 0, 5, 0));

        menuVBox.getChildren().addAll(nameLabel, nameTF, typeObjLabel, typeCB, addObject, msgLabel, addMessage, sep, deleteDiagram);
        return menuVBox;
    }

    /**
     * Set an object as selected.
     * @param object Instance of GObject.
     */
    public void setSelectedObject(GObject object) {
        if (selectedObject != null) {
            selectedObject.selected(false);
        }
        selectedObject = object;
        TextField nameField = (TextField) ((VBox) rightMenu.getChildren().get(0)).getChildren().get(1);
        ComboBox<String> classCB = (ComboBox<String>) ((VBox) rightMenu.getChildren().get(0)).getChildren().get(3);
        if (selectedObject == null) {
            nameField.setText("");
            classCB.setValue("");
            classCB.setDisable(true);
            nameField.setDisable(true);
            return;
        }
        selectedObject.selected(true);
        nameField.setText(selectedObject.getModel().getName());
        classCB.setValue(selectedObject.getModel().getClassOfInstance().getName());
        classCB.setDisable(false);
        nameField.setDisable(false);
    }

    /**
     * Updates observable collections.
     * @param msg Message for observer.
     */
    public void update(String msg) {
        if (Objects.equals(msg, "class")) {
            var clsNames = owner.getModel().getClasses().stream().map(UMLClass::getName).collect(Collectors.toList());
            observableClassNames.clear();
            observableClassNames.addAll(clsNames);
        } else if (Objects.equals(msg, "operation")) {
            for (var gMS : gMessageSettingsList) {
                gMS.update("operation");
            }
        } else if (Objects.equals(msg, "object")) {
            var objNames = model.getObjects().stream().map(UMLObject::getName).collect(Collectors.toList());
            observableObjects.clear();
            observableObjects.addAll(objNames);
            for (var gms : gMessageSettingsList) {
                gms.update("loadMessages");
            }
        }
    }
}
