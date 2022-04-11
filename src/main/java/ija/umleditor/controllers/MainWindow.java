package ija.umleditor.controllers;

import ija.umleditor.models.ClassDiagram;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;

public class MainWindow {
    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane mainTab;
    @FXML
    private VBox baseLayout;

    private GClassDiagram baseDiagram;

    /**
     * Initializes rest of the window after creating main window layout.
     * @throws FileNotFoundException Exception is thrown in case of errors while loading assets
     */
    public void initialize() throws FileNotFoundException {
        baseDiagram = new GClassDiagram(createTemplateModel(), mainTab);
    }

    /**
     * Creates template class diagram for use.
     * @return Created instance of class diagram with some basic classifiers added
     */
    private ClassDiagram createTemplateModel() {
        ClassDiagram classDiagram = new ClassDiagram();
        classDiagram.addClassifiers(
                ClassDiagram.createClassifier("void", false),
                ClassDiagram.createClassifier("int", false),
                ClassDiagram.createClassifier("string", false),
                ClassDiagram.createClassifier("double", false),
                ClassDiagram.createClassifier("bool", false)
                );
        return classDiagram;
    }

    /**
     * Creates new instance of class diagram.
     * @throws FileNotFoundException Exception is thrown in case of errors while loading assets
     */
    public void newClass() throws FileNotFoundException {
        ClassDiagram model = createTemplateModel();
        mainTab.getTabs().clear();
        baseDiagram = new GClassDiagram(model, mainTab);
    }

    /**
     * EventHandler or loading class diagram from file.
     * @throws FileNotFoundException Exception is thrown in case of non-existence of file.
     */
    public void loadFile() throws FileNotFoundException {
        // create opening window dialog
        FileChooser fileChooser = new FileChooser();
        File selectedFile =  fileChooser.showOpenDialog(baseLayout.getScene().getWindow());

        // proceed to load file
        if (selectedFile.isFile()) {
            // TODO: ask to save work
            baseDiagram = new GClassDiagram(ClassDiagram.initClassDiagramFromFile(selectedFile.getAbsolutePath()), mainTab);
        }
    }

    /**
     * EventHandler of storing class diagram.
     */
    public void storeFile() {
        // create saving window dialog
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showSaveDialog(baseLayout.getScene().getWindow());

        // TODO: proceed to store diagram
        if (baseDiagram != null) {
            ClassDiagram.saveClassDiagramToFile(baseDiagram.getModel(), selectedFile.getAbsolutePath());
        }
    }

    /**
     * Opens help dialog window.
     */
    public void showHelp() {
        // TODO: show Help dialog
        System.out.println("Help dialog");
    }

    /**
     * Opens about dialog window.
     */
    public void showAbout() {
        // TODO: show About dialog
        System.out.println("About dialog");
    }

    /**
     * Close program from MenuBar
     */
    public void close() {
        // TODO: ask for save?
        Platform.exit();
    }
}