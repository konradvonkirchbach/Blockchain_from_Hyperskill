package blockchain.Miners;

import blockchain.Components.Block;
import blockchain.Messages.Message;
import blockchain.Transaction.Transaction;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MiningPool {
    private int numberOfMiners;
    private String hashPrefix;
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public MiningPool(int numberOfMiners) {
        this.numberOfMiners = numberOfMiners;
    }

    public MiningPool() {
        this.numberOfMiners = Runtime.getRuntime().availableProcessors();
    }

    public void setHashPrefix(String hashPrefix) {
        this.hashPrefix = hashPrefix;
    }

    public Block mineBlock(String previousHash, Integer blockId) throws InterruptedException {
        var blocks = new ArrayList<Block>();
        var miners = new ArrayList<Miner>();
        Miner.reset();
        for (int i = 0; i < numberOfMiners; i++) {
            Block block = new Block(previousHash, blockId);
            //block.setMessage(messages);
            block.setTransactions(transactions);
            Miner miner = new Miner(hashPrefix, block);
            miner.setName(String.format("%d", i));

            blocks.add(block);
            miners.add(miner);

            try {
                miner.start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        try {
            for (Miner miner : miners) {
                miner.join();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (Block block : blocks) {
            String hashOfBlock = block.getHashOfBlock();
            if (hashOfBlock.substring(0, hashPrefix.length()).equals(hashPrefix)) {
                return block;
            }
        }

        throw new RuntimeException("No block found!");
    }

    public void setMessages(List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                this.messages.add(message);
            }
        } else {
            this.messages = new ArrayList<>();
        }
    }

    public void setTransactions(List<Transaction> transactions) {
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                this.transactions.add(transaction);
            }
        }
    }
}
