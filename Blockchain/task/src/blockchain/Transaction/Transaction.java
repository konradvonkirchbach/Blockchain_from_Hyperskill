package blockchain.Transaction;

import blockchain.Users.UserManager;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int senderId;
    private int receiverId;
    private long amountOfVC;
    private LocalDateTime dateTime;
    private byte[] signature;

    public Transaction(int senderId, int receiverId, long amountOfVC) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amountOfVC = amountOfVC;
        this.dateTime = LocalDateTime.now();
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public long getAmountOfVC() {
        return amountOfVC;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getTransationSignatureString() {
        return String.format("%d %d %d %s", senderId, receiverId, amountOfVC, dateTime);
    }

    @Override
    public String toString() {
        return String.format("%s sent %d VC to %s", Objects.requireNonNull(UserManager.getUserByID(senderId)).getName(),
                this.amountOfVC, Objects.requireNonNull(UserManager.getUserByID(receiverId)).getName());
    }
}
