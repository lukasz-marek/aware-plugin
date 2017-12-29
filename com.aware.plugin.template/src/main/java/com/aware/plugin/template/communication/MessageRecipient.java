package com.aware.plugin.template.communication;

import com.aware.plugin.template.communication.messages.Message;

/**
 * Interface representing an actor in actor system: https://en.wikipedia.org/wiki/Actor_model
 * Classes implementing this interface should be safe to use in a multi-threaded environment.
 * Created by lmarek on 19.12.2017.
 */

public interface MessageRecipient {

    /**
     * Receives and handles a message. Implementation of this method should not require long computation.
     * This method is meant to share data between two actors, but message processing time is not limited by any factors.
     * It is allowed to send messages to other recipients from this method, but an actor cannot receive a new message if it is still processing a previous one.
     * Furthermore, sending messages from this method may lead to some performance issues - it is therefore advised to do it only when necessary.
     *
     * @param message
     */
    void receiveMessage(Message message);

    /**
     * Returns the name of the recipient.
     * Names should be unique in the application as they are used by the system to match recipient with an incoming message.
     */
    String getRecipientName();
}
