package io.topiacoin.node.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeSolution solution = (ChallengeSolution) o;
        return blockNumber == solution.blockNumber &&
                Objects.equals(verificationValue, solution.verificationValue) &&
                Objects.equals(transactionID, solution.transactionID) &&
                Objects.equals(chunkHash, solution.chunkHash);
    }

    @Override
    public int hashCode() {

        return Objects.hash(verificationValue, transactionID, blockNumber, chunkHash);
    }

    @Override
    public String toString() {
        return "ChallengeSolution{" +
                "verificationValue='" + verificationValue + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", blockNumber=" + blockNumber +
                ", chunkHash='" + chunkHash + '\'' +
                '}';
    }
}
