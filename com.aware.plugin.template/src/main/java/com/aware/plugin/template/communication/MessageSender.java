package com.aware.plugin.template.communication;

import android.os.AsyncTask;

import com.aware.plugin.template.communication.messages.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents a greatly simplified version of the actor model: https://en.wikipedia.org/wiki/Actor_model.
 * Actors of the class {{{@link MessageRecipient}}} receive messages {{{@link Message}}}.
 *
 * Created by lmarek on 19.12.2017.
 */

public class MessageSender {

    private static final Map<String, MessageRecipient> WAITING_RECIPIENTS = new ConcurrentHashMap<>();

    /**
     * Registers an actor in the system.
     * @param messageRecipient actor capable of receiving messages.
     */
    public static void waitForMessages(MessageRecipient messageRecipient) {
        WAITING_RECIPIENTS.put(messageRecipient.getRecipientName(), messageRecipient);
    }

    /**
     * Removes an actor from the system.
     * @param messageRecipient Actor to be removed.
     */
    public static void discardIncomingMessages(MessageRecipient messageRecipient) {
        WAITING_RECIPIENTS.remove(messageRecipient.getRecipientName());
    }

    /**
     * Sends message to an actor.
     * @param recipientName name of the actor
     * @param message message to be handled by the actor.
     */
    public static void sendMessageAsync(String recipientName, Message message) {
        final MessageRecipient recipient = WAITING_RECIPIENTS.get(recipientName);
        if (null != recipient) {
            AsyncTask.execute(() -> recipient.receiveMessage(message));
        }
    }


}
