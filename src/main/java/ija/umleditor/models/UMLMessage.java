/**
 * @brief Declaration of UMLMessage class.
 * UMLClass represents abstract structure of message in sequence diagram.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file UMLMessage.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Declaration of UMLMessage class.
 */
public class UMLMessage extends Element implements IObserver {

    private UMLObject sender;
    private UMLObject receiver;
    private UMLOperation message;
    private MessageType messageType;

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * Sets type of message.
     * @param messageType New type that is instance of MessageType
     */
    public void setMessageType(MessageType messageType) {
        if (messageType == null)
            return;
        this.messageType = messageType;
    }

    /**
     * Gets message type
     * @return Instance of MessageType
     */
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
        setSender(sender);
        setReceiver(receiver);
        setMessage(message);
        messageType = MessageType.SYNC;
    }

    /**
     * Returns object that sent the message
     * @return Instance of UMLObject
     */
    public UMLObject getSender() {
        return sender;
    }

    /**
     * Returns object that received the message
     * @return Instance of UMLObject
     */
    public UMLObject getReceiver() {
        return receiver;
    }

    /**
     * Returns sent message
     * @return Instance of UMLOperation
     */
    public UMLOperation getMessage() {
        return message;
    }

    /**
     * Updates receiver, when null is passed, special undef object is set to prevent null pointer exception
     * @param receiver Instance of UMLObject
     */
    public void setReceiver(UMLObject receiver) {
        if (this.receiver != null && this.receiver != SequenceDiagram.undefObject)
            this.receiver.detach(this);
        this.receiver = receiver;
        if (this.receiver == null)
            this.receiver = SequenceDiagram.undefObject;
        else if (this.receiver != SequenceDiagram.undefObject)
            this.receiver.attach(this);
    }

    /**
     * Updates sender, when null is passed, special undef object is set to prevent null pointer exception
     * @param sender Instance of UMLObject
     */
    public void setSender(UMLObject sender) {
        if (this.sender != null && this.sender != SequenceDiagram.undefObject)
            this.sender.detach(this);
        this.sender = sender;
        if (this.sender == null)
            this.sender = SequenceDiagram.undefObject;
        else if (this.sender != SequenceDiagram.undefObject)
            this.sender.attach(this);
    }

    /**
     * Updates message, when null is passed, special undef operation is set to prevent null pointer exception
     * @param message Instance of UMLMessage
     */
    public void setMessage(UMLOperation message) {
        if (this.message != null && this.message != SequenceDiagram.undefOperation)
            this.message.detach(this);
        this.message = message;
        if (this.message == null)
            this.message = SequenceDiagram.undefOperation;
        else if (this.message != SequenceDiagram.undefOperation)
            this.message.attach(this);
    }

    /**
     * Class destructor.
     */
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
        object.put("messageType", messageType.toString());
        return object;
    }
}
