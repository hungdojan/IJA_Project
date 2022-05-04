package ija.umleditor.template;

import ija.umleditor.models.*;

public class Templates {
    private static int classCounter = 1;
    private static int sequenceDiagramCounter = 1;
    private static int objectCounter = 1;

    public static void resetClassCounter() {
        Templates.classCounter = 1;
    }

    /**
     * Creates template class diagram for use.
     * @return Created instance of class diagram with some basic classifiers added
     */
    public static ClassDiagram createClassDiagramModel() {
        ClassDiagram model = new ClassDiagram();
        model.addClassifiers(
                ClassDiagram.createClassifier("void", false),
                ClassDiagram.createClassifier("int", false),
                ClassDiagram.createClassifier("string", false),
                ClassDiagram.createClassifier("double", false),
                ClassDiagram.createClassifier("bool", false)
        );
        Templates.classCounter = 1;
        return model;
    }

    public static SequenceDiagram createSequenceDiagram(ClassDiagram baseDiagram) {
        SequenceDiagram model = new SequenceDiagram("SequenceDiagram" + sequenceDiagramCounter++);
        baseDiagram.addSequenceDiagram(model);
        return model;
    }

    public static UMLClassifier createClassModel(ClassDiagram baseDiagram) {
        int attributeCounter = 1;
        int operationCounter = 1;
        UMLClass model = new UMLClass("Class" + classCounter++);
        model.addAttribute(UMLClass.createAttribute(false, "Attribute" + model.getAttributeCounter(), baseDiagram.getClassifier("void")));
        model.addAttribute(UMLClass.createAttribute(false, "Attribute" + model.getAttributeCounter(), baseDiagram.getClassifier("void")));
        model.addAttribute(UMLClass.createAttribute(true, "Operation" + model.getOperationCounter(), baseDiagram.getClassifier("void")));
        model.addAttribute(UMLClass.createAttribute(true, "Operation" + model.getOperationCounter(), baseDiagram.getClassifier("void")));
        while (!baseDiagram.addClassifier(model)) {
            model.setName("Class" + classCounter++);
        }
        return model;
    }

    public static UMLClassifier createInterfaceModel(ClassDiagram baseDiagram) {
        UMLClass model = (UMLClass) ClassDiagram.createClassifier("Class" + classCounter++, true);
        model.addAttribute(UMLClass.createAttribute(true, "Operation" + model.getOperationCounter(), baseDiagram.getClassifier("void")));
        model.addAttribute(UMLClass.createAttribute(true, "Operation" + model.getOperationCounter(), baseDiagram.getClassifier("void")));
        model.setAbstract(true);
        model.setStereotype("<<interface>>");
        while (!baseDiagram.addClassifier(model)) {
            model.setName("Class" + classCounter++);
        }
        return model;
    }

    /**
     * Creates empty class template.
     * @return Instance of empty UMLClass
     */
    public static UMLClassifier createEmptyClassModel(ClassDiagram baseDiagram) {
        var model = ClassDiagram.createClassifier("Class" + classCounter++, true);
        while (!baseDiagram.addClassifier(model)) {
            model.setName("Class" + classCounter++);
        }
        return model;
    }

    public static UMLAttribute createAttribute(UMLClass baseClass, ClassDiagram baseDiagram) {
        var type = baseDiagram.getClassifier("string");
        if (type == null)
            baseDiagram.addClassifier(ClassDiagram.createClassifier("string", false));
        type = baseDiagram.getClassifier("string");
        return UMLClass.createAttribute(false, "Attribute" + baseClass.getAttributeCounter(), type);
    }

    public static UMLOperation createOperation(UMLClass baseClass, ClassDiagram baseDiagram) {
        var type = baseDiagram.getClassifier("string");
        if (type == null)
            baseDiagram.addClassifier(ClassDiagram.createClassifier("string", false));
        type = baseDiagram.getClassifier("string");
        return (UMLOperation) UMLClass.createAttribute(true, "Operation" + baseClass.getOperationCounter(), type);
    }

    public static UMLAttribute createParameter(UMLOperation baseOperation, ClassDiagram baseDiagram) {
        var type = baseDiagram.getClassifier("string");
        if (type == null)
            baseDiagram.addClassifier(ClassDiagram.createClassifier("string", false));
        type = baseDiagram.getClassifier("string");
        return (UMLAttribute) UMLClass.createAttribute(false, "Parameter"+ UMLOperation.getParameterCounter(), type);
    }

    public static UMLObject createObject() {
        return new UMLObject("Object" + objectCounter++, SequenceDiagram.undefClass);
    }
}
