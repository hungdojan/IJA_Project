package ija.umleditor.models;

import org.json.JSONObject;

import java.util.Objects;

public class UMLMessage extends Element implements IObserver {

    private UMLObject sender;
    private UMLObject receiver;
    private UMLOperation message;
    private MessageType messageType;

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    public void setMessageType(MessageType messageType) {
        if (messageType == null)
            return;
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

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
        setSender(sender);
        setReceiver(receiver);
        setMessage(message);
        messageType = MessageType.SYNC;
        // this.sender = sender;
        // this.receiver = receiver;
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
        if (this.receiver != null && this.receiver != SequenceDiagram.undefObject)
            this.receiver.detach(this);
        this.receiver = receiver;
        if (this.receiver == null)
            this.receiver = SequenceDiagram.undefObject;
        else if (this.receiver != SequenceDiagram.undefObject)
            this.receiver.attach(this);
    }

    public void setSender(UMLObject sender) {
        if (this.sender != null && this.sender != SequenceDiagram.undefObject)
            this.sender.detach(this);
        this.sender = sender;
        if (this.sender == null)
            this.sender = SequenceDiagram.undefObject;
        else if (this.sender != SequenceDiagram.undefObject)
            this.sender.attach(this);
    }

    public void setMessage(UMLOperation message) {
        // TODO: message is callable from receiver
        if (this.message != null && this.message != SequenceDiagram.undefOperation)
            this.message.detach(this);
        this.message = message;
        if (this.message == null)
            this.message = SequenceDiagram.undefOperation;
        else if (this.message != SequenceDiagram.undefOperation)
            this.message.attach(this);
    }

    public void close() {
        if (sender != SequenceDiagram.undefObject) {
            sender.detach(this);
        }
        if (receiver != SequenceDiagram.undefObject) {
            receiver.detach(this);
        }
        if (message != SequenceDiagram.undefOperation) {
            message.detach(this);
        }
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
        JSONObject object = new JSONObject();
        object.put("_class", "UMLMessage");
        object.put("sender", sender.getName());
        object.put("receiver", receiver.getName());
        object.put("name", nameProperty.getValue());
        object.put("message", message.getName());
        return object;
    }
}
