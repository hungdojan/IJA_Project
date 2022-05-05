package ija.umleditor.controllers;

import ija.umleditor.models.UMLMessage;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;

import static ija.umleditor.controllers.GSequenceDiagram.messageType;

public class GMessageSettings {
    private final GSequenceDiagram owner;
    private GMessage gModel = null;

    public GMessage getgModel() {
        return gModel;
    }

    public void setgModel(GMessage gModel) {
        this.gModel = gModel;
    }

    /**
     * Loads UMLMessage model.
     * @param model
     */
    public void loadModel(UMLMessage model) {
        // TODO:
        var srcObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), model.getSender().getName()))
                .findFirst().orElse(null);
        var dstObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), model.getReceiver().getName()))
                .findFirst().orElse(null);
        if (srcObj == null || dstObj == null)
            return;
        gModel = new GMessage(owner.getCanvas(), srcObj, dstObj, owner.countMsg++, model);
        owner.addGMessage(gModel);
    }

    public GMessageSettings(ObservableList<String> observableObjects, VBox menuVBox, GSequenceDiagram owner) {
        this.owner = Objects.requireNonNull(owner);
        // create grid to store messages in right menu
        var messageGrid = new GridPane();
        Label srcLabel = new Label("Source:");
        Label destLabel = new Label("Destination:");
        Label typeMsgLabel = new Label("Type:");
        Label ownOperationLabel = new Label("Set text:");
        Label pickOperationLabel = new Label("Operation:");

        // get list of existing objects'
        // var lofObjects = this.model.getObjects().stream().map(UMLObject::getName).collect(Collectors.toList());
        // ObservableList<String> objOptions = FXCollections.observableArrayList(lofObjects);

        // create combo boxes for options
        ComboBox<String> srcObjCB = new ComboBox<>(observableObjects);
        ComboBox<String> destObjCB = new ComboBox<>(observableObjects);
        ComboBox<String> msgTypeCB = new ComboBox<>(messageType);
        ComboBox<String> operationCB = new ComboBox<>(owner.getObservableOperations());
        msgTypeCB.setMinWidth(80);
//            messageBox.getChildren().addAll(srcObj, destObj, msgCB, deleteMsgButton);

        // create text field to write the text of the message
        TextField msgTF = new TextField();

        // create delete button
        Button deleteMsgButton = new Button("DELETE");
        deleteMsgButton.setMinWidth(60);
        deleteMsgButton.setOnAction(ev -> {
            // TODO: remove and move
            if (owner.getNewMessage() == this) {
                owner.setNewMessage(null);
            }
            menuVBox.getChildren().remove(messageGrid);
        });

        // put parts into grid
        messageGrid.add(srcLabel, 0, 0);
        messageGrid.add(destLabel, 1, 0);
        messageGrid.add(typeMsgLabel, 2, 0);
        messageGrid.add(srcObjCB, 0, 1);
        messageGrid.add(destObjCB, 1, 1);
        messageGrid.add(msgTypeCB, 2, 1);
        messageGrid.add(ownOperationLabel, 0, 2);
        messageGrid.add(pickOperationLabel, 1, 2);
        messageGrid.add(msgTF, 0, 3);
        messageGrid.add(operationCB, 1, 3);
        messageGrid.add(deleteMsgButton, 2, 3);

        for (Node child : messageGrid.getChildren()) {
            HBox.setHgrow(child, Priority.ALWAYS);
            child.maxWidth(Double.MAX_VALUE);
        }

        menuVBox.getChildren().add(menuVBox.getChildren().size()-3, messageGrid);
        srcObjCB.setOnAction(ev -> {
            var srcObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), srcObjCB.getValue()))
                    .findFirst().orElse(null);
        });
        destObjCB.setOnAction(ev -> {
            var dstObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), destObjCB.getValue()))
                    .findFirst().orElse(null);
        });
        msgTypeCB.setOnAction(ev -> {
//            if ()
            // TODO:
        });
        msgTF.setOnAction(ev -> {
            // TODO:
            // put the message line on canvas
            if (gModel == null) {
                var srcObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), srcObjCB.getValue()))
                        .findFirst().orElse(null);
                var dstObj = owner.getgObjectList().stream().filter(x -> Objects.equals(x.getModel().getName(), destObjCB.getValue()))
                        .findFirst().orElse(null);
                if (srcObj == null || dstObj == null)
                    return;
                var messageInstance = new UMLMessage(msgTF.getText(), srcObj.getModel(), dstObj.getModel(), null);
                gModel = new GMessage(owner.getCanvas(), srcObj, dstObj, owner.countMsg++, messageInstance);
                owner.addGMessage(gModel);
                owner.getModel().addMessage(messageInstance);
                owner.setNewMessage(null);
            } else {
                gModel.getModel().setName(msgTF.getText());
                gModel.updateText();
            }
        });
    }
}
