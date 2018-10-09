package io.topiacoin.node.model;

import java.util.Objects;

public class ContainerInfo {
	private String id;
	private long expirationDate;
	private Challenge challenge;

	public ContainerInfo() {
		this(null, 0, null);
	}

	public ContainerInfo(String id, long expirationDate) {
		this (id, expirationDate, null);
	}

	public ContainerInfo(String id, long expirationDate, Challenge challenge) {
		this.id = id;
		this.expirationDate = expirationDate;
		this.challenge = challenge;
	}

	public ContainerInfo(ContainerInfo info) {
		this.id = info.id;
		this.expirationDate = info.expirationDate;
		this.challenge = info.challenge;
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

	public Challenge getChallenge() {
		return challenge;
	}

	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ContainerInfo that = (ContainerInfo) o;
		return expirationDate == that.expirationDate &&
				Objects.equals(id, that.id) &&
				Objects.equals(challenge, that.challenge);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, expirationDate, challenge);
	}

	@Override
	public String toString() {
		return "ContainerInfo{" +
				"id='" + id + '\'' +
				", expirationDate=" + expirationDate +
				", challenge=" + challenge +
				'}';
	}
}
