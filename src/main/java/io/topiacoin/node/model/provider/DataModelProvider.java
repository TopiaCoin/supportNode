package io.topiacoin.node.model.provider;

import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.model.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.model.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.model.exceptions.NoSuchContainerException;
import io.topiacoin.node.model.exceptions.NoSuchDataItemException;

import java.util.List;

public interface DataModelProvider {

	public ContainerInfo createContainer(String id, long expirationDate) throws ContainerAlreadyExistsException;

	public void updateContainer(ContainerInfo updatedContainer) throws NoSuchContainerException;

	public ContainerInfo getContainer(String id) throws NoSuchContainerException;

	public DataItemInfo createDataItem(String id, String containerID, long size, String dataHash) throws DataItemAlreadyExistsException;

	public void updateDataItem(DataItemInfo updatedDataItem) throws NoSuchDataItemException;

	public DataItemInfo getDataItem(String id) throws NoSuchContainerException;

	public List<DataItemInfo> getDataItems(String containerID) throws NoSuchContainerException;

	public void removeDataItem(String id);

	public void removeDataItems(String containerID);

	public MicroNetworkInfo createMicroNetwork(String id, String containerID, String path, MicroNetworkState state, String rpcURL, String p2pURL);

	public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork);

	public MicroNetworkInfo getMicroNetwork(String id);

	public void removeMicroNetwork(String id);

	public void close();
}
