package blockchain.Miners;

import blockchain.Components.Block;

public class Miner extends Thread {
    private static volatile boolean blockIsMined;
    private String prefixString;
    private Block block;

    public Miner(String prefixString, Block block) {
        this.prefixString = prefixString;
        this.block = block;
    }

    public static void reset() {
        blockIsMined = false;
    }

    public static synchronized void setIsMined() {
        blockIsMined = true;
    }

    public static synchronized boolean isMined() {
        return blockIsMined;
    }

    @Override
    public void run() {
        long miningTime = System.currentTimeMillis();
        String hashOfBlock = block.getHashInput();
        block.setMinerId(Thread.currentThread().getName());
        // After this execution, we know that the block with the matching prefix has the right block
        while (!hashOfBlock.substring(0, prefixString.length()).equals(prefixString)) {
            if (Miner.isMined()) {
                return;
            }
            block.nextMagicNumber();
            hashOfBlock = block.getHashOfBlock();
        }
        Miner.setIsMined();
        miningTime = (System.currentTimeMillis() - miningTime) / 1000;
        block.setGeneratingTime(miningTime);
        return;
    }
}
