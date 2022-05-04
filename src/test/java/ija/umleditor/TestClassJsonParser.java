package ija.umleditor;

import ija.umleditor.models.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestClassJsonParser {
    private ClassDiagram classDiagram;

    @BeforeEach
    public void setup() {
        classDiagram = new ClassDiagram();
        classDiagram.addClassifier(ClassDiagram.createClassifier("void", false));
        classDiagram.addClassifier(ClassDiagram.createClassifier("cls1", true));
        classDiagram.addClassifier(ClassDiagram.createClassifier("cls2", true));
        classDiagram.addClassifier(ClassDiagram.createClassifier("inter1", true));
        classDiagram.addClassifier(ClassDiagram.createClassifier("inter2", true));

        UMLClass cls1 = (UMLClass) classDiagram.getClassifier("cls1");
        cls1.addAttributes(
                UMLClass.createAttribute(false, "attr1", classDiagram.getClassifier("cls2")),
                UMLClass.createAttribute(false, "attr2", classDiagram.getClassifier("inter1")),
                UMLClass.createAttribute(true, "oper1", classDiagram.getClassifier("inter1"),
                        UMLClass.createAttribute(false, "param1", classDiagram.getClassifier("inter1")),
                        UMLClass.createAttribute(false, "param2", classDiagram.getClassifier("inter2")))
        );
        UMLClass cls2 = (UMLClass) classDiagram.getClassifier("cls2");
        cls2.addAttributes(
                UMLClass.createAttribute(false, "attr1", classDiagram.getClassifier("void")),
                UMLClass.createAttribute(true, "oper1", classDiagram.getClassifier("inter1"),
                        UMLClass.createAttribute(false, "param1", classDiagram.getClassifier("inter2")),
                        UMLClass.createAttribute(false, "param2", classDiagram.getClassifier("inter1")))
        );
        UMLClass inter1 = (UMLClass) classDiagram.getClassifier("inter1");
        inter1.setAbstract(true);
        inter1.addAttributes(
                UMLClass.createAttribute(true, "oper1", classDiagram.getClassifier("cls2"))
        );
        UMLClass inter2 = (UMLClass) classDiagram.getClassifier("inter2");
        inter2.setAbstract(true);
        inter2.addAttributes(
                UMLClass.createAttribute(true, "oper1", classDiagram.getClassifier("cls2")),
                UMLClass.createAttribute(true, "oper2", classDiagram.getClassifier("inter2"),
                        UMLClass.createAttribute(false, "param1", classDiagram.getClassifier("inter2"))),
                UMLClass.createAttribute(true, "oper3", classDiagram.getClassifier("inter1"),
                        UMLClass.createAttribute(false, "param1", classDiagram.getClassifier("inter2")),
                        UMLClass.createAttribute(false, "param2", classDiagram.getClassifier("inter1")))
        );

        cls1.addRelation(inter1, RelationType.AGGREGATION);
        cls1.addRelation(inter2, RelationType.AGGREGATION);
        cls1.addRelation(cls2, RelationType.AGGREGATION);

        var sd1 = classDiagram.addSequenceDiagram("SequenceDiagram1");
        var obj1 = sd1.addObject(cls1, "obj1");
        var obj2 = sd1.addObject(cls2, "obj2");
        var msg1 = sd1.addMessage(obj1, obj2);
        msg1.setMessage((UMLOperation) obj2.getClassOfInstance().getAttribute("oper1"));
        var msg2 = sd1.addMessage(obj2, obj1);
        msg2.getNameProperty().setValue("response");
        var msg3 = sd1.addMessage(null, obj1);
        msg3.setMessage((UMLOperation) obj1.getClassOfInstance().getAttribute("oper1"));
        var msg4 = sd1.addMessage(obj1, obj1);
        msg4.setMessage((UMLOperation) obj1.getClassOfInstance().getAttribute("oper1"));
        var sd2 = classDiagram.addSequenceDiagram("SequenceDiagram2");
        sd2.addObject(cls1, "obj1");
        sd2.addObject(cls2, "obj2");
    }

    @Test
    void BasicTest() throws IOException {
        JsonParser.saveToFile(classDiagram, "test.json");
        ClassDiagram temp = JsonParser.initFromFile("test.json");
        JsonParser.saveToFile(temp, "test2.json");
    }
}
