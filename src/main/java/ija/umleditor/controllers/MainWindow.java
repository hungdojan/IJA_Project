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

import ija.umleditor.models.JsonParser;
import ija.umleditor.template.Templates;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Starting point of the program.
 */
public class MainWindow {
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
        mainTab.getTabs().clear();
        baseDiagram = new GClassDiagram(Templates.createClassDiagramModel(), mainTab);
    }

    /**
     * EventHandler or loading class diagram from file.
     * @throws FileNotFoundException Exception is thrown in case of non-existence of file.
     */
    public void loadFile() throws FileNotFoundException {
        // create opening window dialog
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("JSON file", "*.json");
        fileChooser.getExtensionFilters().add(fileExtension);
        File selectedFile =  fileChooser.showOpenDialog(baseLayout.getScene().getWindow());

        // proceed to load file
        if (selectedFile != null && selectedFile.isFile()) {
            mainTab.getTabs().clear();
            try {
                baseDiagram = new GClassDiagram(JsonParser.initFromFile(selectedFile.getAbsolutePath()), mainTab);
            } catch (IOException e) {
                baseDiagram = new GClassDiagram(Templates.createClassDiagramModel(), mainTab);
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

        if (baseDiagram != null) {
            JsonParser.saveToFile(baseDiagram.getModel(), filePath);
        }
    }

    /**
     * Opens about dialog window.
     */
    public void showAbout() {
        Dialog<String> aboutDialog = new Dialog<>();
        aboutDialog.getDialogPane().setMinHeight(150);
        aboutDialog.getDialogPane().setMinWidth(500);
        aboutDialog.setTitle("About");
        aboutDialog.setContentText(
                "Submission for semester assignment of class IJA at FIT, BUT 2021/22.\n" +
                "Authors: Do Hung, Kolarik Petr\n" +
                "Date of completion: 07/05/2022"
        );
        aboutDialog.show();
        aboutDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    /**
     * Opens help dialog window.
     */
    public void showHelp() {
        Dialog<String> helpDialog = new Dialog<>();
        helpDialog.getDialogPane().setMinHeight(600);
        helpDialog.getDialogPane().setMinWidth(1050);
        helpDialog.setTitle("Help");
        helpDialog.setContentText(
                "KEYBOARD SHORTCUTS:\n\n" +

                        "ctrl + Z: undo\n" +
                        "ctrl + Y: redo\n" +
                        "ctrl + S: save file\n" +
                        "ctrl + O: open file\n" +
                        "ctrl + N: create brand-new class diagram (original diagram is deleted)\n\n" +

                        "CLASS DIAGRAM:\n\n" +

                        "Adding element: Pick one of the templates from the left side and press it.\n" +
                        "Editing element: Click on element you want to edit and menu will pop up from the right side.\n" +
                        "Functions of the right menu:\n" +
                        "\t Name: Edit name, set if element is abstract a set its stereotype.\n" +
                        "\t Attributes: Add or delete attribute or edit visibility, type and name of already existing one.\n" +
                        "\t Operations: Add or delete operation or edit visibility, type and name of already existing one.\n" +
                        "\t\t Parameters: Add or delete parameter or edit type and name of already existing one.\n" +
                        "\t Relations: \"Add relation\" button opens menu where it is necessary to enter name and type of relation. Button \"Draw\" creates the relation.\n" +
                        "\t\t Types of relation: Association - black line; Aggregation - green line; Composition - blue line; Inheritance - orange line.\n" +
                        "Deleting element: Click on element and press delete button on keyboard.\n" +
                        "Moving with object: Use left mouse button and drag.\n" +
                        "Moving with canvas: Use right mouse button and drag.\n" +
                        "Creating sequence diagram: Press the \"Create sequence diagram\" button in left menu.\n\n" +

                        "SEQUENCE DIAGRAM:\n\n" +

                        "Adding object: Click on \"Add object\" button in right menu.\n" +
                        "Editing object: Click on object you want to edit. You can change its name and type and press return button on your keyboard.\n" +
                        "Adding message: Click on \"Add message\" button in right menu and grid for setting the message will pop up.\n" +
                        "Setting message: Set source object, destination object, type and either set your own text or pick one of the operations. Button \"Draw\" creates the message.\n" +
                        "Deleting diagram: Click on \"Delete diagram\" button in right menu."

        );
        helpDialog.show();
        helpDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    /**
     * Close program from MenuBar
     */
    public void close() {
        Platform.exit();
    }
}