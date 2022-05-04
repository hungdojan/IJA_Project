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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GSequenceDiagram {

//    private final Pane basePane;
    private final HBox baseHBox;
    private AnchorPane rightMenu;
    private final List<GObject> gObjectList = new ArrayList<>();
    private final List<GMessage> gMessageList = new ArrayList<>();
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final GClassDiagram owner;
    private GObject selectedObject;
    private Pane canvas = null;
    private GObject object = null;
    private GMessage msgLine = null;
//    private HBox messageBox = null;
//    private Button deleteMsgButton;

    private SequenceDiagram model;
    private int countObj = 0;
    private int countMsg = 0;

    /**
     * Class {@code GSequenceDiagram} constructor.
     * @param rootTab Pane to put the diagram on
     * @param model Base model
     * @param owner Parent GClassDiagram
     */
    public GSequenceDiagram(TabPane rootTab, SequenceDiagram model, GClassDiagram owner) {

        this.model = Objects.requireNonNull(model);
        this.owner = Objects.requireNonNull(owner);

        Tab baseTab = new Tab(model.getName());

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
        VBox menuVBox = new VBox();
        menuVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        menuVBox.setFillWidth(true);

        // set name
        Label nameLabel = new Label("Select name:");
        nameLabel.setStyle("-fx-font-weight: bold");
        TextField nameTF = new TextField();

        // set type
        Label typeObjLabel = new Label("Select type:");
        typeObjLabel.setStyle("-fx-font-weight: bold");
        var list = owner.getModel().getClasses().stream().map(UMLClass::getName).collect(Collectors.toList());
        ObservableList<String> options =
                FXCollections.observableArrayList(list);
        ComboBox typeCB = new ComboBox(options);
        typeCB.setMaxWidth(Double.MAX_VALUE);

        // add object button
        Button addObject = new Button("Add object");
        addObject.setMaxWidth(Double.MAX_VALUE);
        addObject.setOnAction(e -> {
            // TODO: alert is empty
            var objectInstance = Templates.createObject();
            object = new GObject(canvas, objectInstance, countObj, this);
            model.addObject(objectInstance);
            gObjectList.add(object);
            countObj++;
        });

        Label msgLabel = new Label("Messages:");
        msgLabel.setStyle("-fx-font-weight: bold");

        // create button for adding messages
        Button addMessage = new Button("Add message");
        addMessage.setMaxWidth(Double.MAX_VALUE);
        addMessage.setOnAction(e -> {
            // create grid to store messages in right menu
            var messageGrid = new GridPane();
            Label srcLabel = new Label("Source:");
            Label destLabel = new Label("Destination:");
            Label typeMsgLabel = new Label("Type:");

            // get list of existing objects
            var lofObjects = this.model.getObjects().stream().map(UMLObject::getName).collect(Collectors.toList());
            ObservableList<String> objOptions = FXCollections.observableArrayList(lofObjects);
//            TextField msgTF = new TextField();
            ObservableList<String> msgTypes =
                    FXCollections.observableArrayList(
                            "Sync",
                            "Async",
                            "Return",
                            "Create",
                            "Free"
                    );

            // create combo boxes for options
            ComboBox srcObjCB = new ComboBox(objOptions);
            ComboBox destObjCB = new ComboBox(objOptions);
            ComboBox msgTypeCB = new ComboBox(msgTypes);
            msgTypeCB.setMinWidth(80);
//            messageBox.getChildren().addAll(srcObj, destObj, msgCB, deleteMsgButton);

            // create text field to write the text of the message
            TextField msgTF = new TextField();

            // create delete button
            Button deleteMsgButton = new Button("DELETE");
            deleteMsgButton.setMinWidth(60);
            deleteMsgButton.setOnAction(ev -> {
                menuVBox.getChildren().remove(messageGrid);
            });

            // put parts into grid
            messageGrid.add(srcLabel, 0, 0);
            messageGrid.add(destLabel, 1, 0);
            messageGrid.add(typeMsgLabel, 2, 0);
            messageGrid.add(srcObjCB, 0, 1);
            messageGrid.add(destObjCB, 1, 1);
            messageGrid.add(msgTypeCB, 2, 1);
            messageGrid.add(msgTF, 0, 2, 2, 1);
            messageGrid.add(deleteMsgButton, 2, 2);

            for (Node child : messageGrid.getChildren()) {
                HBox.setHgrow(child, Priority.ALWAYS);
            }

            menuVBox.getChildren().add(menuVBox.getChildren().size()-3, messageGrid);
            srcObjCB.setOnAction(ev -> {
                // put the message line on canvas
                if (gObjectList.size() >= 2) {
                    msgLine = new GMessage(canvas, gObjectList.get(0), gObjectList.get(1), countMsg, (GObject) srcObjCB.getValue(), msgTF.getText());
                    countMsg++;
                }
            });
        });
        
        // fill vbox to anchor pane and add border
        AnchorPane.setBottomAnchor(menuVBox, 0.0);
        AnchorPane.setTopAnchor(menuVBox, 0.0);
        AnchorPane.setLeftAnchor(menuVBox, 0.0);
        AnchorPane.setRightAnchor(menuVBox, 0.0);
        menuVBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: hidden hidden hidden solid");
        menuVBox.setPadding(new Insets(5, 10, 5, 10));

        // TODO: FIX DRAGGING
//        if (messageBox != null) {
//            messageBox.setOnDragDetected(e -> {
//                Dragboard db = messageBox.startDragAndDrop(TransferMode.ANY);
//                ClipboardContent content = new ClipboardContent();
//                content.putString("we've done it boys");
//                db.setContent(content);
//            });
//        };

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
        if (selectedObject == null) {
            nameField.textProperty().unbind();
            nameField.setText("");
            return;
        }
        selectedObject.selected(true);
        // TODO: load data to right menu
        nameField.textProperty().bind(selectedObject.getModel().getNameProperty());
    }
}
