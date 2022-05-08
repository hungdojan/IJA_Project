package ija.umleditor;

import ija.umleditor.models.ClassDiagram;
import ija.umleditor.models.UMLClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClassDiagramComplex {

    private ClassDiagram classDiagram;
    @BeforeEach
    public void setUp() {
        classDiagram = new ClassDiagram();
        List<UMLClassifier> listOfClassifiers = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                listOfClassifiers.add(ClassDiagram.createClassifier("classifier" + i, false));
            } else {
                listOfClassifiers.add(ClassDiagram.createClassifier("class" + i, true));
            }
        }

        for (var item : listOfClassifiers) {
            classDiagram.addClassifier(item);
        }
    }

    @Test
    @DisplayName("Number of classifiers in diagram")
    public void numberOfClassifiers() {
        assertEquals(classDiagram.getClassElements().size(), 6);
    }

    @Test
    @DisplayName("Get class name")
    public void className() {
        var list = classDiagram.getClassElements();
        assertEquals(list.get(0).getName(), "classifier0");
    }
}
