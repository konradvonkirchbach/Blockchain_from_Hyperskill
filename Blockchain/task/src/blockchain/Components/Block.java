package blockchain.Components;

import blockchain.Messages.Message;
import blockchain.Transaction.Transaction;
import blockchain.utils.StringUtil;

import java.io.Serializable;
import java.util.*;

public class Block implements Serializable {
    private static final long serialVersionUID = 1L;

    static final Random random = new Random();
    static Integer ID_COUNTER = 0;
    private Integer id;
    private String minerId;
    private String previousHash = "0";
    private String hashOfBlock;
    private Integer magicNumber;
    private long timeStamp;
    private long generatingTime;
    private String metaInfo = "";
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public Block(String previousHash, String hashPrefix) {
        this.id = ID_COUNTER;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.magicNumber = this.random.nextInt();
        String blockInfo = this.getHashInput();
        this.hashOfBlock= StringUtil.applySha256(blockInfo);

        while (!this.hashOfBlock.substring(0, hashPrefix.length()).equals(hashPrefix)) {
            this.magicNumber = this.random.nextInt();
            blockInfo = this.getHashInput();
            this.hashOfBlock= StringUtil.applySha256(blockInfo);
        }
        this.generatingTime = new Date().getTime() - this.timeStamp;
        ID_COUNTER++;
    }

    public Block(String previousHash, Integer blockId) {
        this.previousHash = previousHash;
        this.magicNumber = this.random.nextInt();
        this.id = blockId;
        this.timeStamp = new Date().getTime();
        this.generatingTime = -1;
    }

    // Private Methods
    private String convertMessageToString() {
        if (this.messages == null || this.messages.size() == 0) {
            return "no messages\n";
        } else {
            return messages.stream()
                    .map(x -> x.getMessage())
                    .reduce("\n", (x, y) -> String.format("%s%s\n", x, y));
        }
    }

    private String convertTransactionToString() {
        if (this.transactions == null || this.transactions.size() == 0) {
            return "No transactions\n";
        } else {
            return transactions.stream()
                    .map(x -> x.toString())
                    .reduce("\n", (x, y) -> String.format("%s%s\n", x, y));
        }
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    public void setMinerId(String minerId) {
        this.minerId = minerId;
    }

    public String getMinerId() {
        return minerId;
    }

    public static void setIdCounter(Integer NEW_VALUE) {
        ID_COUNTER = NEW_VALUE;
    }

    public String getHashInput() {
        return String.format("%d %s %d %d %s", id, previousHash, timeStamp, magicNumber, convertMessageToString());
    }

    public Integer getId() {
        return id;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public boolean hasBlockMessages() {
        return messages.size() > 0;
    }

    public String getHashOfBlock() {
        hashOfBlock = StringUtil.applySha256(this.getHashInput());
        return hashOfBlock;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setGeneratingTime(long generatingTime) {
        this.generatingTime = generatingTime;
    }

    public long getGeneratingTime() {
        return generatingTime;
    }

    public Integer getMagicNumber() {
        return magicNumber;
    }

    public void nextMagicNumber() {
        this.magicNumber = this.random.nextInt();
    }

    public void setMessage(List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                this.messages.add(message);
            }
        }
    }

    public int getBiggestMessageId(){
        if (messages != null) {
            return messages.stream()
                    .map(x -> x.getMessageId())
                    .reduce(Integer.MIN_VALUE, (x, y) -> Integer.max(x, y));
        } else {
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public String toString() {
        return String.format("Block:\nCreated by miner # %s\nminer%s gets 100 VC\nId: %d\nTimestamp: %d\nMagic number: %d\nHash of the previous block:\n%s" +
                "\nHash of the block:\n%s\nBlock data: %sBlock was generating for %d seconds\n%s\n",
                minerId, minerId, id, timeStamp, magicNumber, previousHash, hashOfBlock, convertTransactionToString(), generatingTime, metaInfo);
    }

    public void setTransactions(List<Transaction> transactions) {
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                this.transactions.add(transaction);
            }
        }
    }
}
