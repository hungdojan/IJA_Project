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

public class GSequenceDiagram {

//    private final Pane basePane;
    private final HBox baseHBox;
    private final AnchorPane rightMenu;
    private final List<GObject> gObjectList = new ArrayList<>();
    private final List<GMessage> gMessageList = new ArrayList<>();
    private final List<GMessageSettings> gMessageSettingsList = new ArrayList<>();
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final ObservableList<String> observableClassNames = FXCollections.observableArrayList();
    public final static ObservableList<String> messageType =
            FXCollections.observableArrayList("Sync", "Async", "Return", "Create", "Free");
    private final ObservableList<String> observableObjects = FXCollections.observableArrayList();
    private final GClassDiagram owner;
    private GObject selectedObject;
    private Pane canvas = null;
    private GObject object = null;
    private GMessage msgLine = null;
    private GMessageSettings newMessage = null;
    private final Tab baseTab;

    private final SequenceDiagram model;
    private int countObj = 0;
    public int countMsg = 0;

    public void updateArrow() {
        for (var gm : gMessageList) {
            gm.updateArrowHead();
        }
    }

    public Tab getBaseTab() {
        return baseTab;
    }

    public void addGMessage(GMessage gMessage) {
        if (gMessage == null)
            return;
        gMessageList.add(gMessage);
    }

    public void addGMessage(GMessage gMessage, int position) {
        if (gMessage == null)
            return;
        for (int i = position; i < gMessageList.size(); i++) {
            gMessageList.get(i).moveContent(1);
        }
        gMessage.moveContent(gMessageList.size() - 1 - position);
        gMessageList.add(gMessage);
    }

    public void removeGMessage(GMessage gMessage) {
        if (gMessage == null)
            return;
        int position = gMessageList.indexOf(gMessage);
        if (position < 0)
            return;
        gMessage.removeFromCanvas(canvas);
        gMessageList.remove(gMessage);
        for (; position < gMessageList.size(); position++)
            gMessageList.get(position).moveContent(-1);
        countMsg--;
    }

    public void removeGMessageSettings(GMessageSettings gMessageSettings) {
        gMessageSettingsList.remove(gMessageSettings);
    }

    public SequenceDiagram getModel() {
        return model;
    }

    public List<GObject> getgObjectList() {
        return gObjectList;
    }

    public void setNewMessage(GMessageSettings newMessage) {
        this.newMessage = newMessage;
    }

    public GMessageSettings getNewMessage() {
        return newMessage;
    }

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


        // content pane
        baseHBox = new HBox();

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
            // TODO: load messages
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
        drawable.setOnMouseClicked(ev -> {
            setSelectedObject(null);
        });
        drawable.setFitToWidth(true);
        drawable.setFitToHeight(true);

        canvas = new AnchorPane();
        drawable.setContent(canvas);
        drawable.setOnKeyPressed(ev -> {
            System.out.println("tu");
            if (ev.getCode() == KeyCode.DELETE && selectedObject != null) {
                selectedObject.getModel().close();
                for (var m : gMessageList.stream().filter(x -> x.getSrcGObject() == selectedObject ||
                        x.getDstGObject() == selectedObject).collect(Collectors.toList())) {
                    // TODO: update gMessages (color??)
                }
                canvas.getChildren().remove(selectedObject.getBaseGroup());
            }
        });
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
        typeCB.setMaxWidth(Double.MAX_VALUE);
        typeCB.setOnAction(ev -> {
            if (selectedObject != null) {
                var objType = owner.getModel().getClass(typeCB.getValue());
                selectedObject.getModel().setClassOfInstance(objType);
            }
        });

        // add object button
        Button addObject = new Button("Add object");
        addObject.setMaxWidth(Double.MAX_VALUE);
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

    public void setSelectedObject(GObject object) {
        if (selectedObject != null) {
            selectedObject.selected(false);
        }
        selectedObject = object;
        TextField nameField = (TextField) ((VBox) rightMenu.getChildren().get(0)).getChildren().get(1);
        ComboBox<String> classCB = (ComboBox<String>) ((VBox) rightMenu.getChildren().get(0)).getChildren().get(3);
        if (selectedObject == null) {
            // nameField.textProperty().unbind();
            nameField.setText("");
            classCB.setValue("");
            return;
        }
        selectedObject.selected(true);
        // TODO: load data to right menu
        // nameField.textProperty().bindBidirectional(selectedObject.getModel().getNameProperty());
        nameField.setText(selectedObject.getModel().getName());
        classCB.setValue(selectedObject.getModel().getClassOfInstance().getName());
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
            // update objects??
        } else if (Objects.equals(msg, "operation")) {
            // TODO: update operations
        } else if (Objects.equals(msg, "object")) {
            var objNames = model.getObjects().stream().map(UMLObject::getName).collect(Collectors.toList());
            observableObjects.clear();
            observableObjects.addAll(objNames);
        }
    }
}
