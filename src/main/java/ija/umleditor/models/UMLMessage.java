package ija.umleditor.models;

import org.json.JSONObject;

import java.util.Objects;

public class UMLMessage extends Element implements IObserver {

    private UMLObject sender;
    private UMLObject receiver;
    private UMLOperation message;

    /**
     * Class UMLMessage constructor
     * @param name      Message name
     * @param sender    Sender object
     * @param receiver  Receiver object
     * @param message   Instance of message
     */
    public UMLMessage(String name, UMLObject sender, UMLObject receiver, UMLOperation message) {
        super(name);
        // FIXME: connection between InstancePeriods, not UMLObjects
        this.sender = sender;
        this.receiver = receiver;
        // TODO:
    }

    /**
     * Returns object that sent message
     * @return
     */
    public UMLObject getSender() {
        return sender;
    }

    public UMLObject getReceiver() {
        return receiver;
    }

    public UMLOperation getMessage() {
        return message;
    }

    public void setReceiver(UMLObject receiver) {
        this.receiver = receiver;
    }

    public void setSender(UMLObject sender) {
        this.sender = sender;
    }

    public void setMessage(UMLOperation message) {
        this.message = message;
    }

    @Override
    public void update(String msg) {
        if(Objects.equals(msg, "DELETE")) {
            setMessage(SequenceDiagram.undefOperation);
        }
        else if (Objects.equals(msg, sender.getName())) {
            setSender(SequenceDiagram.undefObject);
        }
        else if (Objects.equals(msg, receiver.getName())) {
            setReceiver(SequenceDiagram.undefObject);
        }
    }

    @Override
    public JSONObject createJsonObject() {
        return null;
    }
}
