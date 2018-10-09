package io.topiacoin.node.mbm.impl;

import io.topiacoin.node.mbm.MicroNetworkBlockchainManager;
import io.topiacoin.node.model.BlockchainInfo;

import java.util.concurrent.Future;

public class EOSMicroNetworkBlockchainManager extends MicroNetworkBlockchainManager {

	@Override public String getVerificationValue(String blockchainID) {
		return null;
	}

	@Override public void createBlockchain(String blockchainID, boolean whatever) {

	}

	@Override public Future syncBlockchain(String blockchainConnectionString, String blockchainID, boolean notsure) {
		return null;
	}

	@Override public void startBlockchain(String blockchainID, boolean nop) {

	}

	@Override public void stopBlockchain(String blockchainID, boolean nop) {

	}

	@Override public void destroyBlockchain(String blockchainID, boolean nop) {

	}

	@Override public BlockchainInfo getBlockchainInfo(String blockchainID, boolean nop) {
		return null;
	}
}
