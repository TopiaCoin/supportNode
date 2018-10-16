package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.exceptions.NodeConnectionInfoAlreadyExistsException;
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
import io.topiacoin.node.model.NodeConnectionInfo;
import io.topiacoin.node.utilities.RelationshipMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Profile("memory")
public class MemoryDataModelProvider implements DataModelProvider {

    private Log _log = LogFactory.getLog(this.getClass());

    private Map<String, ContainerInfo> _containerMap = new HashMap<>();
    private Map<String, DataItemInfo> _dataItemMap = new HashMap<>();
    private Map<String, MicroNetworkInfo> _microNetworkMap = new HashMap<>();
    private Map<String, BlockchainInfo> _blockchainInfoMap = new HashMap<>();
    private Map<String, NodeConnectionInfo> _nodeConnectionInfoMap = new HashMap<>();

    private RelationshipMap _containerDataItemRelationship = new RelationshipMap();

    // -------- Lifecycle Methods --------

    @PostConstruct
    @Override
    public void initialize() {
        _log.info("Initializing Memory Data Model Provider");
        _log.info("Initialized Memory Data Model Provider");
    }

    @PreDestroy
    @Override
    public void shutdown() {
        _log.info("Shutting Down Memory Data Model Provider");
        _log.info("Shut Down Memory Data Model Provider");
    }

    @Override
    public ContainerInfo createContainer(String containerID, long expirationDate, Challenge challenge)
            throws ContainerAlreadyExistsException {
        if (_containerMap.containsKey(containerID)) {
            throw new ContainerAlreadyExistsException("Container with id " + containerID + " already exists");
        }
        ContainerInfo info = new ContainerInfo(containerID, expirationDate, challenge);
        _containerMap.put(containerID, info);
        return info;
    }

    @Override
    public void updateContainer(ContainerInfo updatedContainer)
            throws NoSuchContainerException {
        if (!_containerMap.containsKey(updatedContainer.getId())) {
            throw new NoSuchContainerException("No container exists with the requested ID");
        }
        ContainerInfo containerToUpdate = new ContainerInfo(updatedContainer);
        _containerMap.put(containerToUpdate.getId(), containerToUpdate);
    }

    @Override
    public ContainerInfo getContainer(String containerID) {
        if (!_containerMap.containsKey(containerID)) {
            return null;
        }
        return new ContainerInfo(_containerMap.get(containerID));
    }

    @Override
    public boolean removeContainer(String containerID) {
        ContainerInfo containerInfo = _containerMap.remove(containerID);
        if (containerInfo == null) {
            return false;
        }
        _containerDataItemRelationship.removeAllRelationships(containerInfo);
        return true;
    }

