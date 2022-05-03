/**
 * @brief Starting point of the program.
 * Initializes main structure and calls GClassDiagram.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file MainWindow.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

import ija.umleditor.models.ClassDiagram;
import ija.umleditor.models.JsonParser;
import ija.umleditor.template.Templates;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
        baseDiagram = new GClassDiagram(Templates.createClassDiagramModel(), mainTab);
    }

    /**
     * Creates new instance of class diagram.
     * @throws FileNotFoundException Exception is thrown in case of errors while loading assets
     */
    public void newClass() throws FileNotFoundException {
        // save old work??
        mainTab.getTabs().clear();
        baseDiagram = new GClassDiagram(Templates.createClassDiagramModel(), mainTab);
        GClassElement.initPositions();
    }

    /**
     * EventHandler or loading class diagram from file.
     * @throws FileNotFoundException Exception is thrown in case of non-existence of file.
     */
    public void loadFile() throws FileNotFoundException {
        GClassElement.initPositions();
        // create opening window dialog
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("JSON file", "*.json");
        fileChooser.getExtensionFilters().add(fileExtension);
        File selectedFile =  fileChooser.showOpenDialog(baseLayout.getScene().getWindow());

        // proceed to load file
        if (selectedFile.isFile()) {
            // TODO: ask to save work
            try {
                baseDiagram = new GClassDiagram(JsonParser.initFromFile(selectedFile.getAbsolutePath()), mainTab);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * EventHandler of storing class diagram.
     */
    public void storeFile() {
        // create saving window dialog
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("JSON file", "*.json");
        fileChooser.getExtensionFilters().add(fileExtension);
        File selectedFile = fileChooser.showSaveDialog(baseLayout.getScene().getWindow());
        if (selectedFile == null)
            return;
        String filePath = selectedFile.getAbsolutePath();
        if (!filePath.endsWith(".json"))
            filePath += ".json";

        // TODO: proceed to store diagram
        if (baseDiagram != null) {
            JsonParser.saveToFile(baseDiagram.getModel(), filePath);
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