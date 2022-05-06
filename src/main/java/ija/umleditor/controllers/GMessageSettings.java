/**
 * @brief Creates grid for every message.
 * It is possible to set source object, destination object, message type, either new text or operation (new text is privileged)
 * and button to draw or delete the message.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GMessageSettings.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Graphical representation of box for editing messages.
 */
public class GMessageSettings {
    private final GSequenceDiagram owner;
    private GMessage gModel = null;
    private final ComboBox<String> operationCB;
    private final ComboBox<String> srcObjCB;
    private final ComboBox<String> destObjCB;
    private final ComboBox<String> msgTypeCB;
    private final TextField msgTF;

    /**
     * Loads UMLMessage model.
     * @param model Instance of UMLMessage.
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
        msgTypeCB.setValue(model.getMessageType().toString());
        owner.addGMessage(gModel);
    }

    /**
     * Class {@code GMessageSettings} constructor.
     * @param observableObjects List of object that are currently on canvas.
     * @param menuVBox Box to store the messages into.
     * @param owner Instance of GSequenceDiagram.
     */
    public GMessageSettings(ObservableList<String> observableObjects, VBox menuVBox, GSequenceDiagram owner) {
        this.owner = Objects.requireNonNull(owner);
        // create grid to store messages in right menu
        var messageGrid = new GridPane();
        Label srcLabel = new Label("Source:");
        Label destLabel = new Label("Destination:");
        Label typeMsgLabel = new Label("Type:");
        Label ownOperationLabel = new Label("Set text:");
        Label pickOperationLabel = new Label("Operation:");

        // create combo boxes for options
        srcObjCB = new ComboBox<>(observableObjects);
        destObjCB = new ComboBox<>(observableObjects);
        msgTypeCB = new ComboBox<>(
                FXCollections.observableArrayList(EnumSet.allOf(MessageType.class).stream()
                                .map(MessageType::name).collect(Collectors.toList())
                )
        );
        operationCB = new ComboBox<>();
        operationCB.setDisable(true);
        msgTypeCB.setMinWidth(80);

        // create text field to write the text of the message
        msgTF = new TextField();

        // create delete button
        Button deleteMsgButton = new Button("Delete");
        deleteMsgButton.setStyle("-fx-background-radius: 15px");
        deleteMsgButton.setMinWidth(60);
        GMessageSettings temp = this;
        deleteMsgButton.setOnAction(ev -> owner.getCommandBuilder().execute(new ICommand() {
            int index;
            final GridPane tempGrid = messageGrid;
            final GMessage tempModel = gModel;
            @Override
            public void undo() {
                menuVBox.getChildren().add(index + 5, tempGrid);
                owner.addGMessage(tempModel, index);
            }

            @Override
            public void redo() {
                menuVBox.getChildren().remove(tempGrid);
                owner.removeGMessage(tempModel);
            }

            @Override
            public void execute() {
                if (owner.getNewMessage() == temp) {
                    owner.setNewMessage(null);
                }
                menuVBox.getChildren().remove(tempGrid);
                index = owner.removeGMessage(tempModel);
            }
        }));

        Button drawMsgButton = new Button("Draw");
        drawMsgButton.setStyle("-fx-background-radius: 15px");

        // separator between messages
        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.setPadding(new Insets(5, 0, 5, 0));
        HBox.setHgrow(sep, Priority.ALWAYS);
        menuVBox.getChildren().add(sep);

        // fill the grid
        srcObjCB.setMaxWidth(Double.MAX_VALUE);
        destObjCB.setMaxWidth(Double.MAX_VALUE);
        msgTypeCB.setMaxWidth(Double.MAX_VALUE);
        msgTF.setMaxWidth(Double.MAX_VALUE);
        operationCB.setMaxWidth(Double.MAX_VALUE);
        drawMsgButton.setMaxWidth(Double.MAX_VALUE);
        deleteMsgButton.setMaxWidth(Double.MAX_VALUE);

        // put parts into grid
        messageGrid.add(srcLabel, 0, 0);
        messageGrid.add(destLabel, 1, 0);
        messageGrid.add(typeMsgLabel, 2, 0);
        messageGrid.add(srcObjCB, 0, 1);
        messageGrid.add(destObjCB, 1, 1);
        messageGrid.add(msgTypeCB, 2, 1, 2, 1);
        messageGrid.add(ownOperationLabel, 0, 2);
        messageGrid.add(pickOperationLabel, 1, 2);
        messageGrid.add(msgTF, 0, 3);
        messageGrid.add(operationCB, 1, 3);
        messageGrid.add(drawMsgButton, 2, 3);
        messageGrid.add(deleteMsgButton, 3, 3);
        messageGrid.add(sep, 0, 4, 4, 1);

        menuVBox.getChildren().add(menuVBox.getChildren().size()-3, messageGrid);

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
            if (gModel != null) {
                owner.getCommandBuilder().execute(new ICommand() {
                    final UMLOperation oldOperation = gModel.getModel().getMessage();
                    final UMLOperation newOperation = (UMLOperation) gModel.getDstGObject().getModel().getClassOfInstance().getAttribute(operationCB.getValue());
                    @Override
                    public void undo() {
                        gModel.getModel().setMessage(oldOperation);
                    }

                    @Override
                    public void redo() {
                        execute();
                    }

                    @Override
                    public void execute() {
                        gModel.getModel().setMessage(newOperation);
                    }
                });
            }
        });
        msgTypeCB.setOnAction(ev -> {
            if (gModel != null) {
                gModel.getModel().setMessageType(MessageType.valueOf(msgTypeCB.getValue()));
                gModel.update("line");
            }
        });
        drawMsgButton.setOnAction(ev -> {
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
                if (!msgTypeCB.getValue().isBlank())
                    messageInstance.setMessageType(MessageType.valueOf(msgTypeCB.getValue()));
                gModel = new GMessage(owner.getCanvas(), srcObj, dstObj, owner.countMsg++, messageInstance);
                owner.addGMessage(gModel);
                owner.getModel().addMessage(messageInstance);
                owner.setNewMessage(null);
                destObjCB.setDisable(true);
                srcObjCB.setDisable(true);
            } else {
                gModel.getModel().setName(msgTF.getText());
                gModel.updateText();
            }
        });
    }
    public void update(String msg) {
        if (Objects.equals(msg, "operation")) {
            if (gModel == null)
                return;
            var receiver = gModel.getModel().getReceiver();
            if (receiver == null)
                return;
            var operList = operationCB.getItems();
            operList.clear();
            operList.addAll(
                    receiver.getClassOfInstance().getOperations().stream().map(UMLOperation::getName).collect(Collectors.toList())
            );
            operationCB.setItems(operList);
            operationCB.setValue(gModel.getModel().getMessage().getName());
        } else if (Objects.equals(msg, "loadMessages")) {
            if (gModel == null)
                return;
            srcObjCB.setValue(gModel.getModel().getSender().getName());
            destObjCB.setValue(gModel.getModel().getReceiver().getName());
            if (gModel.getModel().getMessage() != null) {
                operationCB.setValue(gModel.getModel().getMessage().getName());
            }
        }
    }
}
