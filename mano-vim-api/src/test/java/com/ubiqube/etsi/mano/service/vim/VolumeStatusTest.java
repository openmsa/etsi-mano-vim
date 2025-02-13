package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VolumeStatusTest {

	@Test
	void test() {
		VolumeStatus volumeStatus = VolumeStatus.AVAILABLE;
		assertEquals(VolumeStatus.AVAILABLE, volumeStatus);
	}

}
