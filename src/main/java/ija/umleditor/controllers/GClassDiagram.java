/**
 * @brief Creates class diagram.
 * HBox layout contains left menu with class templates, canvas to draw the diagram on and right menu to edit elements.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file GClassDiagram.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.*;
import ija.umleditor.template.Templates;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GClassDiagram {
    private final ClassDiagram model;
    private GClassElement selectedElement = null;
    private RightMenu rightMenu = null;
    private final List<GClassElement> gClassElementList = new ArrayList<>();
    private final List<GRelation> gRelationsList = new ArrayList<>();
    private final List<GSequenceDiagram> gSequenceDiagramList = new ArrayList<>();
    private double posX;
    private double posY;
    private final CommandBuilder commandBuilder = new CommandBuilder();

    private final HBox content;
    private final Pane canvas;

    // element can be clicked on and was not dragged
    private boolean clickable = true;
    // user is in current diagram's tab and selected element can be deleted
    private boolean deleteFlag = true;

    /**
     * Gets model of type ClassDiagram
     * @return model
     */
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

            // create Image and ImageView
            Image classImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(pathName)));
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
        }

        var objects = root.getChildren();
        // only image view is clickable
        // class
        ((AnchorPane) objects.get(0)).getChildren().get(0).setOnMouseClicked(ev -> {
            var createdElement = new GClassElement(canvas, (UMLClass) Templates.createClassModel(model), this);
            gClassElementList.add(createdElement);
        });
        // interface
        ((AnchorPane) objects.get(1)).getChildren().get(0).setOnMouseClicked(ev -> {
            var createdElement = new GClassElement(canvas, (UMLClass) Templates.createInterfaceModel(model), this);
            gClassElementList.add(createdElement);
        });
        // object
        ((AnchorPane) objects.get(2)).getChildren().get(0).setOnMouseClicked(ev -> {
            var createdElement = new GClassElement(canvas, (UMLClass) Templates.createEmptyClassModel(model), this);
            gClassElementList.add(createdElement);
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
            var sequenceDiagram = Templates.createSequenceDiagram(model);
            var qSequenceDiagram = new GSequenceDiagram(rootTab, sequenceDiagram, this);
            gSequenceDiagramList.add(qSequenceDiagram);
        });

        // set margin between items in vbox
        objectPane.getChildren().addAll(createSD);
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
        // set flag to ignore deleting element when class diagram tab is not in focus
        tab.setOnSelectionChanged(ev -> deleteFlag = tab.isSelected());

        // base content
        content = new HBox();

        // drawable canvas
        // ScrollPane <--|
        ScrollPane drawable = new ScrollPane();
        drawable.setFitToHeight(true);
        drawable.setFitToWidth(true);

        canvas = new AnchorPane();
        drawable.setContent(canvas);
        HBox.setHgrow(drawable, Priority.ALWAYS);
        // selecting element
        canvas.setOnMouseClicked(ev -> {
            if (clickable) {
                setSelectedElement(null);
            }
            clickable = true;
            ev.consume();
        });
        // define movement of canvas
        canvas.setOnMousePressed(ev -> {
            posX = ev.getX();
            posY = ev.getY();
            for (var gClassElement : gClassElementList) {
                gClassElement.storeRelativePosition();
            }
        });
        canvas.setOnMouseDragged(ev -> {
            if (ev.getButton() == MouseButton.SECONDARY) {
                for (var gClassElement : gClassElementList) {
                    var point = gClassElement.getRelativePosition();
                    gClassElement.getBaseLayout().setTranslateX(point.getX() - posX + ev.getX());
                    gClassElement.getBaseLayout().setTranslateY(point.getY() - posY + ev.getY());
                }
            }
            ev.consume();
        });

        for (var classElement : classDiagram.getClassElements()) {
            if (!(classElement instanceof UMLClass))
                continue;
            var createdElement = new GClassElement(canvas, (UMLClass) classElement, this);
            gClassElementList.add(createdElement);
        }
        Rectangle clipRect = new Rectangle(canvas.getWidth(), canvas.getHeight());
        clipRect.heightProperty().bind(canvas.heightProperty());
        clipRect.widthProperty().bind(canvas.widthProperty());
        canvas.setClip(clipRect);

        // left panel with objects to create
        AnchorPane leftPane = new AnchorPane();
        createLeftMenu(leftPane, rootTab);

        // right menu with selected class
        content.getChildren().addAll(leftPane, drawable);

        // event to delete selected class element with DELETE
        rootTab.setOnKeyPressed(ev -> {
            if (ev.getCode() == KeyCode.DELETE) {
                if (selectedElement == null || !deleteFlag) {
                    return;
                }
                var command = new ICommand() {
                    @Override
                    public void undo() {

                    }

                    @Override
                    public void redo() {

                    }

                    @Override
                    public void execute() {

                    }
                };
                commandBuilder.execute(command);
                // remove from model
                model.removeClassElement(selectedElement.getModel());
                // remove from canvas
                canvas.getChildren().remove(selectedElement.getBaseLayout());
                // remove gClassElement from the list
                gClassElementList.remove(selectedElement);
                // resets element
                setSelectedElement(null);
            }
            else if (ev.isControlDown() && ev.getCode() == KeyCode.Z) {
                commandBuilder.undo();
            }
            else if (ev.isControlDown() && ev.getCode() == KeyCode.Y) {
                commandBuilder.redo();
            }
        });

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

    public GClassElement getClassElement(UMLClass classElement) {
        return gClassElementList.stream()
                .filter(x -> x.getModel() == classElement)
                .findFirst().orElse(null);
    }

    /**
     * Gets canvas.
     * @return Instance of canvas
     */
    public Pane getCanvas() {
        return canvas;
    }

    /**
     * Adds created relation to gRelationsList.
     * @param relation Instance of GRelation
     */
    public void addRelation(GRelation relation) {
        gRelationsList.add(relation);
    }

    /**
     * Gets instance of CommandBuilder.
     * @return Instance of CommandBuilder
     */
    public CommandBuilder getCommandBuilder() {
        return commandBuilder;
    }

    /**
     * Gets relation between two instances of UMLClass.
     * @param src Starting instance of UMLClass
     * @param dest Ending instance of UMLClass
     * @return Instance of GRelation
     */
    public GRelation getRelation(UMLClass src, UMLClass dest) {
        return gRelationsList.stream()
                .filter(x -> (x.getModel1().getModel() == src && x.getModel2().getModel() == dest) ||
                        (x.getModel1().getModel() == dest && x.getModel2().getModel() == src))
                .findFirst().orElse(null);
    }

    /**
     * Removes relation from gRelationsList and from cavnas.
     * @param relation Instance of GRelation to be removed
     * @return If removal was successful
     */
    public boolean removeRelation(GRelation relation) {
        gRelationsList.remove(relation);
        return canvas.getChildren().remove(relation.getBaseStructure());
    }
    // TODO: command undo-redo
}
