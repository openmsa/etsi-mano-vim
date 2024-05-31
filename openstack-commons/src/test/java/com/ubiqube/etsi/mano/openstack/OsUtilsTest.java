/**
 *     Copyright (C) 2019-2024 Ubiqube.
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

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;

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
		final InterfaceInfo ii = new InterfaceInfo();
		final KeystoneAuthV3 ai = new KeystoneAuthV3();
		ai.setUserDomain("userDomain");
		ai.setProject("project");
		ai.setProjectId("projectId");
		OsUtils.authenticate(ii, ai);
		assertTrue(true);
	}

	@Test
	void testAuth2() {
		final InterfaceInfo ii = new InterfaceInfo();
		ii.setNonStrictSsl(true);
		ii.setNatHost("nat-host");
		final KeystoneAuthV3 ai = new KeystoneAuthV3();
		ai.setProjectId("projectId");
		OsUtils.authenticate(ii, ai);
		assertTrue(true);
	}

	@Test
	void testAuth3() {
		final InterfaceInfo ii = new InterfaceInfo();
		final KeystoneAuthV3 ai = new KeystoneAuthV3();
		assertThrows(NullPointerException.class, () -> OsUtils.authenticate(ii, ai));
	}
}
