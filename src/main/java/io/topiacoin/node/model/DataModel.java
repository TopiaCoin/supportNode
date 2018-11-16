package io.topiacoin.node.model;

import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.exceptions.NodeConnectionInfoAlreadyExistsException;
import io.topiacoin.node.model.provider.DataModelProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class DataModel {

    private Log _log = LogFactory.getLog(this.getClass());

    @Autowired
    private DataModelProvider _provider;

    public DataModel() {
        // NOOP
    }

    // -------- Lifecycle Methods --------

    @PostConstruct
    public void initialize() {
        _log.info("Initializing Data Model");
        _log.info("Initialized Data Model");
    }

    @PreDestroy
    public void shutdown() {
        _log.info("Shutting Down Data Model");
        _log.info("Shut Down Data Model");
    }


    // -------- Data Model Methods --------

    public ContainerInfo createContainer(String id, long expirationDate, Challenge challenge)
            throws ContainerAlreadyExistsException {
        return _provider.createContainer(id, expirationDate, challenge);
    }

    public void updateContainer(ContainerInfo updatedContainer)
            throws NoSuchContainerException {
        _provider.updateContainer(updatedContainer);
    }

    public ContainerInfo getContainer(String id) {
        return _provider.getContainer(id);
    }

    public boolean removeContainer(String id) {
        return _provider.removeContainer(id);
    }

    public void addDataItemToContainer(String dataItemID, String containerID)
            throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException {
        _provider.addDataItemToContainer(dataItemID, containerID);
    }

    public boolean removeDataItemFromContainer(String dataItemID, String containerID)
            throws NoSuchContainerException {
        return _provider.removeDataItemFromContainer(dataItemID, containerID);
    }

    public boolean isDataItemInContainer(String dataItemID, String containerID)
            throws NoSuchContainerException {
        return _provider.isDataItemInContainer(dataItemID, containerID);
    }

    public boolean isDataItemInAnyContainer(String dataID) {
        return _provider.isDataItemInAnyContainer(dataID);
    }

    public DataItemInfo createDataItem(String id, long size, String dataHash)
            throws DataItemAlreadyExistsException {
        return _provider.createDataItem(id, size, dataHash);
    }

    public void updateDataItem(DataItemInfo updatedDataItem)
            throws NoSuchDataItemException {
        _provider.updateDataItem(updatedDataItem);
    }

    public DataItemInfo getDataItem(String id) {
        return _provider.getDataItem(id);
    }

    public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException {
        return _provider.getDataItems(containerID);
    }

    public boolean removeDataItem(String id) {
        return _provider.removeDataItem(id);
    }

    public boolean removeDataItems(String containerID)
            throws NoSuchContainerException {
        return _provider.removeDataItems(containerID);
    }

    public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL)
            throws MicroNetworkAlreadyExistsException {
        return _provider.createMicroNetwork(id, containerID, path, state, rpcURL, p2pURL);
    }

    public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork)
            throws NoSuchMicroNetworkException {
        _provider.updateMicroNetwork(updatedMicroNetwork);
    }

    public MicroNetworkInfo getMicroNetwork(String id) {
        return _provider.getMicroNetwork(id);
    }

    public boolean removeMicroNetwork(String id) {
        return _provider.removeMicroNetwork(id);
    }

    public BlockchainInfo createBlockchain(String id, String localPath)
            throws BlockchainAlreadyExistsException {
        return _provider.createBlockchain(id, localPath);
    }

    public void updateBlockchain(BlockchainInfo updatedBlockchainInfo)
            throws NoSuchBlockchainException {
        _provider.updateBlockchain(updatedBlockchainInfo);
    }

    public BlockchainInfo getBlockchain(String id) {
        return _provider.getBlockchain(id);
    }

    public boolean removeBlockchain(String id) {
        return _provider.removeBlockchain(id);
    }

    public NodeConnectionInfo createNodeConnectionInfo(String nodeID, String nodeURL)
            throws NodeConnectionInfoAlreadyExistsException, NoSuchContainerException {
        return _provider.createNodeConnectionInfo(nodeID, nodeURL);
    }

    public NodeConnectionInfo getNodeConnectionInfo(String nodeID) {
        return _provider.getNodeConnectionInfo(nodeID);
    }

    public void updateNodeConnectionInfo(NodeConnectionInfo info)
            throws NoSuchNodeException {
        _provider.updateNodeConnectionInfo(info);
    }

    public boolean removeNodeConnectionInfo(String nodeID) {
        return _provider.removeNodeConnectionInfo(nodeID);
    }

    // -------- Accessor Methods --------


    public void setProvider(DataModelProvider provider) {
        _provider = provider;
    }

}
