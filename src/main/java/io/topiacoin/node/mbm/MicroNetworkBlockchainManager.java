package io.topiacoin.node.mbm;

import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.CouldNotConnectToBlockchainException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.model.BlockchainInfo;
import io.topiacoin.node.model.DataModel;

import java.util.List;
import java.util.concurrent.Future;

public abstract class MicroNetworkBlockchainManager {

	DataModel _dataModel;

	//Creates a blockchain with the given blockchainID. This blockchain will be created on disk so that it can be started later. Throws an exception if the blockchainID is invalid or not unique, or if the Container creation hasn't been greenlit by the SMSC
	void createBlockchain(String blockchainID) throws BlockchainAlreadyExistsException {
		//Check if a Blockchain with the given blockchainID exists already - if it does, throw a BlockchainAlreadyExistsException
		try {
			_dataModel.getBlockchain(blockchainID);
			throw new BlockchainAlreadyExistsException("Blockchain with id " + blockchainID + " already exists");
		} catch (NoSuchBlockchainException e) {
			//Ok good
		}
		//Add BlockchainInfo to the Data Model with a status flag indicating it's being created
		//Actually create the blockchain using the implementation
		//Start the blockchain(?)
		//Update the Data Model to indicate that creation is complete
	}

	//Attempts to connect to another Node (via the blockchainConnectionString) and synchronize the blockchain with the specified blockchainID. An exception will be thrown if parameters are invalid, if the SMSC doesn't indicate that either we or the target node should be synchronizing the specified Blockchain, or if the target Node indicates an issue
	Future syncBlockchain(String blockchainConnectionString, String blockchainID) throws BlockchainAlreadyExistsException, CouldNotConnectToBlockchainException {
		//Check if the Blockchain already exists and is already synced. If so, I suppose throw a BlockchainAlreadyExistsException
		//Attempt to connect using the blockchainConnectionString. If it fails, throw a CouldNotConnectToBlockchainException
		//Once connected, update Data Model to indicate syncing
		//Sync
		//Once done syncing, update Data Model to indicate that syncing is done

		//Do we need this Future?
		return null;
	}

	//Starts a blockchain that's on disk but isn't running. Throws an exception if the Blockchain doesn't exist, is already running, or if the blockchain cannot be started
	void startBlockchain(String blockchainID) throws NoSuchBlockchainException {
		//Check if the Blockchain exists. If not, throw NoSuchBlockchainException
		//Update the status in the Data Model to indicate that it is starting if it isn't already running
		//Start the blockchain if it isn't already running
		//Update the status in the Data Model to indicate that it has started if needed
	}

	//Stops a running blockchain. Throws an exception if the Blockchain isn't running
	void stopBlockchain(String blockchainID) throws NoSuchBlockchainException {
		//Check if the Blockchain exists. If not, throw NoSuchBlockchainException
		//Update the status in the Data Model to indicate that it is stopping if it isn't already stopped
		//Stop the blockchain if it is running
		//Update the status in the Data Model to indicate that it has stopped if needed
	}

	//Deletes a non-running blockchain from disk. Throws an exception if the Blockchain is running, or if the Blockchain doesn't exist
	void destroyBlockchain(String blockchainID) {
		//Check if the Blockchain exists. If not, throw NoSuchBlockchainException
		//stopBlockchain(blockchainID);
		//Update the status in the Data Model to indicate that it is being destroyed if it isn't already
		//Destroy the blockchain
		//Update the status in the Data Model to indicate that it has been destroyed...? Right?
	}

	//Gets the latest verification value out of the blockchain for proof-of-hosting purposes. Throws an exception if the Blockchain isn't running or if a Verification Value cannot be ascertained
	public abstract String getVerificationValue(String blockchainID);

	//Returns a list of blockchainIDs of currently-running Blockchains
	public List<String> listRunningBlockchains() {
		//Somebody has a list of which Blockchains are running...so...return it?
		return null;
	}

	//Returns a list of blockchainIDs of stopped Blockchains
	public List<String> listStoppedBlockchains() {
		//This is a weird function, do we really need this?
		//Well, grab all the Blockchains listed in the DataModel that aren't marked as Starting or Running
		return null;
	}

	//Returns info about a running blockchain, to include, at minimum, duration and remaining lifetime of the blockchain, as well as the RPC URL and the P2P URL. Throws an exception if the Blockchain isn't running
	public BlockchainInfo getBlockchainInfo(String blockchainID) {
		//Check if the Blockchain exists. If not, throw NoSuchBlockchainException
		//I assume we just grab it out of the DataModel and return it.
		return null;
	}

	//Creates a blockchain with the given blockchainID. This blockchain will be created on disk so that it can be started later. Throws an exception if the blockchainID is invalid or not unique, or if the Container creation hasn't been greenlit by the SMSC
	public abstract void createBlockchain(String blockchainID, boolean whatever);

	//Attempts to connect to another Node (via the blockchainConnectionString) and synchronize the blockchain with the specified blockchainID. An exception will be thrown if parameters are invalid, if the SMSC doesn't indicate that either we or the target node should be synchronizing the specified Blockchain, or if the target Node indicates an issue
	public abstract Future syncBlockchain(String blockchainConnectionString, String blockchainID, boolean notsure);

	//Starts a blockchain that's on disk but isn't running. Throws an exception if the Blockchain doesn't exist, is already running, or if the blockchain cannot be started
	public abstract void startBlockchain(String blockchainID, boolean nop);

	//Stops a running blockchain. Throws an exception if the Blockchain isn't running
	public abstract void stopBlockchain(String blockchainID, boolean nop);

	//Deletes a non-running blockchain from disk. Throws an exception if the Blockchain is running, or if the Blockchain doesn't exist
	public abstract void destroyBlockchain(String blockchainID, boolean nop);

	//Returns info about a running blockchain, to include, at minimum, duration and remaining lifetime of the blockchain, as well as the RPC URL and the P2P URL. Throws an exception if the Blockchain isn't running
	public abstract BlockchainInfo getBlockchainInfo(String blockchainID, boolean nop);
}
