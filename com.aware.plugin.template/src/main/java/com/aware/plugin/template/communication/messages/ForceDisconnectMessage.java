package com.aware.plugin.template.communication.messages;

/**
 * Created by lmarek on 29.12.2017.
 */

public class ForceDisconnectMessage extends Message {

    public ForceDisconnectMessage( ) {
        super(MessageType.DISCONNECT);
    }
}
