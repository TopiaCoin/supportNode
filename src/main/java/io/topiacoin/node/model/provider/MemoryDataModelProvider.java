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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
@Profile("memory")
public class MemoryDataModelProvider implements DataModelProvider {

	private Log _log = LogFactory.getLog(this.getClass());

	private Map<String, ContainerInfo> _containerMap = new HashMap<>();
	private Map<String, DataItemInfo> _dataItemMap = new HashMap<>();
	private Map<String, MicroNetworkInfo> _microNetworkMap = new HashMap<>();
	private Map<String, BlockchainInfo> _blockchainInfoMap = new HashMap<>();
	private Map<String, List<DataItemInfo>> _containerDataItemMap = new HashMap<>();

	// -------- Lifecycle Methods --------

	@PostConstruct
	@Override
	public void initialize() {
		_log.info ( "Initializing Memory Data Model Provider");
		_log.info ( "Initialized Memory Data Model Provider");
	}

	@PreDestroy
	@Override
	public void shutdown() {
		_log.info ( "Shutting Down Memory Data Model Provider");
		_log.info ( "Shut Down Memory Data Model Provider");
	}

	@Override
	public ContainerInfo createContainer(String containerID, long expirationDate, Challenge challenge)
			throws ContainerAlreadyExistsException {
		if(_containerMap.containsKey(containerID)) {
			throw new ContainerAlreadyExistsException("Container with id " + containerID + " already exists");
		}
		ContainerInfo info = new ContainerInfo(containerID, expirationDate, challenge);
		_containerMap.put(containerID, info);
		_containerDataItemMap.put(containerID, new ArrayList<>());
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
		if ( !_containerMap.containsKey(containerID)) {
			return null;
		}
		return new ContainerInfo(_containerMap.get(containerID));
	}

	@Override
	public boolean removeContainer(String containerID) {
		return (_containerMap.remove(containerID) != null);
	}

	@Override
	public void addDataItemToContainer(String dataItemID, String containerID)
			throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException {
		DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
		if ( dataItemInfo == null ) {
			throw new NoSuchDataItemException("The specified data item does not exist" ) ;
		}
		List<DataItemInfo> dataItemList = _containerDataItemMap.get(containerID);
		if ( dataItemList == null ) {
			throw new NoSuchContainerException("The specified container does not exist (" + containerID + ")");
		}
		if ( dataItemList.contains(dataItemInfo)) {
			throw new DataItemAlreadyExistsException("The specified data item is already in the specified container" ) ;
		}
		dataItemList.add(dataItemInfo);
	}

	@Override
	public boolean removeDataItemFromContainer(String dataItemID, String containerID)
			throws NoSuchContainerException {
		DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
		if ( dataItemInfo == null ) {
			return false;
		}
		List<DataItemInfo> dataItemList = _containerDataItemMap.get(containerID);
		if ( dataItemList == null ) {
			throw new NoSuchContainerException("The specified container does not exist (" + containerID + ")");
		}
		return dataItemList.remove(dataItemInfo) ;
	}

	@Override
	public boolean isDataItemInContainer(String dataItemID, String containerID)
			throws NoSuchContainerException {
		DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
		if ( dataItemInfo == null ) {
			return false ;
		}
		List<DataItemInfo> dataItemList = _containerDataItemMap.get(containerID);
		if ( dataItemList == null ) {
			throw new NoSuchContainerException("The specified container does not exist (" + containerID + ")");
		}
		return dataItemList.contains(dataItemInfo);
	}

	@Override
	public DataItemInfo createDataItem(String dataItemID, long size, String dataHash)
			throws DataItemAlreadyExistsException {
		if(_dataItemMap.containsKey(dataItemID)) {
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
		List<DataItemInfo> items = _containerDataItemMap.get(containerID);
		if (items == null) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}

		List<DataItemInfo> retItems = new ArrayList<DataItemInfo>();

		Iterator<DataItemInfo> iterator = items.iterator();
		while (iterator.hasNext()) {
			retItems.add(new DataItemInfo(iterator.next()));
		}

		return retItems;
	}

	@Override
	public boolean removeDataItem(String dataItemID) {
//		DataItemInfo item = _dataItemMap.get(dataItemID);
//		_containerDataItemMap.get(item.getContainerID()).remove(item);
		return (_dataItemMap.remove(dataItemID) != null);
	}

	@Override
	public boolean removeDataItems(String containerID)
			throws NoSuchContainerException {
		boolean removedItems = false ;
		List<DataItemInfo> items = _containerDataItemMap.get(containerID);
		if (items == null) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		Iterator<DataItemInfo> iterator = items.iterator();
		while (iterator.hasNext()) {
			removedItems = true ;
			_dataItemMap.remove(iterator.next().getId());
			iterator.remove();
		}

		return removedItems;
	}

	@Override
	public MicroNetworkInfo createMicroNetwork(String microNetworkID, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL)
			throws MicroNetworkAlreadyExistsException {
		if(_microNetworkMap.containsKey(microNetworkID)) {
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

	@Override public BlockchainInfo createBlockchain(String blockchainID, String localPath)
			throws BlockchainAlreadyExistsException {
		if(_blockchainInfoMap.containsKey(blockchainID)) {
			throw new BlockchainAlreadyExistsException("Blockchain with id " + blockchainID + " already exists");
		}
		BlockchainInfo info = new BlockchainInfo(blockchainID, localPath);
		_blockchainInfoMap.put(blockchainID, info);
		return info;
	}

	@Override public void updateBlockchain(BlockchainInfo updatedBlockchainInfo)
			throws NoSuchBlockchainException {
		if (!_blockchainInfoMap.containsKey(updatedBlockchainInfo.getId())) {
			throw new NoSuchBlockchainException("No Blockchain exists with the requested ID");
		}
		BlockchainInfo blockchainToUpdate = new BlockchainInfo(updatedBlockchainInfo);
		_blockchainInfoMap.put(blockchainToUpdate.getId(), blockchainToUpdate);
	}

	@Override public BlockchainInfo getBlockchain(String blockchainID) {
		if (!_blockchainInfoMap.containsKey(blockchainID)) {
			return null;
		}
		return new BlockchainInfo(_blockchainInfoMap.get(blockchainID));
	}

	@Override public boolean removeBlockchain(String blockchainID) {
		return (_blockchainInfoMap.remove(blockchainID) != null);
	}

}
