package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ComputeStatusTest {

	@Test
	void test() {
		ComputeStatus status = ComputeStatus.START;
		assertEquals(ComputeStatus.START, status);
		status = ComputeStatus.COMPLETED;
		assertEquals(ComputeStatus.COMPLETED, status);
	}

}
