package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class VimGenericExceptionTest {

	@Test
	void test() {
		VimGenericException vimGenericException = new VimGenericException("string");
		assertNotNull(vimGenericException);
		assertEquals("string", vimGenericException.getMessage());
	}

	@Test
	void test002() {
		VimGenericException vimGenericException = new VimGenericException(new Throwable());
		assertNotNull(vimGenericException);
		assertNotNull(vimGenericException.getCause());
	}
}
