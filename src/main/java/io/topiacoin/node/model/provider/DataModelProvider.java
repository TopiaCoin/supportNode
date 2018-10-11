package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.model.BlockchainInfo;
import io.topiacoin.node.model.Challenge;
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

    void initialize();

    void shutdown();

    ContainerInfo createContainer(String containerID, long expirationDate, Challenge challenge)
            throws ContainerAlreadyExistsException;

    void updateContainer(ContainerInfo updatedContainer)
            throws NoSuchContainerException;

    ContainerInfo getContainer(String containerID);

    boolean removeContainer(String containerID);

    void addDataItemToContainer(String dataItemID, String containerID)
            throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException;

    boolean removeDataItemFromContainer(String dataItemID, String containerID)
            throws NoSuchContainerException;

    boolean isDataItemInContainer(String dataItemID, String containerID)
            throws NoSuchContainerException;

    boolean isDataItemInAnyContainer(String dataID);

    DataItemInfo createDataItem(String dataItemID, long size, String dataHash)
            throws DataItemAlreadyExistsException;

    void updateDataItem(DataItemInfo updatedDataItem)
            throws NoSuchDataItemException;

    DataItemInfo getDataItem(String dataItemID);

    List<DataItemInfo> getDataItems(String containerID)
            throws NoSuchContainerException;

    boolean removeDataItem(String dataItemID);

    boolean removeDataItems(String containerID)
            throws NoSuchContainerException;

    MicroNetworkInfo createMicroNetwork(String microNetworkID, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL)
            throws MicroNetworkAlreadyExistsException;

    void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork)
            throws NoSuchMicroNetworkException;

    MicroNetworkInfo getMicroNetwork(String microNetworkID);

    boolean removeMicroNetwork(String microNetworkID);

    BlockchainInfo createBlockchain(String blockchainID, String localPath)
            throws BlockchainAlreadyExistsException;

    void updateBlockchain(BlockchainInfo updatedMicroNetwork)
            throws NoSuchBlockchainException;

    BlockchainInfo getBlockchain(String blockchainID);

    boolean removeBlockchain(String blockchainID);
}
