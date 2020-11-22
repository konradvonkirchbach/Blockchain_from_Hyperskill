package blockchain.Components;

import blockchain.Messages.Message;
import blockchain.Miners.MiningPool;
import blockchain.Transaction.Transaction;
import blockchain.Users.User;
import blockchain.Users.UserManager;
import blockchain.utils.SerializationUtils;
import blockchain.utils.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockChain implements Serializable {
    private static final long serialVersionUID = 1L;

    private int messageId = 0;
    private ArrayList<Block> blockchain;
    private Integer N;
    private String hashPrefix;
    private String filename = "blockchain.data";
    private List<Message> messages;
    private List<Transaction> transactions;

    public BlockChain(int numberOfBocks, Integer N) throws IOException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        // Check if blockchain is already in a file
        File blockchainData = new File(this.filename);
        if (blockchainData.exists() && blockchainData.isFile()) {
            this.blockchain = (ArrayList<Block>) SerializationUtils.deserialize(this.filename);
            Block.setIdCounter(this.blockchain.get(this.blockchain.size() - 1).getId() + 1);
            messageId = blockchain.get(blockchain.size() - 1).getBiggestMessageId();
            if (!this.isValid()) {
                throw new RuntimeException("Invalid Blockchain");
            }
        } else {
            this.blockchain = new ArrayList<>();
        }

        this.setHashPrefix(N);

        if (numberOfBocks > 0 && this.blockchain.size() == 0) {
            Block block = new Block("0", this.hashPrefix);
            this.blockchain.add(block);
            SerializationUtils.serialize(this.blockchain, this.filename);
        }
        int blockChainSize = this.blockchain.size();
        for (int i = blockChainSize; i < blockChainSize + numberOfBocks; i++) {
            Block block = new Block(this.blockchain.get(i - 1).getHashOfBlock(), this.hashPrefix);
            this.blockchain.add(block);
            SerializationUtils.serialize(this.blockchain, this.filename);
        }
    }

    public void addMessage(Message message) {
        int userId = message.getUserId();
        User user = UserManager.getUserByID(userId);
        boolean isValid;
        try {
            isValid = user.verifyMessage(message);
            if (isValid) {
                messages.add(message);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            int senderId = transaction.getSenderId();
            User user = UserManager.getUserByID(senderId);
            boolean isValid;
            try {
                isValid = user.verifyTransaction(transaction);
                if (isValid) {
                    transactions.add(transaction);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BlockChain() throws IOException, ClassNotFoundException {
        // Check if blockchain is already in a file
        messages = new ArrayList<>();
        transactions = new ArrayList<>();
        N = 1;
        File blockchainData = new File(this.filename);
        if (blockchainData.exists() && blockchainData.isFile()) {
            this.blockchain = (ArrayList<Block>) SerializationUtils.deserialize(this.filename);
            messageId = blockchain.get(blockchain.size() - 1).getBiggestMessageId();
            Block.setIdCounter(this.blockchain.get(this.blockchain.size() - 1).getId() + 1);
            if (!this.isValid()) {
                throw new RuntimeException("Invalid Blockchain");
            }
        } else {
            this.blockchain = new ArrayList<>();
        }

        this.setHashPrefix(N);
    }

    public void setHashPrefix(Integer numberOfZeros) {
        char[] prefix = new char[numberOfZeros];
        Arrays.fill(prefix, '0');
        this.hashPrefix = new String(prefix);
        this.N = numberOfZeros;
    }

    public Block getBlockAt(int blockId) throws IndexOutOfBoundsException {
        try {
            return this.blockchain.get(blockId);
        } catch (RuntimeException e) {
            throw new ArrayIndexOutOfBoundsException(e.getMessage());
        }
    }

    public void addBlock() throws IOException {
        if (this.blockchain.size() > 0) {
            this.blockchain.add(
                    new Block(blockchain.get(blockchain.size() - 1).getHashOfBlock(), this.hashPrefix)
            );
            SerializationUtils.serialize(this.blockchain, this.filename);
        } else {
            this.blockchain.add(new Block("0", this.hashPrefix));
            SerializationUtils.serialize(this.blockchain, this.filename);
        }
    }

    public void generateBlocks() throws IOException, InterruptedException {
        generateNBlocks(15);
    }

    public void generateNBlocks(int n) throws InterruptedException, IOException {
        if (n < 0) {
            throw new RuntimeException("Invalid n. Should be greater or equal 0");
        }
        for (int i = 0; i < n; i++) {
            String previousHash = "0";
            Integer blockId = 0;
            if (blockchain.size() > 0) {
                previousHash = blockchain.get(blockchain.size() - 1).getHashOfBlock();
                blockId = blockchain.get(blockchain.size() - 1).getId() + 1;
            }

            MiningPool pool = new MiningPool();
            pool.setHashPrefix(hashPrefix);
            //pool.setMessages(messages);
            //messages.clear();
            pool.setTransactions(transactions);
            transactions.clear();
            Block block = pool.mineBlock(previousHash, blockId);

            long generatingTime = block.getGeneratingTime();
            if (generatingTime > 5) {
                N--;
                setHashPrefix(N);
                block.setMetaInfo(String.format("N was decreased to %d", N));
            } else if (generatingTime < 2) {
                N++;
                setHashPrefix(N);
                block.setMetaInfo(String.format("N was increased to %d", N));
            } else {
                block.setMetaInfo("N stays the same");
            }

            blockchain.add(block);
            int minerId = Integer.parseInt(block.getMinerId());
            int numberOfUsers = UserManager.numberOfUser();
            User user = UserManager.getUserByID( minerId % numberOfUsers );
            user.addVCAmount(100);
            SerializationUtils.serialize(blockchain, filename);
        }

    }

    public boolean isValid() {
        int lastBLockHighestMessageId = Integer.MIN_VALUE;
        for (int i = 0; i < this.blockchain.size(); i++) {
            Block block = this.blockchain.get(i);

            int currentBlockHighestMessageID = block.getBiggestMessageId();
            if (lastBLockHighestMessageId > currentBlockHighestMessageID && block.hasBlockMessages()) {
                return false;
            }

            lastBLockHighestMessageId = currentBlockHighestMessageID;

            String validationString = block.getHashInput();
            String validationHash = StringUtil.applySha256(validationString);
            if (!validationHash.equals(block.getHashOfBlock())) {
                return false;
            }
        }
        return true;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return this.blockchain.size();
    }

    @Override
    public String toString() {
        String blockchainString = "";
        for (Block block : blockchain) {
            blockchainString += block.toString() + "\n";
        }
        return blockchainString;
    }

    public int getMessageId() {
        return messageId++;
    }
}
