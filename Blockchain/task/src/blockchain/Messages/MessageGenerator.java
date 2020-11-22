package blockchain.Messages;

import blockchain.Components.BlockChain;
import blockchain.Users.User;
import blockchain.Users.UserManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Random;

public class MessageGenerator extends Thread {
    private static Random ran = new Random();
    private static BlockChain blockchain;
    private static boolean isProducing = true;

    public static void setBlockchain(BlockChain bc) {
        blockchain = bc;
    }

    public static void stopProducing() {
        isProducing = false;
    }

    public MessageGenerator() {
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (isProducing) {
            long timeToSleep = Math.abs(ran.nextLong() % 150);
            try {
                Thread.sleep(timeToSleep);
                User user = UserManager.getRandomUser();
                blockchain.addMessage(user.generateMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }
}
