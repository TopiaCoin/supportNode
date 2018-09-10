package io.topiacoin.node.model;

import java.util.Objects;

public class MicroNetworkInfo {
	private String id;
	private String containerID;
	private String path;
	private MicroNetworkState state;
	private String rpcURL;
	private String p2pURL;

	public MicroNetworkInfo() {

	}
	public MicroNetworkInfo(String id, String containerId, String path, MicroNetworkState state, String rpcURL, String p2pURL) {
		this.id = id;
		this.containerID = containerId;
		this.path = path;
		this.state = state;
		this.rpcURL = rpcURL;
		this.p2pURL = p2pURL;
	}

	public MicroNetworkInfo(MicroNetworkInfo info) {
		this.id = info.id;
		this.containerID = info.containerID;
		this.path = info.path;
		this.state = info.state;
		this.rpcURL = info.rpcURL;
		this.p2pURL = info.p2pURL;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContainerID() {
		return containerID;
	}

	public void setContainerID(String containerID) {
		this.containerID = containerID;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public MicroNetworkState getState() {
		return state;
	}

	public void setState(MicroNetworkState state) {
		this.state = state;
	}

	public String getRpcURL() {
		return rpcURL;
	}

	public void setRpcURL(String rpcURL) {
		this.rpcURL = rpcURL;
	}

	public String getP2pURL() {
		return p2pURL;
	}

	public void setP2pURL(String p2pURL) {
		this.p2pURL = p2pURL;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MicroNetworkInfo that = (MicroNetworkInfo) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(containerID, that.containerID) &&
				Objects.equals(path, that.path) &&
				Objects.equals(state, that.state) &&
				Objects.equals(rpcURL, that.rpcURL) &&
				Objects.equals(p2pURL, that.p2pURL);
	}

	@Override public int hashCode() {

		return Objects.hash(id, containerID, path, state, rpcURL, p2pURL);
	}
}
