package com.aware.plugin.template.communication.messages;

/**
 * Created by lmarek on 19.12.2017.
 */

public abstract class Message {
    public enum MessageType {
        DEVICE_SELECTED,
        DISCONNECT
    }

    private final MessageType messageType;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public final MessageType getMessageType() {
        return this.messageType;
    }
}
