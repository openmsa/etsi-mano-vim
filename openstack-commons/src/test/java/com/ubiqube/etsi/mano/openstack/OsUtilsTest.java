/**
 *     Copyright (C) 2019-2023 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.openstack;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Olivier Vignaud
 *
 */
@SuppressWarnings("static-method")
class OsUtilsTest {

	@Test
	void testLoadMapping() {
		OsUtils.loadGnocchiMapping();
		assertTrue(true);
	}

	@Test
	void testAuth1() {
		OsUtils.authenticate(Map.of(), Map.of("userDomain", "userDomain", "project", "project", "projectId", "projectId"));
		assertTrue(true);
	}

	@Test
	void testAuth2() {
		OsUtils.authenticate(Map.of("non-strict-ssl", "true", "nat-host", "nat-host"), Map.of("projectId", "projectId"));
		assertTrue(true);
	}

	@Test
	void testAuth3() {
		final Map<String, String> ii = Map.of();
		final Map<String, String> ai = Map.of();
		assertThrows(NullPointerException.class, () -> OsUtils.authenticate(ii, ai));
	}
}
