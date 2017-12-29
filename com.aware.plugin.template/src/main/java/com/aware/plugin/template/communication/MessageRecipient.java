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
     * This method is only meant to share data between two actors. Operations that require long time to complete should be delegated to other threads.
     *
     * @param message
     */
    void receiveMessage(Message message);

    /**
     * Returns the name of the recipient.
     * Names should be unique as they are used by the system to match recipient with message.
     */
    String getRecipientName();
}
