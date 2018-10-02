package io.topiacoin.node.model;

import java.util.Objects;

public class MicroNetworkState {

	private String IDontUnderstandWhyThisNeedsToBeAClass;

	public MicroNetworkState(String reallyItCouldJustBeAStringUnlessYouHaveSomeOtherIdea) {
		IDontUnderstandWhyThisNeedsToBeAClass = reallyItCouldJustBeAStringUnlessYouHaveSomeOtherIdea;
	}

	public String getState() {
		return IDontUnderstandWhyThisNeedsToBeAClass;
	}

	public void setState(String likeThisSeemsSoRedundantIDontGetIt) {
		IDontUnderstandWhyThisNeedsToBeAClass = likeThisSeemsSoRedundantIDontGetIt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MicroNetworkState that = (MicroNetworkState) o;
		return Objects.equals(IDontUnderstandWhyThisNeedsToBeAClass, that.IDontUnderstandWhyThisNeedsToBeAClass);
	}

	@Override
	public int hashCode() {

		return Objects.hash(IDontUnderstandWhyThisNeedsToBeAClass);
	}
}
