package io.topiacoin.node.model;

import java.util.ArrayList;
import java.util.List;

public class Dispute {

    private String disputeID ;
    private String containerID ;
    private String disputedNodeID ;
    private String disputingAccount ;
    private String status ;
    private String chainURL ;
    private List<String> chunkIDs ;

    public Dispute() {
        chunkIDs = new ArrayList<>();
    }

    public Dispute(String disputeID,
                   String containerID,
                   String disputedNodeID,
                   String disputingAccount,
                   String status,
                   String chainURL,
                   List<String> chunkIDs) {
        this.disputeID = disputeID;
        this.containerID = containerID;
        this.disputedNodeID = disputedNodeID;
        this.disputingAccount = disputingAccount;
        this.status = status;
        this.chainURL = chainURL;
        this.chunkIDs = chunkIDs;
    }

    public String getDisputeID() {
        return disputeID;
    }

    public void setDisputeID(String disputeID) {
        this.disputeID = disputeID;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getDisputedNodeID() {
        return disputedNodeID;
    }

    public void setDisputedNodeID(String disputedNodeID) {
        this.disputedNodeID = disputedNodeID;
    }

    public String getDisputingAccount() {
        return disputingAccount;
    }

    public void setDisputingAccount(String disputingAccount) {
        this.disputingAccount = disputingAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChainURL() {
        return chainURL;
    }

    public void setChainURL(String chainURL) {
        this.chainURL = chainURL;
    }

    public List<String> getChunkIDs() {
        return chunkIDs;
    }

    public void setChunkIDs(List<String> chunkIDs) {
        this.chunkIDs = chunkIDs;
    }
}
