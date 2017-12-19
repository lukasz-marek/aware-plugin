package com.aware.plugin.template.communication;

import com.aware.plugin.template.communication.messages.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lmarek on 19.12.2017.
 */

public class MessageSender {

    private static final Map<String, WaitingMessageRecipient > WAITING_RECIPIENTS = new ConcurrentHashMap<>();

    public static void waitForMessages(WaitingMessageRecipient waitingMessageRecipient){
        WAITING_RECIPIENTS.put(waitingMessageRecipient.getRecipientName(), waitingMessageRecipient);
    }

    public static void discardIncomingMessages(WaitingMessageRecipient waitingMessageRecipient){
        WAITING_RECIPIENTS.remove(waitingMessageRecipient.getRecipientName());
    }

    public static void sendMessage(String recipientName, Message message){
        final WaitingMessageRecipient recipient = WAITING_RECIPIENTS.get(recipientName);
        if(null != recipient){
            recipient.receiveMessage(message);
        }
    }



}
