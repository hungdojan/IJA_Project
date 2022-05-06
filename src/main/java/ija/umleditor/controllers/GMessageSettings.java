package ija.umleditor.controllers;

import ija.umleditor.models.UMLAttribute;
import ija.umleditor.models.UMLClass;
import ija.umleditor.models.UMLMessage;
import ija.umleditor.models.UMLOperation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.stream.Collectors;

import static ija.umleditor.controllers.GSequenceDiagram.messageType;

public class GMessageSettings {
    private final GSequenceDiagram owner;
    private GMessage gModel = null;
    private final ComboBox<String> operationCB;
    private final ComboBox<String> srcObjCB;
    private final ComboBox<String> destObjCB;
    private final ComboBox<String> msgTypeCB;
    private final TextField msgTF;

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
        srcObjCB.setValue(model.getSender().getName());
        destObjCB.setValue(model.getReceiver().getName());
        msgTF.setText(model.getName());
        operationCB.setValue(model.getMessage().getName());
        operationCB.setDisable(false);
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
        srcObjCB = new ComboBox<>(observableObjects);
        destObjCB = new ComboBox<>(observableObjects);
        msgTypeCB = new ComboBox<>(messageType);
        operationCB = new ComboBox<>();
        operationCB.setDisable(true);
        msgTypeCB.setMinWidth(80);
//            messageBox.getChildren().addAll(srcObj, destObj, msgCB, deleteMsgButton);

        // create text field to write the text of the message
        msgTF = new TextField();

        // create delete button
        Button deleteMsgButton = new Button("DELETE");
        deleteMsgButton.setMinWidth(60);
        deleteMsgButton.setOnAction(ev -> {
            // TODO: remove and move
            if (owner.getNewMessage() == this) {
                owner.setNewMessage(null);
            }
            menuVBox.getChildren().remove(messageGrid);
            owner.removeGMessage(gModel);
        });

        // separator between messages
        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.setPadding(new Insets(5, 0, 5, 0));
        HBox.setHgrow(sep, Priority.ALWAYS);
        menuVBox.getChildren().add(sep);

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
        messageGrid.add(sep, 0, 4, 3, 1);

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
            operationCB.setDisable(dstObj == null);
            if (dstObj != null) {
                var obsOperations = FXCollections.observableArrayList(
                        dstObj.getModel().getOperations().stream().map(UMLOperation::getName).collect(Collectors.toList())
                );
                operationCB.setItems(obsOperations);
            }
        });
        operationCB.setOnAction(ev -> {
            // TODO:
        });
        msgTypeCB.setOnAction(ev -> {
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
                UMLAttribute operation = dstObj.getModel().getClassOfInstance().getAttribute(operationCB.getValue());
                var messageInstance = new UMLMessage(msgTF.getText(), srcObj.getModel(), dstObj.getModel(), (UMLOperation) operation);
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
    public void update(String msg) {
        if (Objects.equals(msg, "operation")) {
            var receiver = gModel.getModel().getReceiver();
            if (receiver == null)
                return;
            var operList = operationCB.getItems();
            operList.clear();
            operList.addAll(
                    receiver.getClassOfInstance().getOperations().stream().map(UMLOperation::getName).collect(Collectors.toList())
            );
        }
    }
}
