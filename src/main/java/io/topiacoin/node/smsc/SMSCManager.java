package io.topiacoin.node.smsc;

public interface SMSCManager {

    /**
     *
     * @param containerID
     * @param solution
     * @param callback
     */
    void submitProofSolution(String containerID, String solution, Callback callback);

    /**
     *
     * @param callback
     */
    void getContainers(Callback callback);

    /**
     *
     * @param containerID
     * @param callback
     */
    void getContainerInfo(String containerID, Callback callback);

    /**
     *
     * @param containerID
     * @param callback
     */
    void getNodesForContainer(String containerID, Callback callback);

    /**
     *
     * @param callback
     */
    void registerNode(Callback callback);

    /**
     *
     * @param callback
     */
    void unregisterNode(Callback callback);

    /**
     *
     * @param stakingAccount
     */
    void setStakingAccount(String stakingAccount);

    /**
     *
     * @param signingAccount
     */
    void setSigningAccount(String signingAccount);

    /**
     *
     * @param callback
     */
    void getAssignedDisputes(Callback callback);

    /**
     *
     * @param disputeID
     * @param ruling
     * @param callback
     */
    void sendDisputeResolution(String disputeID, String ruling, Callback callback);
    
    public static class Callback {
        
    }
}
