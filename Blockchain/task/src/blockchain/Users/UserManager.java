package blockchain.Users;

import blockchain.Components.Block;
import blockchain.Components.BlockChain;
import blockchain.utils.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserManager {
    private static List<User> users;

    private static final String USER_FILE_NAME = "user.data";
    private static Random ran = new Random();

    public static void initialize(BlockChain blockChain)  {
        User.setBlockChain(blockChain);

        File blockchainData = new File(USER_FILE_NAME);
        if (blockchainData.exists() && blockchainData.isFile()) {
            try {
                users = (List<User>) SerializationUtils.deserialize(USER_FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                users = List.of(
                        new User("Tom Riddle"),
                        new User("Darth Vader"),
                        new User("Lord Palpatine"),
                        new User("Harry"),
                        new User("Bro-bi-wan"),
                        new User("Long Schlong Silver")
                );
                SerializationUtils.serialize(users, USER_FILE_NAME);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static User getUserByID(int id) {
        for (User user : users) {
            if (user.getUserId() == id) {
                return user;
            }
        }
        return null;
    }

    public static int numberOfUser() {
        return users.size();
    }

    public static User getRandomUser() {
        return users.get(Math.abs(ran.nextInt() % users.size()));
    }
}
