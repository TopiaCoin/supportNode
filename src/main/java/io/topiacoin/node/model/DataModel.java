package io.topiacoin.node.model;

import io.topiacoin.node.Configuration;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.exceptions.NotInitializedException;
import io.topiacoin.node.model.provider.DataModelProvider;
import io.topiacoin.node.model.provider.MemoryDataModelProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataModel {

	private static DataModel __instance;

	private DataModelProvider _provider;

	protected DataModel(Configuration _config) {
		if(_config != null) {
			if (_config.getConfigurationOption("model.storage.type", "memory").equalsIgnoreCase("memory")) {
				_provider = new MemoryDataModelProvider();
			} else {
				//_provider = new SQLiteDataModelProvider(_config);
			}
		} else {
			throw new NotInitializedException();
		}
	}

	public static synchronized DataModel getInstance() {
		if (__instance == null) {
			throw new NotInitializedException();
		}
		return __instance;
	}

	public static synchronized void initialize(Configuration config) {
		__instance = new DataModel(config);
	}

	public ContainerInfo createContainer(String id, long expirationDate) throws ContainerAlreadyExistsException {
		return _provider.createContainer(id, expirationDate);
	}

	public void updateContainer(ContainerInfo updatedContainer) throws NoSuchContainerException {
		_provider.updateContainer(updatedContainer);
	}

	public ContainerInfo getContainer(String id) throws NoSuchContainerException {
		return _provider.getContainer(id);
	}

	public DataItemInfo createDataItem(String id, String containerID, long size, String dataHash) throws DataItemAlreadyExistsException {
		return _provider.createDataItem(id, containerID, size, dataHash);
	}

	public void updateDataItem(DataItemInfo updatedDataItem) throws NoSuchDataItemException {
		_provider.updateDataItem(updatedDataItem);
	}

	public DataItemInfo getDataItem(String id) throws NoSuchDataItemException {
		return _provider.getDataItem(id);
	}

	public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException {
		return _provider.getDataItems(containerID);
	}

	public void removeDataItem(String id) throws NoSuchDataItemException {
		_provider.removeDataItem(id);
	}

	public void removeDataItems(String containerID) throws NoSuchContainerException {
		_provider.removeDataItems(containerID);
	}

	public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL) throws MicroNetworkAlreadyExistsException {
		return _provider.createMicroNetwork(id, containerID, path, state, rpcURL, p2pURL);
	}

	public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork) throws NoSuchMicroNetworkException {
		_provider.updateMicroNetwork(updatedMicroNetwork);
	}

	public MicroNetworkInfo getMicroNetwork(String id) throws NoSuchMicroNetworkException {
		return _provider.getMicroNetwork(id);
	}

	public void removeMicroNetwork(String id) throws NoSuchMicroNetworkException {
		_provider.removeMicroNetwork(id);
	}

	public void close() {
		_provider.close();
		__instance = null;
	}

}
