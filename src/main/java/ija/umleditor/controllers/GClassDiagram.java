package ija.umleditor.controllers;

import ija.umleditor.models.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GClassDiagram {
    private static int classCounter = 1;
    private final ClassDiagram model;
    private GClassElement selectedElement = null;
    private RightMenu rightMenu = null;

    private final HBox content;
    private final Pane canvas;

    private double mousePanePosX;
    private double mousePanePosY;
    private boolean clickable = true;

    public ClassDiagram getModel() {
        return model;
    }

    /**
     * Extracts images from assets folder and creates clickable panes with them.
     * @param root Root layout
     * @throws FileNotFoundException Unable to get assets
     */
    private void addResources(VBox root) throws FileNotFoundException {
        List<String> lofAssets = new ArrayList<>(
                Arrays.asList("/class.png", "/interface.png", "/object.png")
        );
        for (var pathName : lofAssets) {

            // get image from list
            FileInputStream input = new FileInputStream(getClass().getResource(pathName).getPath());

            // create Image and ImageView
            Image classImg = new Image(input);
            ImageView classImgView = new ImageView();

            // set size of images
            classImgView.setPreserveRatio(true);
            classImgView.setFitWidth(200);

            // insert Image into ImageView
            classImgView.setImage(classImg);

            // create Pane and put ImageView into it
            AnchorPane pic1 = new AnchorPane();
            pic1.getChildren().add(classImgView);
            root.getChildren().add(pic1);

            // set scaling ang margins of VBox
            VBox.setVgrow(pic1, Priority.ALWAYS);
            VBox.setMargin(pic1, new Insets(20, 20, 20, 20));
            // TODO: event on mouse clicked
            // pic1.setOnMouseClicked(ev -> {
            //     new GClassElement(canvas, new UMLClass("test"), this);
            // });
        }
        var objects = root.getChildren();
        // only image view is clickable
        // class
        ((AnchorPane) objects.get(0)).getChildren().get(0).setOnMouseClicked(ev -> {
            int operationCounter = 1;
            var inter = (UMLClass) ClassDiagram.createClassifier("Class" + classCounter++, true);
            inter.addAttribute(UMLClass.createAttribute(true, "Operation" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(false, "Attribute" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(false, "Attribute" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(true, "Attribute" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(true, "Attribute" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(false, "Attributeaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + operationCounter++, model.getClassifier("void")));
            inter.addAttribute(UMLClass.createAttribute(false, "Attribute" + operationCounter++, model.getClassifier("void")));
            new GClassElement(canvas, inter, this);
        });
        // interface
        ((AnchorPane) objects.get(1)).getChildren().get(0).setOnMouseClicked(ev -> {
            int operationCounter = 1;
            var inter = (UMLClass) ClassDiagram.createClassifier("Class" + classCounter++, true);
            inter.addAttribute(UMLClass.createAttribute(true, "Attribute" + operationCounter++, model.getClassifier("void")));
            new GClassElement(canvas, inter, this);
        });
        // object
        ((AnchorPane) objects.get(2)).getChildren().get(0).setOnMouseClicked(ev -> {
            new GClassElement(canvas, ClassDiagram.createClassifier("Class" + classCounter++, true), this);
        });
    }

    /**
     * Creates left menu that contains list of objects that can be added to diagram.
     * @param leftPane Root pane
     * @param rootTab  Root tab pane
     * @throws FileNotFoundException Unable to get assets
     */
    private void createLeftMenu(AnchorPane leftPane, TabPane rootTab) throws FileNotFoundException {
        VBox objectPane = new VBox();
        addResources(objectPane);

        // create separator button x objects
        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);
        sep.setHalignment(HPos.CENTER);

        // button to create sequence diagram
        Button createSD = new Button("Create sequence diagram");
        createSD.setAlignment(Pos.CENTER);
        createSD.setMaxWidth(Double.MAX_VALUE);
        createSD.setStyle("-fx-background-radius: 15px");
        createSD.setOnMouseClicked(ev -> {
            // TODO: init with model
            new GSequenceDiagram(rootTab, new SequenceDiagram("test"));
        });

        //TESTING BUTTON TO ADD ATTRIBUTE
        Button addAttr = new Button("add attribute");
        addAttr.setAlignment(Pos.CENTER);
        addAttr.setMaxWidth(Double.MAX_VALUE);
        addAttr.setStyle("-fx-background-radius: 15px");
        addAttr.setOnAction(ev -> {
            if (selectedElement != null)
                selectedElement.addAttribute(new UMLAttribute("ahoj", model.getClassifier("string")));
        });

        // set margin between items in vbox
        objectPane.getChildren().addAll(createSD, addAttr);
        objectPane.getChildren().add(3, sep);
        VBox.setMargin(createSD, new Insets(5, 5, 5, 5));
        VBox.setVgrow(createSD, Priority.ALWAYS);

        // fill size to match parents
        AnchorPane.setBottomAnchor(objectPane, 0.0);
        AnchorPane.setTopAnchor(objectPane, 0.0);
        AnchorPane.setLeftAnchor(objectPane, 0.0);
        AnchorPane.setRightAnchor(objectPane, 0.0);
        objectPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: hidden solid hidden hidden");

        leftPane.getChildren().add(objectPane);
    }

    /**
     * Class {@code GClassDiagram} constructor.
     * @param classDiagram Base model
     * @param rootTab Root tab pane
     * @throws FileNotFoundException Unable to get assets
     */
    public GClassDiagram(ClassDiagram classDiagram, TabPane rootTab) throws FileNotFoundException {
        model = Objects.requireNonNull(classDiagram);
        Tab tab = new Tab("Class diagram");
        tab.setClosable(false);

        // base content
        content = new HBox();

        // drawable canvas
        canvas = new AnchorPane();
        HBox.setHgrow(canvas, Priority.ALWAYS);
        canvas.setOnMouseClicked(ev -> {
            if (clickable) {
                setSelectedElement(null);
            }
            clickable = true;
        });
        Rectangle clipRect = new Rectangle(canvas.getWidth(), canvas.getHeight());
        clipRect.heightProperty().bind(canvas.heightProperty());
        clipRect.widthProperty().bind(canvas.widthProperty());
        canvas.setClip(clipRect);
        // TODO: testing pane movement
        // canvas.setStyle("-fx-background-color: #dedede");
        // canvas.setOnMousePressed(ev -> {
        //     mousePanePosX = ev.getX();
        //     mousePanePosY = ev.getY();
        // });
        // canvas.setOnMouseDragged(ev -> {
        //     // TODO: move with group
        //     clickable = false;
        //     canvas.setTranslateX(canvas.getTranslateX() + ev.getX() - mousePanePosX);
        //     canvas.setTranslateY(canvas.getTranslateY() + ev.getY() - mousePanePosY);
        // });
        // set clip
        // TODO: end of testing
        // TODO: add classDiagram content to canvas

        // left panel with objects to create
        AnchorPane leftPane = new AnchorPane();
        createLeftMenu(leftPane, rootTab);

        // right menu with selected class
        content.getChildren().addAll(leftPane, canvas);

        // TODO: rozlozeni
        tab.setContent(content);
        rootTab.getTabs().add(tab);
    }

    /**
     * Change status of selected element.
     * @param element New selected element
     */
    public void setSelectedElement(GClassElement element) {
        // unselect current element
        if (selectedElement != null) {
            selectedElement.selected(false);
            rightMenu.remove(content);
            rightMenu = null;
        }

        // select new element
        selectedElement = element;
        if (selectedElement != null) {
            selectedElement.selected(true);
            rightMenu = new RightMenu(selectedElement, content);
        }
    }

    // TODO: command undo-redo
}
