package io.topiacoin.node.micronetwork;

import io.topiacoin.node.model.MicroNetworkInfo;

import java.util.concurrent.Future;

/**
 * A Blockchain Manager is responsible for creating, destroying, starting, and stopping blockchains.  It handles
 * all of the underlying operations that are needed to create and manage a blockchain.
 */
public interface BlockchainManager {

    public void createBlockchain(String blockchainID) ;

    public Future syncBlockchain(String blockchainConnectionString, String blockchainID) ;

    public void startBlockchain(String blockchainID) ;

    public void stopBlockchain (String blockchainID) ;

    public void destroyBlockchain(String blockchainID) ;

    public MicroNetworkInfo getBlockchainInfo(String blockchainID);
}
