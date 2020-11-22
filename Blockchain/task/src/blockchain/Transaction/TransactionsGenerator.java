package blockchain.Transaction;

import blockchain.Components.BlockChain;
import blockchain.Users.User;
import blockchain.Users.UserManager;

import java.util.Random;

public class TransactionsGenerator extends Thread {
    private static Random ran = new Random();
    private static BlockChain blockChain;
    private static boolean isGenerating = true;

    public static void setBlockChain(BlockChain bc) {
        blockChain = bc;
    }

    public static void stopGenerating() {
        isGenerating = false;
    }

    public TransactionsGenerator() {
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (isGenerating) {
            long timeToSleep = Math.abs(ran.nextLong() % 150);
            try {
                Thread.sleep(timeToSleep);
                User user = UserManager.getRandomUser();
                blockChain.addTransaction(user.generateTransation());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
