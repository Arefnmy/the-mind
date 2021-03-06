package message;

public class Message {
    private String authToken;
    private final MessageType messageType;
    private final String message;

    //client side
    public Message(String authToken, MessageType messageType, String message) {
        this.authToken = authToken;
        this.messageType = messageType;
        this.message = message;
    }

    //server side
    public Message(MessageType messageType , String message){
        this.messageType = messageType;
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
