package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VimExceptionTest {

	@Test
	void test() {
		VimException vimException = new VimException("Test");
		assertEquals("Test", vimException.getMessage());
	}

	@Test
	void test002() {
		VimException vimException = new VimException(new Throwable());
		assertTrue(vimException.getCause() instanceof Throwable);
	}
}
