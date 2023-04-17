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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openstack4j.api.OSClient.OSClientV3;

import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;

class OsNetworkTest {

	@Test
	void test() {
		final VimConnectionInformation vimConn = new VimConnectionInformation();
		final OSClientV3 os = null;
		final OsNetwork n = new OsNetwork(os, vimConn);
		assertTrue(true);
	}

}
