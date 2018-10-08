package io.topiacoin.node.model;

import java.util.Objects;

public class DataItemInfo {
	private String id;
	private long size;
	private String dataHash;

	public DataItemInfo() {

	}
	public DataItemInfo(String id, long size, String dataHash) {
		this.id = id;
		this.size = size;
		this.dataHash = dataHash;
	}

	public DataItemInfo(DataItemInfo item) {
		this.id = item.id;
		this.size = item.size;
		this.dataHash = item.dataHash;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getDataHash() {
		return dataHash;
	}

	public void setDataHash(String dataHash) {
		this.dataHash = dataHash;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataItemInfo that = (DataItemInfo) o;
		return size == that.size &&
				Objects.equals(id, that.id) &&
				Objects.equals(dataHash, that.dataHash);
	}

	@Override public int hashCode() {

		return Objects.hash(id, size, dataHash);
	}
}
