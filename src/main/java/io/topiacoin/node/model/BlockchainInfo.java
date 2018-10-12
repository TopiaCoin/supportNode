package io.topiacoin.node.model;

import java.util.Objects;

public class BlockchainInfo {
	private String _id;
	private String _localPath;
	private String _status;

	public BlockchainInfo() {
		_status = "NEW";
	}

	public BlockchainInfo(String id, String localPath) {
		_id = id;
		_localPath = localPath;
		_status = "NEW";
	}

	public BlockchainInfo(BlockchainInfo info) {
		_id = info._id;
		_localPath = info._localPath;
		_status = info._status;
	}

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getLocalPath() {
		return _localPath;
	}

	public void setLocalPath(String _localPath) {
		this._localPath = _localPath;
	}

	public String getStatus() {
		return _status;
	}

	public void setStatus(String _status) {
		this._status = _status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockchainInfo that = (BlockchainInfo) o;
		return Objects.equals(_id, that._id) &&
				Objects.equals(_localPath, that._localPath) &&
				Objects.equals(_status, that._status);
	}

	@Override
	public int hashCode() {

		return Objects.hash(_id, _localPath, _status);
	}
}
