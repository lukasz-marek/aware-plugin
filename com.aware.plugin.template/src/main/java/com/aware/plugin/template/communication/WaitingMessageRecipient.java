package com.aware.plugin.template.communication;

import com.aware.plugin.template.communication.messages.Message;

/**
 * Created by lmarek on 19.12.2017.
 */

public interface WaitingMessageRecipient {

    void receiveMessage(Message message);

    String getRecipientName();
}
