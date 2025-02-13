package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NetworkObjectTest {

	@Test
	void test() {
		NetworkObject networkObject = new NetworkObject("id", "name");
		assertEquals("id", networkObject.id());
		assertEquals("name", networkObject.name());
	}

}
