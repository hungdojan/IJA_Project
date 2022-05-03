package ija.umleditor.controllers;

import ija.umleditor.models.SequenceDiagram;
import ija.umleditor.models.UMLClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GSequenceDiagram {

//    private final Pane basePane;
    private final HBox baseHBox;
    private Pane canvas = null;
    private GObject object = null;
    private GMessage msgLine = null;
    private HBox messageBox = null;

    private SequenceDiagram model;
    private double count = 0;

    public GSequenceDiagram(TabPane rootTab, SequenceDiagram model) throws FileNotFoundException {
        this.model = Objects.requireNonNull(model);
        Tab baseTab = new Tab("Sequence diagram");

        // content pane
        baseHBox = new HBox();

        // create right menu layout
        AnchorPane rightMenu = new AnchorPane();
        rightMenu.setMinWidth(200);
        VBox menuVBox = new VBox();
        menuVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        menuVBox.setFillWidth(true);

        // set name
        Label nameLabel = new Label("Select name:");
        nameLabel.setStyle("-fx-font-weight: bold");
        TextField nameTF = new TextField();

        // set type
        Label typeLabel = new Label("Select type:");
        typeLabel.setStyle("-fx-font-weight: bold");
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Option 1",
                        "Option 2",
                        "Option 3"
                );
        ComboBox typeCB = new ComboBox(options);
        typeCB.setMaxWidth(Double.MAX_VALUE);

        // add object button
        Button addObject = new Button("Add object");
        addObject.setMaxWidth(Double.MAX_VALUE);
        addObject.setOnAction(e -> {
            // TODO: alert is empty
            object = new GObject(canvas, (String) typeCB.getValue(), nameTF.getText(), count);
            count++;
        });

        Label msgLabel = new Label("Messages:");
        msgLabel.setStyle("-fx-font-weight: bold");

        // create button for adding messages
        Button addMessage = new Button("Add message");
        addMessage.setMaxWidth(Double.MAX_VALUE);
        addMessage.setOnAction(e -> {
            // create box to store messages in right menu
            messageBox = new HBox();
            TextField msgTF = new TextField();
            ObservableList<String> msgTypes =
                    FXCollections.observableArrayList(
                            "Sync",
                            "Async",
                            "Return",
                            "Create",
                            "Free"
                    );
            ComboBox msgCB = new ComboBox(msgTypes);
            msgCB.setMinWidth(80);
            messageBox.getChildren().addAll(msgTF, msgCB);
            menuVBox.getChildren().add(menuVBox.getChildren().size()-3, messageBox);
            // put the message line on canvas
            msgLine = new GMessage(canvas);
        });

        // TODO: FIX DRAGGING
//        if (messageBox != null) {
//            messageBox.setOnDragDetected(e -> {
//                Dragboard db = messageBox.startDragAndDrop(TransferMode.ANY);
//                ClipboardContent content = new ClipboardContent();
//                content.putString("we've done it boys");
//                db.setContent(content);
//            });
//        };

        // dialog layout
//        Dialog<Void> dialogWindow = new Dialog<>();
//        GridPane dialogGrid = new GridPane();

        // place into grid
//        dialogGrid.add(nameLabel, 0, 0);
//        dialogGrid.add(nameTF, 1, 0);
//        dialogGrid.add(typeLabel, 0, 1);
//        dialogGrid.add(typeCB, 1, 1);
//
//        dialogWindow.getDialogPane().setContent(dialogGrid);
//        dialogWindow.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        dialogWindow.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
//
//        addObject.setOnAction(e -> {
//            dialogWindow.showAndWait();
//        });
//        dialogWindow.setOnCloseRequest(e -> {
//            System.out.println(nameTF.getText());
//        });

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

        // stretch vbox to whole height
//        VBox.setVgrow(nameLabel, Priority.ALWAYS);
//        VBox.setVgrow(nameTF, Priority.ALWAYS);
//        VBox.setVgrow(typeLabel, Priority.ALWAYS);
//        VBox.setVgrow(typeCB, Priority.ALWAYS);
//        VBox.setVgrow(addObject, Priority.ALWAYS);
//        VBox.setVgrow(msgLabel, Priority.ALWAYS);
//        VBox.setVgrow(addMessage, Priority.ALWAYS);
//        VBox.setVgrow(sep, Priority.ALWAYS);
//        VBox.setVgrow(deleteDiagram, Priority.ALWAYS);

//        for (Node child : menuVBox.getChildren()) {
//            VBox.setMargin(child, new Insets(0, 0, 50, 0));
//        }
//        menuVBox.getChildren().forEach(child -> VBox.setMargin(child, new Insets (0, 0, 50, 0)));

        menuVBox.getChildren().addAll(nameLabel, nameTF, typeLabel, typeCB, addObject, msgLabel, addMessage, sep, deleteDiagram);

        // create canvas
        ScrollPane drawable = new ScrollPane();
        drawable.setFitToWidth(true);
        drawable.setFitToHeight(true);

        canvas = new AnchorPane();
        drawable.setContent(canvas);
        HBox.setHgrow(drawable, Priority.ALWAYS);

        // fill vbox to anchor pane and add border
        AnchorPane.setBottomAnchor(menuVBox, 0.0);
        AnchorPane.setTopAnchor(menuVBox, 0.0);
        AnchorPane.setLeftAnchor(menuVBox, 0.0);
        AnchorPane.setRightAnchor(menuVBox, 0.0);
        menuVBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: hidden hidden hidden solid");
        menuVBox.setPadding(new Insets(5, 10, 5, 10));

        // add created elements to base
        rightMenu.getChildren().add(menuVBox);
        baseHBox.getChildren().addAll(drawable, rightMenu);
        baseTab.setContent(baseHBox);
        rootTab.getTabs().add(baseTab);
    }
}
