package io.topiacoin.node.model;

import java.util.Objects;

public class ContainerInfo {
	private String id;
	private long expirationDate;

	public ContainerInfo(String id, long expirationDate) {
		this.id = id;
		this.expirationDate = expirationDate;
	}

	public ContainerInfo(ContainerInfo info) {
		this.id = info.id;
		this.expirationDate = info.expirationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ContainerInfo that = (ContainerInfo) o;
		return expirationDate == that.expirationDate &&
				Objects.equals(id, that.id);
	}

	@Override public int hashCode() {

		return Objects.hash(id, expirationDate);
	}
}
