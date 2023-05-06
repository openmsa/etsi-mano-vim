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
package com.ubiqube.etsi.mano.vim.dummy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Olivier Vignaud
 *
 */
class DummyNetworkTest {

	@Test
	void test() {
		final DummyNetwork srv = new DummyNetwork();
		srv.createNetwork(null, null, null, null);
		srv.createPort(null, null, null, null, null);
		srv.createRouter(null, null, null);
		srv.createSecurityGroup(null);
		srv.createSecurityRule(null, null);
		srv.createSubnet(null, null, null);
		srv.createSubnet(null, null, null);
		srv.deletePort(null);
		srv.deleteRouter(null);
		srv.deleteSecurityGroup(null);
		srv.deleteSecurityRule(null);
		srv.deleteSubnet(null);
		srv.deleteVirtualLink(null);
		srv.getPublicNetworks();
		srv.searchByName(null);
		assertTrue(true);
	}

}
