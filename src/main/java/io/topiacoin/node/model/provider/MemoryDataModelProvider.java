package io.topiacoin.node.model.provider;

import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
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
	public ContainerInfo createContainer(String id, long expirationDate, Challenge challenge) throws ContainerAlreadyExistsException {
		if(_containerMap.containsKey(id)) {
			throw new ContainerAlreadyExistsException("Container with id " + id + " already exists");
		}
		ContainerInfo info = new ContainerInfo(id, expirationDate, challenge);
		_containerMap.put(id, info);
		_containerDataItemMap.put(id, new ArrayList<>());
		return info;
	}

	@Override
	public void updateContainer(ContainerInfo updatedContainer) throws NoSuchContainerException {
		if (!_containerMap.containsKey(updatedContainer.getId())) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		ContainerInfo containerToUpdate = new ContainerInfo(updatedContainer);
		_containerMap.put(containerToUpdate.getId(), containerToUpdate);
	}

	@Override
	public ContainerInfo getContainer(String id) throws NoSuchContainerException {
		if (!_containerMap.containsKey(id)) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		return new ContainerInfo(_containerMap.get(id));
	}

	@Override
	public void removeContainer(String id) throws NoSuchContainerException {
		if (!_containerMap.containsKey(id)) {
			throw new NoSuchContainerException("No container exists with the requested ID");
		}
		_containerMap.remove(id);
	}

	@Override
	public void addDataItemToContainer(String dataItemID, String containerID) throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException {
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
	public boolean removeDataItemFromContainer(String dataItemID, String containerID) throws NoSuchContainerException, NoSuchDataItemException {
		DataItemInfo dataItemInfo = _dataItemMap.get(dataItemID);
		if ( dataItemInfo == null ) {
			throw new NoSuchDataItemException("The specified data item does not exist" ) ;
		}
		List<DataItemInfo> dataItemList = _containerDataItemMap.get(containerID);
		if ( dataItemList == null ) {
			throw new NoSuchContainerException("The specified container does not exist (" + containerID + ")");
		}
		return dataItemList.remove(dataItemInfo) ;
	}

	@Override
	public boolean isDataItemInContainer(String dataItemID, String containerID) throws NoSuchContainerException {
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
	public DataItemInfo createDataItem(String id, long size, String dataHash) throws DataItemAlreadyExistsException {
		if(_dataItemMap.containsKey(id)) {
			throw new DataItemAlreadyExistsException("DataItem with id " + id + " already exists");
		}
		DataItemInfo item = new DataItemInfo(id, size, dataHash);
		_dataItemMap.put(id, item);
		return item;
	}

	@Override
	public void updateDataItem(DataItemInfo updatedDataItem) throws NoSuchDataItemException {
		if (!_dataItemMap.containsKey(updatedDataItem.getId())) {
			throw new NoSuchDataItemException("No DataItem exists with the requested ID");
		}
		DataItemInfo dataItemToUpdate = new DataItemInfo(updatedDataItem);
		_dataItemMap.put(dataItemToUpdate.getId(), dataItemToUpdate);
	}

	@Override
	public DataItemInfo getDataItem(String id) throws NoSuchDataItemException {
		if (!_dataItemMap.containsKey(id)) {
			throw new NoSuchDataItemException("No DataItem exists with the requested ID");
		}
		return new DataItemInfo(_dataItemMap.get(id));
	}

	@Override
	public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException {
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
	public void removeDataItem(String id) throws NoSuchDataItemException {
		DataItemInfo item = _dataItemMap.get(id);
		if (item == null) {
			throw new NoSuchDataItemException("No dataItem exists with the requested ID");
		}
//		_containerDataItemMap.get(item.getContainerID()).remove(item);
		_dataItemMap.remove(id);
	}

	@Override
	public void removeDataItems(String containerID) throws NoSuchContainerException {
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

	@Override
	public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL) throws MicroNetworkAlreadyExistsException {
		if(_microNetworkMap.containsKey(id)) {
			throw new MicroNetworkAlreadyExistsException("Micro Network with id " + id + " already exists");
		}
		MicroNetworkInfo info = new MicroNetworkInfo(id, containerID, path, state, rpcURL, p2pURL);
		_microNetworkMap.put(id, info);
		return info;
	}

	@Override
	public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork) throws NoSuchMicroNetworkException {
		if (!_microNetworkMap.containsKey(updatedMicroNetwork.getId())) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		MicroNetworkInfo containerToUpdate = new MicroNetworkInfo(updatedMicroNetwork);
		_microNetworkMap.put(containerToUpdate.getId(), containerToUpdate);
	}

	@Override
	public MicroNetworkInfo getMicroNetwork(String id) throws NoSuchMicroNetworkException {
		if (!_microNetworkMap.containsKey(id)) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		return new MicroNetworkInfo(_microNetworkMap.get(id));
	}

	@Override
	public void removeMicroNetwork(String id) throws NoSuchMicroNetworkException {
		MicroNetworkInfo item = _microNetworkMap.get(id);
		if (item == null) {
			throw new NoSuchMicroNetworkException("No Micro Network exists with the requested ID");
		}
		_microNetworkMap.remove(id);
	}

}
