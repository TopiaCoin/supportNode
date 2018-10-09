package io.topiacoin.node.micronetwork.providers;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.node.model.MicroNetworkInfo;

import java.util.concurrent.Future;

public class EOSBlockchainProvider implements BlockchainProvider {

    private EOSRPCAdapter _eosRpcAdapter;

    // -------- Public Methods --------

    @Override
    public void createBlockchain(String blockchainID) {

    }

    @Override
    public Future syncBlockchain(String blockchainConnectionString, String blockchainID) {
        return null;
    }

    @Override
    public void startBlockchain(String blockchainID) {

    }

    @Override
    public void stopBlockchain(String blockchainID) {

    }

    @Override
    public void destroyBlockchain(String blockchainID) {

    }

    @Override
    public MicroNetworkInfo getBlockchainInfo(String blockchainID) {
        return null;
    }

    // -------- Accessor Methods --------


    public void setEosRpcAdapter(EOSRPCAdapter eosRpcAdapter) {
        _eosRpcAdapter = eosRpcAdapter;
    }
}
