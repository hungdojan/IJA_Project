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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Graphical representation of class diagram.
 */
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
     * Updates all sequence diagrams with a message.
     * @param msg The message that diagrams will be updated with.
     */
    public void notify(String msg) {
        for (var sd : gSequenceDiagramList) {
            sd.update(msg);
        }
    }

    /**
     * Gets list of sequence diagrams.
     * @return List of sequence diagrams.
     */
    public List<GSequenceDiagram> getgSequenceDiagramList() {
        return gSequenceDiagramList;
    }

    /**
     * Extracts images from assets folder and creates clickable panes with them.
     * @param root Root layout
     */
    private void addResources(VBox root) {
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
            // update sequence diagrams
            notify("class");
        });
        // interface
        ((AnchorPane) objects.get(1)).getChildren().get(0).setOnMouseClicked(ev -> {
            var createdElement = new GClassElement(canvas, (UMLClass) Templates.createInterfaceModel(model), this);
            gClassElementList.add(createdElement);
            // update sequence diagrams
            notify("class");
        });
        // object
        ((AnchorPane) objects.get(2)).getChildren().get(0).setOnMouseClicked(ev -> {
            var createdElement = new GClassElement(canvas, (UMLClass) Templates.createEmptyClassModel(model), this);
            gClassElementList.add(createdElement);
            // update sequence diagrams
            notify("class");
        });
    }

    /**
     * Creates left menu that contains list of objects that can be added to diagram.
     * @param leftPane Root pane
     * @param rootTab  Root tab pane
     */
    private void createLeftMenu(AnchorPane leftPane, TabPane rootTab) {
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
        tab.setOnSelectionChanged(e -> {
            deleteFlag = tab.isSelected();
            if (tab.isSelected()) {
                // event to delete selected class element with DELETE
                rootTab.setOnKeyPressed(ev -> {
                    if (ev.getCode() == KeyCode.DELETE && selectedElement != null && deleteFlag) {
                        commandBuilder.execute(new ICommand() {
                            final int index = classDiagram.getClassElements().indexOf(selectedElement.getModel());
                            final GClassElement element = selectedElement;
                            @Override
                            public void undo() {
                                model.getClassElements().add(index, element.getModel());
                                canvas.getChildren().add(element.getBaseLayout());
                                gClassElementList.add(element);
                                // update sequence diagrams
                                GClassDiagram.this.notify("class");
                            }

                            @Override
                            public void redo() {
                                model.removeClassElement(element.getModel());
                                canvas.getChildren().remove(element.getBaseLayout());
                                gClassElementList.remove(element);
                                // update sequence diagrams
                                GClassDiagram.this.notify("class");
                            }

                            @Override
                            public void execute() {
                                element.selected(false);
                                // remove class element from class diagram
                                model.removeClassElement(element.getModel());
                                // remove element from canvas
                                canvas.getChildren().remove(element.getBaseLayout());
                                // remove gClassElement
                                gClassElementList.remove(element);
                                // update sequence diagrams
                                GClassDiagram.this.notify("class");
                            }
                        });
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

            }
        });

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
        });
        canvas.setOnMouseDragged(ev -> {
            if (ev.getButton() == MouseButton.SECONDARY) {
                for (var gClassElement : gClassElementList) {
                    gClassElement.getBaseLayout().setTranslateX(gClassElement.getModel().getX() - posX + ev.getX());
                    gClassElement.getBaseLayout().setTranslateY(gClassElement.getModel().getY() - posY + ev.getY());
                }
            }
            ev.consume();
        });
        canvas.setOnMouseReleased(ev -> {
            if (ev.getButton() != MouseButton.SECONDARY)
                return;
            // create undo and redo action for moving all object on the canvas
            commandBuilder.execute(new ICommand() {
                final Map<UMLClassifier, Point2D> oldPos = new HashMap<>();
                final Map<UMLClassifier, Point2D> newPos = new HashMap<>();
                @Override
                public void undo() {
                    for (var elem : gClassElementList) {
                        var oldPoint = oldPos.get(elem.getModel());
                        elem.getModel().setX(oldPoint.getX());
                        elem.getModel().setY(oldPoint.getY());
                        elem.getBaseLayout().setTranslateX(oldPoint.getX());
                        elem.getBaseLayout().setTranslateY(oldPoint.getY());
                    }
                }

                @Override
                public void redo() {
                    for (var elem : gClassElementList) {
                        var newPoint = newPos.get(elem.getModel());
                        elem.getModel().setX(newPoint.getX());
                        elem.getModel().setY(newPoint.getY());
                        elem.getBaseLayout().setTranslateX(newPoint.getX());
                        elem.getBaseLayout().setTranslateY(newPoint.getY());
                    }
                }

                @Override
                public void execute() {
                    for (var elem : gClassElementList) {
                        var oldPoint = new Point2D(elem.getModel().getX(), elem.getModel().getY());
                        var newPoint = new Point2D(elem.getBaseLayout().getTranslateX(), elem.getBaseLayout().getTranslateY());
                        oldPos.put(elem.getModel(), oldPoint);
                        newPos.put(elem.getModel(), newPoint);
                        elem.getModel().setX(elem.getBaseLayout().getTranslateX());
                        elem.getModel().setY(elem.getBaseLayout().getTranslateY());
                    }
                }
            });
        });

        // add class elements
        for (var classElement : classDiagram.getClassElements()) {
            if (!(classElement instanceof UMLClass))
                continue;
            var createdElement = new GClassElement(canvas, (UMLClass) classElement, this);
            gClassElementList.add(createdElement);
        }

        // add relations
        List<UMLRelation> loadedRelations = new ArrayList<>();
        for (var classes : classDiagram.getClasses()) {
            for (var relation : classes.getRelations()) {
                if (loadedRelations.contains(relation))
                    continue;
                loadedRelations.add(relation);
                GClassElement gSrc = gClassElementList.stream().filter(x -> x.getModel() == relation.getSrc())
                        .findFirst().orElse(null);
                GClassElement gDst = gClassElementList.stream().filter(x -> x.getModel() == relation.getDest())
                        .findFirst().orElse(null);
                if (gSrc == null || gDst == null)
                    continue;
                GRelation gRelation = new GRelation(gSrc, gDst, canvas, relation);
                gRelationsList.add(gRelation);
            }
        }

        tab.setContent(content);
        rootTab.getTabs().add(tab);

        for (var sequenceDiagram : classDiagram.getSequenceDiagrams()) {
            var qSequenceDiagram = new GSequenceDiagram(rootTab, sequenceDiagram, this);
            gSequenceDiagramList.add(qSequenceDiagram);
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

    /**
     * Gets element from the list of all elements.
     * @param classElement The element to be found.
     * @return Instance of GClassElement
     */
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
                .filter(x -> (x.getSrcClass().getModel() == src && x.getDestClass().getModel() == dest) ||
                        (x.getSrcClass().getModel() == dest && x.getDestClass().getModel() == src))
                .findFirst().orElse(null);
    }

    /**
     * Removes relation from gRelationsList and from canvas.
     * @param relation Instance of GRelation to be removed
     * @return If removal was successful
     */
    public boolean removeRelation(GRelation relation) {
        gRelationsList.remove(relation);
        relation.getSrcClass().getModel().removeRelationWithClass(relation.getDestClass().getModel());
        return canvas.getChildren().remove(relation.getBaseStructure());
    }
}
