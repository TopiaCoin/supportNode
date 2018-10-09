package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.model.BlockchainInfo;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;

import java.util.List;

public interface DataModelProvider {

	public ContainerInfo createContainer(String id, long expirationDate) throws ContainerAlreadyExistsException;

	public void updateContainer(ContainerInfo updatedContainer) throws NoSuchContainerException;

	public ContainerInfo getContainer(String id) throws NoSuchContainerException;

	public DataItemInfo createDataItem(String id, String containerID, long size, String dataHash) throws DataItemAlreadyExistsException;

	public void updateDataItem(DataItemInfo updatedDataItem) throws NoSuchDataItemException;

	public DataItemInfo getDataItem(String id) throws NoSuchDataItemException;

	public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException;

	public void removeDataItem(String id) throws NoSuchDataItemException;

	public void removeDataItems(String containerID) throws NoSuchContainerException;

	public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL) throws MicroNetworkAlreadyExistsException;

	public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork) throws NoSuchMicroNetworkException;

	public MicroNetworkInfo getMicroNetwork(String id) throws NoSuchMicroNetworkException;

	public void removeMicroNetwork(String id) throws NoSuchMicroNetworkException;

	public BlockchainInfo createBlockchain(String id, String localPath) throws BlockchainAlreadyExistsException;

	public void updateBlockchain(BlockchainInfo updatedMicroNetwork) throws NoSuchBlockchainException;

	public BlockchainInfo getBlockchain(String id) throws NoSuchBlockchainException;

	public void removeBlockchain(String id) throws NoSuchBlockchainException;

	public void close();
}
