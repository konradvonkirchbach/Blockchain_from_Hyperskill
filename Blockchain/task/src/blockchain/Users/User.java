package blockchain.Users;

import blockchain.Components.BlockChain;
import blockchain.Messages.Message;
import blockchain.Transaction.Transaction;
import blockchain.utils.RandomMessageGenerator;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String KEY_GENERATOR_ALGORITHM = "RSA";
    private static final String MESSAGE_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final Random ran = new Random();

    private static BlockChain blockChain;
    private static int ID_COUNTER = 0;

    private final int userId;
    private PublicKey publicKey;
    private transient PrivateKey privateKey;
    private String name;
    private long VC = 100;

    public User(String name) throws NoSuchAlgorithmException {
        this.name = name;
        userId = ID_COUNTER++;

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_GENERATOR_ALGORITHM);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public long getVC() {
        return VC;
    }

    public void setVC(long VC) {
        this.VC = VC;
    }

    public void addVCAmount(long amount) {
        this.VC += amount;
    }

    public Transaction generateTransation() {
        if (this.VC > 0) {
            int randomRecipientId = Math.abs(ran.nextInt() % UserManager.numberOfUser());
            long randomNumberOfVC = Math.abs(ran.nextLong() % this.VC);
            Transaction transaction = new Transaction(userId, randomRecipientId, randomNumberOfVC);
            this.setVC(-randomNumberOfVC);

            Signature signature = null;
            try {
                signature = Signature.getInstance(MESSAGE_SIGNATURE_ALGORITHM);
                signature.initSign(this.privateKey);
                signature.update(transaction.getTransationSignatureString().getBytes());
                byte[] signedTransaction = signature.sign();

                transaction.setSignature(signedTransaction);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            }

            return transaction;
        }
        return null;
    }

    public boolean verifyTransaction(Transaction transaction) {
        Signature signature = null;
        try {
            signature = Signature.getInstance(MESSAGE_SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(transaction.getTransationSignatureString().getBytes());
            return signature.verify(transaction.getSignature());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setBlockChain(BlockChain bc) {
        blockChain = bc;
    }

    public Message generateMessage() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int id = blockChain.getMessageId();
        String message = RandomMessageGenerator.getRandomMessage();
        String signatureString = String.format("%s %s %d %d", message, this.name, userId, id);

        Signature signature = Signature.getInstance(MESSAGE_SIGNATURE_ALGORITHM);
        signature.initSign(this.privateKey);
        signature.update(signatureString.getBytes());
        byte[] messageSignature = signature.sign();

        signature.initVerify(this.publicKey);
        signature.update(signatureString.getBytes());
        boolean isValid = signature.verify(messageSignature);
        assert isValid == true;

        return new Message(message, userId, name, messageSignature, id);
    }

    public boolean verifyMessage(Message message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance(MESSAGE_SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(message.getVerificationString().getBytes());
        return signature.verify(message.getSignedData());
    }

    private void writeObject(ObjectOutputStream oos) throws Exception {
        oos.defaultWriteObject();
        byte[] encryptPrivateKey = privateKey.getEncoded();
        oos.writeObject(encryptPrivateKey);
    }

    private void readObject(ObjectInputStream ois) throws Exception {
        ois.defaultReadObject();
        byte[] encryptedPrivateKey = (byte[]) ois.readObject();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encryptedPrivateKey);
        KeyFactory kf = KeyFactory.getInstance(KEY_GENERATOR_ALGORITHM);
        privateKey = kf.generatePrivate(spec);
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return userId;
    }
}
