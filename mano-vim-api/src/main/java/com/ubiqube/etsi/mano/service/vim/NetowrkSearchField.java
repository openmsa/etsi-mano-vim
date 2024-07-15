package com.ubiqube.etsi.mano.service.vim;

public enum NetowrkSearchField {

	NAME("name"),
	ID("id");

	private final String value;

	NetowrkSearchField(final String string) {
		value = string;
	}

	@Override
	public String toString() {
		return value;
	}

}
