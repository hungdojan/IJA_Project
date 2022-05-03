package ija.umleditor;

import ija.umleditor.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassDiagramBasic {

    private ClassDiagram classDiagram;
    @BeforeEach
    public void setUp() {
        classDiagram = new ClassDiagram();
    }
    @AfterEach
    public void tearDown() { }

    @Test
    @DisplayName("Test empty class")
    public void testEmptyClass() {
        List<UMLClassifier> list = classDiagram.getClassElements();
        assertEquals(0, list.size());
    }

    @Test
    @DisplayName("Basic classifier insertion")
    public void addClassifierBasic() {
        List<UMLClassifier> lofClassifiers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            UMLClassifier classifier = ClassDiagram.createClassifier("classifier" + i, false);
            lofClassifiers.add(classifier);
            assertTrue(classDiagram.addClassifier(classifier));
        }

        // inserting already defined classes
        for (UMLClassifier lofClassifier : lofClassifiers) {
            assertFalse(classDiagram.addClassifier(lofClassifier));
        }
    }

    @Test
    @DisplayName("Factory method test")
    public void createUMLClass() {
        // create uml Classifier
        UMLClassifier classifier = ClassDiagram.createClassifier("classifier", false);
        assertNotNull(classifier);
        assertFalse(classifier instanceof UMLClass);

        // create UMLClass
        assertTrue(ClassDiagram.createClassifier("class", true) instanceof UMLClass);
    }

    @Test
    @DisplayName("Get classifier from class diagram")
    public void getClassifier() {
        classDiagram.addClassifier(ClassDiagram.createClassifier("classifier", false));
        classDiagram.addClassifier(ClassDiagram.createClassifier("class", true));

        // check types
        assertNotNull(classDiagram.getClassifier("classifier"));
        assertNull(classDiagram.getClass("classifier"));
        assertNotNull(classDiagram.getClassifier("class"));
        assertNotNull(classDiagram.getClass("class"));
    }

    @Test
    @DisplayName("Add multiple classifiers")
    public void addClassifiersBasic2() {
        UMLClassifier cls1 = ClassDiagram.createClassifier("cls1", false);
        UMLClassifier cls2 = ClassDiagram.createClassifier("cls2", false);
        UMLClass cls3 = (UMLClass) ClassDiagram.createClassifier("cls3", true);
        // class with identical name as "cls2"
        UMLClass cls4 = (UMLClass) ClassDiagram.createClassifier("cls2", true);

        classDiagram.addClassifiers(cls1, cls2, cls3, cls4, cls2);

        // class diagram should contain only cls1, cls2 and cls3
        assertEquals(classDiagram.getClassElements().size(), 3);
        assertFalse(classDiagram.getClassifier("cls2") instanceof UMLClass);
        assertNull(classDiagram.getClass("cls2"));
        assertNull(classDiagram.getClass("cls4"));
        assertNotNull(classDiagram.getClass("cls3"));
    }

    @Test
    @DisplayName("Case sensitive test")
    public void caseSensitivityTest() {
        UMLClassifier cls1 = ClassDiagram.createClassifier("cls1", false);
        UMLClassifier cls2 = ClassDiagram.createClassifier("CLS1", false);
        UMLClassifier cls3 = ClassDiagram.createClassifier("cLs1", true);
        UMLClassifier cls4 = ClassDiagram.createClassifier("CLs1", true);
        // identical name as "cls1"
        UMLClassifier cls5 = ClassDiagram.createClassifier("cls1", true);

        classDiagram.addClassifiers(cls1, cls2, cls3, cls4, cls5);

        assertEquals(classDiagram.getClassElements().size(), 4);
        assertEquals(classDiagram.getClassifier("cls1"), cls1);
        assertEquals(classDiagram.getClassifier("CLS1"), cls2);
        assertEquals(classDiagram.getClassifier("cLs1"), cls3);
        assertEquals(classDiagram.getClassifier("CLs1"), cls4);

        // remove class that isn't in the class diagram but shares same as one of the class in the diagram
        assertFalse(classDiagram.isInClassDiagram(cls5));

        // nothing should change though
        assertEquals(classDiagram.getClassElements().size(), 4);
    }

    @Test
    @DisplayName("Replace existing classifier")
    public void replaceClassifier() {
        UMLClassifier cls1 = ClassDiagram.createClassifier("cls1", false);
        UMLClassifier cls2 = ClassDiagram.createClassifier("cls1", true);

        // inserting classifier with name that is already in use will be ignored
        assertTrue(classDiagram.addClassifier(cls1));
        assertFalse(classDiagram.addClassifier(cls2));
        assertEquals(classDiagram.getClassElements().size(), 1);

        // but when using "addOrReplaceClassifier" method
        // old one should be replaced by new one
        assertTrue(classDiagram.removeClassElement(cls1));
        assertNull(classDiagram.addOrReplaceClassifier(cls1));
        assertEquals(classDiagram.addOrReplaceClassifier(cls2), cls1);
        assertEquals(classDiagram.getClassElements().size(), 1);
    }

    @Test
    @DisplayName("Remove classifier by name")
    public void removeClassifierByName() {
        UMLClassifier cls1 = ClassDiagram.createClassifier("cls1", false);
        UMLClassifier cls2 = ClassDiagram.createClassifier("cls2", false);
        UMLClass cls3 = (UMLClass) ClassDiagram.createClassifier("cls3", true);
        UMLClass cls4 = (UMLClass) ClassDiagram.createClassifier("cls4", true);

        classDiagram.addClassifiers(cls1, cls2, cls3, cls4);

        // check insertion status
        assertEquals(classDiagram.getClassElements().size(), 4);

        // remove all items by name
        for (int i = 0; i < 4; i++) {
            if (i == 0)
                assertFalse(classDiagram.removeClassElement("cls" + i));
            else
                assertTrue(classDiagram.removeClassElement("cls" + i));
        }

        // check updated status
        assertEquals(classDiagram.getClassElements().size(), 1);
        assertEquals(classDiagram.getClassifier("cls4"), cls4);
        assertTrue(classDiagram.removeClassElement(cls4));
    }

    @Test
    @DisplayName("Change class name")
    public void changeName() {
        UMLClassifier cls1 = ClassDiagram.createClassifier("cls1", false);
        UMLClassifier cls2 = ClassDiagram.createClassifier("cls2", false);
        UMLClass cls3 = (UMLClass) ClassDiagram.createClassifier("cls3", true);
        UMLClass cls4 = (UMLClass) ClassDiagram.createClassifier("cls4", true);
        UMLClass cls5 = (UMLClass) ClassDiagram.createClassifier("cls4", true);

        classDiagram.addClassifiers(cls1, cls2, cls3, cls4);

        // change by name
        assertTrue(classDiagram.changeClassifierName("cls1", "Cls1"));
        assertTrue(classDiagram.changeClassifierName("cls2", "cls1"));
        assertFalse(classDiagram.changeClassifierName("cls3", "cls4"));

        // check if classifiers stayed unchanged
        assertEquals(cls3, classDiagram.getClassifier("cls3"));
        assertEquals(cls4, classDiagram.getClassifier("cls4"));

        // change by object
        assertFalse(classDiagram.changeClassifierName(cls5, "cls5"));
        assertFalse(classDiagram.changeClassifierName(cls3, "cls4"));
        assertFalse(classDiagram.changeClassifierName(cls3, "cls3"));
        assertTrue(classDiagram.changeClassifierName(cls4, "cls5"));

        // check class content
        assertEquals(classDiagram.getClassifier("cls5"), cls4);
        assertEquals(classDiagram.getClassifier("cls3"), cls3);
        assertEquals(classDiagram.getClassifier("cls1"), cls2);
        assertEquals(classDiagram.getClassifier("Cls1"), cls1);
    }
}
