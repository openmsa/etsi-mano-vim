/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
