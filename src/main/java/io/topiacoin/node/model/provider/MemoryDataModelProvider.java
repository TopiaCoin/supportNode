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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MemoryDataModelProvider implements DataModelProvider {

	private Map<String, ContainerInfo> _containerMap = new HashMap<>();
	private Map<String, DataItemInfo> _dataItemMap = new HashMap<>();
	private Map<String, MicroNetworkInfo> _microNetworkMap = new HashMap<>();
	private Map<String, BlockchainInfo> _blockchainInfoMap = new HashMap<>();
	private Map<String, List<DataItemInfo>> _containerDataItemMap = new HashMap<>();

	@Override public ContainerInfo createContainer(String id, long expirationDate) throws ContainerAlreadyExistsException {
		if(_containerMap.containsKey(id)) {
			throw new ContainerAlreadyExistsException("Container with id " + id + " already exists");
		}
		ContainerInfo info = new ContainerInfo(id, expirationDate);
		_containerMap.put(id, info);
		_containerDataItemMap.put(id, new ArrayList<>());
		return info;
	}

	@Override public void updateContainer(ContainerInfo updatedContainer) throws NoSuchContainerException {
		if (!_containerMap.containsKey(updatedContainer.getId())) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		ContainerInfo containerToUpdate = new ContainerInfo(updatedContainer);
		_containerMap.put(containerToUpdate.getId(), containerToUpdate);
	}

	@Override public ContainerInfo getContainer(String id) throws NoSuchContainerException {
		if (!_containerMap.containsKey(id)) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		return new ContainerInfo(_containerMap.get(id));
	}

	@Override public DataItemInfo createDataItem(String id, String containerID, long size, String dataHash) throws DataItemAlreadyExistsException {
		if(_dataItemMap.containsKey(id)) {
			throw new DataItemAlreadyExistsException("DataItem with id " + id + " already exists");
		}
		DataItemInfo item = new DataItemInfo(id, containerID, size, dataHash);
		_dataItemMap.put(id, item);
		return item;
	}

	@Override public void updateDataItem(DataItemInfo updatedDataItem) throws NoSuchDataItemException {
		if (!_dataItemMap.containsKey(updatedDataItem.getId())) {
			throw new NoSuchDataItemException("No DataItem exists with the requested ID");
		}
		DataItemInfo dataItemToUpdate = new DataItemInfo(updatedDataItem);
		_dataItemMap.put(dataItemToUpdate.getId(), dataItemToUpdate);
	}

	@Override public DataItemInfo getDataItem(String id) throws NoSuchDataItemException {
		if (!_dataItemMap.containsKey(id)) {
			throw new NoSuchDataItemException("No DataItem exists with the requested ID");
		}
		return new DataItemInfo(_dataItemMap.get(id));
	}

	@Override public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException {
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

	@Override public void removeDataItem(String id) throws NoSuchDataItemException {
		DataItemInfo item = _dataItemMap.get(id);
		if (item == null) {
			throw new NoSuchDataItemException("No dataItem exists with the requested ID");
		}
		_containerDataItemMap.get(item.getContainerID()).remove(item);
		_dataItemMap.remove(id);
	}

	@Override public void removeDataItems(String containerID) throws NoSuchContainerException {
		List<DataItemInfo> items = _containerDataItemMap.get(containerID);
		if (items == null) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		Iterator<DataItemInfo> iterator = items.iterator();
		while (iterator.hasNext()) {
			_dataItemMap.remove(iterator.next().getId());
			iterator.remove();
		}
	}

	@Override public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL) throws MicroNetworkAlreadyExistsException {
		if(_microNetworkMap.containsKey(id)) {
			throw new MicroNetworkAlreadyExistsException("Micro Network with id " + id + " already exists");
		}
		MicroNetworkInfo info = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);
		_microNetworkMap.put(id, info);
		return info;
	}

	@Override public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork) throws NoSuchMicroNetworkException {
		if (!_microNetworkMap.containsKey(updatedMicroNetwork.getId())) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		MicroNetworkInfo containerToUpdate = new MicroNetworkInfo(updatedMicroNetwork);
		_microNetworkMap.put(containerToUpdate.getId(), containerToUpdate);
	}

	@Override public MicroNetworkInfo getMicroNetwork(String id) throws NoSuchMicroNetworkException {
		if (!_microNetworkMap.containsKey(id)) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		return new MicroNetworkInfo(_microNetworkMap.get(id));
	}

	@Override public void removeMicroNetwork(String id) throws NoSuchMicroNetworkException {
		MicroNetworkInfo item = _microNetworkMap.get(id);
		if (item == null) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		_microNetworkMap.remove(id);
	}

	@Override public BlockchainInfo createBlockchain(String id, String localPath) throws BlockchainAlreadyExistsException {
		if(_blockchainInfoMap.containsKey(id)) {
			throw new BlockchainAlreadyExistsException("Blockchain with id " + id + " already exists");
		}
		BlockchainInfo info = new BlockchainInfo(id, localPath);
		_blockchainInfoMap.put(id, info);
		return info;
	}

	@Override public void updateBlockchain(BlockchainInfo updatedBlockchainInfo) throws NoSuchBlockchainException {
		if (!_blockchainInfoMap.containsKey(updatedBlockchainInfo.getId())) {
			throw new NoSuchBlockchainException("No Blockchain exists with the requested ID");
		}
		BlockchainInfo blockchainToUpdate = new BlockchainInfo(updatedBlockchainInfo);
		_blockchainInfoMap.put(blockchainToUpdate.getId(), blockchainToUpdate);
	}

	@Override public BlockchainInfo getBlockchain(String id) throws NoSuchBlockchainException {
		if (!_blockchainInfoMap.containsKey(id)) {
			throw new NoSuchBlockchainException("No Blockchain exists with the requested ID");
		}
		return new BlockchainInfo(_blockchainInfoMap.get(id));
	}

	@Override public void removeBlockchain(String id) throws NoSuchBlockchainException {
		BlockchainInfo item = _blockchainInfoMap.get(id);
		if (item == null) {
			throw new NoSuchBlockchainException("No Blockchain exists with the requested ID");
		}
		_blockchainInfoMap.remove(id);
	}

	@Override public void close() {

	}
}
