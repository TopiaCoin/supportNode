package io.topiacoin.node.micronetwork;

public class BlockchainVerificationInformation {

    private String verificationValue;
    private long blockNumber;
    private String transactionID;

    public BlockchainVerificationInformation() {
    }

    public BlockchainVerificationInformation(String verificationValue, long blockNumber, String transactionID) {
        this.verificationValue = verificationValue;
        this.blockNumber = blockNumber;
        this.transactionID = transactionID;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public String getTransactionID() {
        return transactionID;
    }
}
