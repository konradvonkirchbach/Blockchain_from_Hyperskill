package blockchain.Messages;

import blockchain.Users.User;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final int userID;
    private final String userName;
    private final byte[] signedData;
    private final int messageId;

    public Message(String message, int userID, String userName, byte[] signedData, int messageId) {
        this.message = message;
        this.userID = userID;
        this.userName = userName;
        this.signedData = signedData;
        this.messageId = messageId;
    }

    public Message(Message m) {
        this.message = m.message;
        this.userID = m.userID;
        this.userName = m.userName;
        this.signedData = m.signedData;
        this.messageId = m.messageId;
    }

    public String getMessage() {
        return String.format("%s: %s", this.userName, this.message);
    }

    public String getVerificationString() {
        return String.format("%s %s %d %d", message, userName, userID, messageId);
    }

    public byte[] getSignedData() {
        return signedData;
    }

    public int getMessageId() {
        return messageId;
    }

    public int getUserId() {
        return userID;
    }
}
