package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NetowrkSearchFieldTest {

	@Test
	void test() {
		NetowrkSearchField netowrkSearchField = NetowrkSearchField.NAME;
		assertEquals(NetowrkSearchField.NAME, netowrkSearchField);
		assertEquals("name", netowrkSearchField.toString());
	}

}
