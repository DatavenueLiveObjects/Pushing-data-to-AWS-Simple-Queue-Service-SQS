package com.orange.lo.sample.sqs.liveobjects;

public class LoMessage {

    private final int messageId;
    private final String message;

    public LoMessage(int messageId, String message) {
        this.messageId = messageId;
        this.message = message;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
