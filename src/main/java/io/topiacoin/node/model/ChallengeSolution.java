package io.topiacoin.node.model;

public class ChallengeSolution {

    private String verificationValue;
    private String transactionID;
    private long blockNumber;
    private String chunkHash;

    public ChallengeSolution(String verificationValue, String transactionID, long blockNumber, String chunkHash) {
        this.verificationValue = verificationValue;
        this.transactionID = transactionID;
        this.blockNumber = blockNumber;
        this.chunkHash = chunkHash;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public String getChunkHash() {
        return chunkHash;
    }
}