    @Override
    public void addDataItemToContainer(String dataItemID, String containerID)
            throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException {
        DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
        if (dataItemInfo == null) {
            throw new NoSuchDataItemException("The specified data item does not exist");
        }
        ContainerInfo containerInfo = _containerMap.get(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        if (!_containerDataItemRelationship.addRelationship(containerInfo, dataItemInfo)) {
            throw new DataItemAlreadyExistsException("The specified data item is already in the specified container");
        }
    }

    @Override
    public boolean removeDataItemFromContainer(String dataItemID, String containerID)
            throws NoSuchContainerException {
        DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
        if (dataItemInfo == null) {
            return false;
        }
        ContainerInfo containerInfo = _containerMap.get(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        return _containerDataItemRelationship.removeRelationship(containerInfo, dataItemInfo);
    }

    @Override
    public boolean isDataItemInContainer(String dataItemID, String containerID)
            throws NoSuchContainerException {
        DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
        if (dataItemInfo == null) {
            return false;
        }
        ContainerInfo containerInfo = _containerMap.get(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified container does not exist");
        }
        return _containerDataItemRelationship.areRelated(containerInfo, dataItemInfo);
    }

    @Override
    public boolean isDataItemInAnyContainer(String dataItemID) {
        DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
        if (dataItemInfo == null) {
            return false;
        }

        return _containerDataItemRelationship.getRelationships(dataItemInfo).size() > 0;
    }

    @Override
    public DataItemInfo createDataItem(String dataItemID, long size, String dataHash)
            throws DataItemAlreadyExistsException {
        if (_dataItemMap.containsKey(dataItemID)) {
            throw new DataItemAlreadyExistsException("DataItem with id " + dataItemID + " already exists");
        }
        DataItemInfo item = new DataItemInfo(dataItemID, size, dataHash);
        _dataItemMap.put(dataItemID, item);
        return item;
    }

    @Override
    public void updateDataItem(DataItemInfo updatedDataItem)
            throws NoSuchDataItemException {
        if (!_dataItemMap.containsKey(updatedDataItem.getId())) {
            throw new NoSuchDataItemException("No DataItem exists with the requested ID");
        }
        DataItemInfo dataItemToUpdate = new DataItemInfo(updatedDataItem);
        _dataItemMap.put(dataItemToUpdate.getId(), dataItemToUpdate);
    }

    @Override
    public DataItemInfo getDataItem(String dataItemID) {
        if (!_dataItemMap.containsKey(dataItemID)) {
            return null;
        }
        return new DataItemInfo(_dataItemMap.get(dataItemID));
    }

    @Override
    public List<DataItemInfo> getDataItems(String containerID)
            throws NoSuchContainerException {
        ContainerInfo containerInfo = _containerMap.get(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("The specified Container does not exist");
        }
        Set<Object> items = _containerDataItemRelationship.getRelationships(containerInfo);

        List<DataItemInfo> retItems = new ArrayList<DataItemInfo>();

        Iterator<Object> iterator = items.iterator();
        while (iterator.hasNext()) {
            retItems.add(new DataItemInfo((DataItemInfo) iterator.next()));
        }

        return retItems;
    }

    @Override
    public boolean removeDataItem(String dataItemID) {
        DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
        _containerDataItemRelationship.removeAllRelationships(dataItemInfo);
        return (_dataItemMap.remove(dataItemID) != null);
    }

    @Override
    public boolean removeDataItems(String containerID)
            throws NoSuchContainerException {
        boolean removedItems = false;
        ContainerInfo containerInfo = _containerMap.get(containerID);
        if (containerInfo == null) {
            throw new NoSuchContainerException("No container exists with the requested ID");
        }
        Set<Object> items = _containerDataItemRelationship.getRelationships(containerInfo);
        Iterator<Object> iterator = items.iterator();
        while (iterator.hasNext()) {
            removedItems = true;
            _dataItemMap.remove(((DataItemInfo) iterator.next()).getId());
            iterator.remove();
        }

        return removedItems;
    }

    @Override
    public MicroNetworkInfo createMicroNetwork(String microNetworkID, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL)
            throws MicroNetworkAlreadyExistsException {
        if (_microNetworkMap.containsKey(microNetworkID)) {
            throw new MicroNetworkAlreadyExistsException("Micro Network with id " + microNetworkID + " already exists");
        }
        MicroNetworkInfo info = new MicroNetworkInfo(microNetworkID, containerID, path, state, rpcURL, p2pURL);
        _microNetworkMap.put(microNetworkID, info);
        return info;
    }

    @Override
    public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork)
            throws NoSuchMicroNetworkException {
        if (!_microNetworkMap.containsKey(updatedMicroNetwork.getId())) {
            throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
        }
        MicroNetworkInfo containerToUpdate = new MicroNetworkInfo(updatedMicroNetwork);
        _microNetworkMap.put(containerToUpdate.getId(), containerToUpdate);
    }

    @Override
    public MicroNetworkInfo getMicroNetwork(String microNetworkID) {
        if (!_microNetworkMap.containsKey(microNetworkID)) {
            return null;
        }
        return new MicroNetworkInfo(_microNetworkMap.get(microNetworkID));
    }

    @Override
    public boolean removeMicroNetwork(String microNetworkID) {
        return (_microNetworkMap.remove(microNetworkID) != null);
    }

    @Override
    public BlockchainInfo createBlockchain(String blockchainID, String localPath)
            throws BlockchainAlreadyExistsException {
        if (_blockchainInfoMap.containsKey(blockchainID)) {
            throw new BlockchainAlreadyExistsException("Blockchain with id " + blockchainID + " already exists");
        }
        BlockchainInfo info = new BlockchainInfo(blockchainID, localPath);
        _blockchainInfoMap.put(blockchainID, info);
        return info;
    }

    @Override
    public void updateBlockchain(BlockchainInfo updatedBlockchainInfo)
            throws NoSuchBlockchainException {
        if (!_blockchainInfoMap.containsKey(updatedBlockchainInfo.getId())) {
            throw new NoSuchBlockchainException("No Blockchain exists with the requested ID");
        }
        BlockchainInfo blockchainToUpdate = new BlockchainInfo(updatedBlockchainInfo);
        _blockchainInfoMap.put(blockchainToUpdate.getId(), blockchainToUpdate);
    }

    @Override
    public BlockchainInfo getBlockchain(String blockchainID) {
        if (!_blockchainInfoMap.containsKey(blockchainID)) {
            return null;
        }
        return new BlockchainInfo(_blockchainInfoMap.get(blockchainID));
    }

    @Override
    public boolean removeBlockchain(String blockchainID) {
        return (_blockchainInfoMap.remove(blockchainID) != null);
    }

    @Override
    public NodeConnectionInfo createNodeConnectionInfo(
            String containerID, String nodeID, String rpcURL, String p2pURL)
            throws NodeConnectionInfoAlreadyExistsException, NoSuchContainerException {

        String key = containerID + ":" + nodeID;
        if ( _nodeConnectionInfoMap.containsKey(key)) {
            throw new NodeConnectionInfoAlreadyExistsException("The node connection info or the specified container/node already exists");
        }
        if ( !_containerMap.containsKey(containerID)) {
            throw new NoSuchContainerException("The specified container does not exist");
        }

        NodeConnectionInfo info = new NodeConnectionInfo(containerID, nodeID, rpcURL, p2pURL);

        _nodeConnectionInfoMap.put(key, info) ;

        return new NodeConnectionInfo(info);
    }

    @Override
    public NodeConnectionInfo getNodeConnectionInfo(String containerID, String nodeID) {

        String key = containerID + ":" + nodeID;

        if ( !_nodeConnectionInfoMap.containsKey(key)) {
            return null;
        }

        return new NodeConnectionInfo(_nodeConnectionInfoMap.get(key));
    }

    @Override
    public void updateNodeConnectionInfo(NodeConnectionInfo info)
            throws NoSuchNodeException {

        String key = info.getContainerID()+ ":" + info.getNodeID();
        if ( !_nodeConnectionInfoMap.containsKey(key)) {
            throw new NoSuchNodeException("The specified Node Info does not exist");
        }

        NodeConnectionInfo updatedInfo = new NodeConnectionInfo(info);
        _nodeConnectionInfoMap.put(key, updatedInfo);
    }

    @Override
    public boolean removeNodeConnectionInfo(String containerID, String nodeID) {
        boolean removed = false ;
        String key = containerID + ":" + nodeID;
        if ( _nodeConnectionInfoMap.containsKey(key)) {
            _nodeConnectionInfoMap.remove(key) ;
            removed = true ;
        }

        return removed;
    }
}
