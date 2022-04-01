package ija.umleditor.models;

import java.util.Objects;

public final class UMLRelation {

    private UMLClass src;
    private UMLClass dest;
    private RelationType relationType;
    private String srcMsg;
    private String destMsg;

    /**
     * Class {@code UMLRelation} constructor
     * Relation type is set as {@code RelationType.UNDEFINED} by default.
     * @param src Source class
     * @param dest Destination class
     */
    public UMLRelation(UMLClass src, UMLClass dest) {
        this.src = src;
        this.dest = dest;
        relationType = RelationType.UNDEFINED;
    }

    /**
     * Class {@code UMLRelation} constructor
     * @param src Source class
     * @param dest Destination class
     * @param type Relation type
     */
    public UMLRelation(UMLClass src, UMLClass dest, RelationType type) {
        this.src = src;
        this.dest = dest;
        relationType = type;
    }

    /**
     * Returns relation type
     * @return This relation's type
     */
    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
        if (relationType == RelationType.INHERITANCE) {
            src.addParentClass(dest);
        }
    }

    /**
     * Returns source class
     * @return Source class
     */
    public UMLClassifier getSrc() {
        return src;
    }

    /**
     * Returns destination class
     * @return destination class
     */
    public UMLClassifier getDest() {
        return dest;
    }

    /**
     * Returns source class message.
     * @return Source class's message
     */
    public String getSrcMsg() {
        return srcMsg;
    }

    /**
     * Updates source class message.
     * @return Source class's message
     */
    public void setSrcMsg(String srcMsg) {
        this.srcMsg = srcMsg;
    }

    /**
     * Returns destination class message.
     * @return destination class's message
     */
    public String getDestMsg() {
        return destMsg;
    }

    /**
     * Updates destination class message.
     * @return destination class's message
     */
    public void setDestMsg(String destMsg) {
        this.destMsg = destMsg;
    }

    /**
     * Adds this relation to both source and destination classes
     * @return true if relation as added successfully to both classes; false otherwise
     */
    public boolean setRelationDependency() {
        return src.addRelation(this) && dest.addRelation(this);
    }

    /**
     * Swap source and destination classes.
     * When {@code relationType} is {@code RelationType.INHERITANCE} method also
     * updates collection of parent classes in both classes.
     */
    public void swapDirection() {
        // swap inheritance
        if (relationType == RelationType.INHERITANCE) {
            src.removeParentClass(dest);
            dest.addParentClass(src);
        }
        UMLClass temp = src;
        src = dest;
        dest = temp;
    }

    /**
     * Removes this relation from both source and destination classes.
     */
    public void removeRelationDependency() {
        // remove inheritance
        if (relationType == RelationType.INHERITANCE) {
            src.removeParentClass(dest);
        }

        // remove relationship dependency
        src.removeRelation(this);
        dest.removeRelation(this);
    }

    /**
     * Compare if this relation is between two given classes.
     * Doesn't matter which is source and which is destination class.
     * @param cls1 First class
     * @param cls2 Second class
     * @return true if this relation is instance of bond between two classes; false otherwise
     */
    public boolean compareClassesInRelation(UMLClass cls1, UMLClass cls2) {
        return (cls1 == src && cls2 == dest) || (cls1 == dest && cls2 == src);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UMLRelation relation = (UMLRelation) o;
        return (Objects.equals(src, relation.src) && Objects.equals(dest, relation.dest)) ||
               (Objects.equals(src, relation.dest) && Objects.equals(dest, relation.src));
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest);
    }
}
