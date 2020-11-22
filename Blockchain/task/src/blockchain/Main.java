package blockchain;

import blockchain.Components.BlockChain;
import blockchain.Messages.MessageGenerator;
import blockchain.Transaction.TransactionsGenerator;
import blockchain.Users.UserManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) {
        try {
            BlockChain blockchain = new BlockChain();
            UserManager.initialize(blockchain);
            TransactionsGenerator transactionsGenerator = new TransactionsGenerator();
            TransactionsGenerator.setBlockChain(blockchain);
            transactionsGenerator.start();
            //MessageGenerator messageGenerator = new MessageGenerator();
            //MessageGenerator.setBlockchain(blockchain);
            //messageGenerator.start();
            blockchain.generateBlocks();
            for (int i = blockchain.getSize() - 15; i < blockchain.getSize(); i++) {
                System.out.println(blockchain.getBlockAt(i));
            }
            MessageGenerator.stopProducing();
            TransactionsGenerator.stopGenerating();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
