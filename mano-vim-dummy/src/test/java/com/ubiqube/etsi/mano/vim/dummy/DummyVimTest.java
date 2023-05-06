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
class DummyVimTest {

	@Test
	void test() {
		final DummyVim dv = new DummyVim();
		dv.allocateResources(null, null);
		dv.authenticate(null);
		dv.canCreateFlavor();
		dv.cnf(null);
		dv.createCompute(null);
		dv.createFlavor(null, null, 0, 0, 0, null);
		dv.createServerGroup(null, null);
		dv.deleteCompute(null, null);
		dv.deleteCompute(null, null);
		dv.deleteServerGroup(null, null);
		dv.dns(null);
		dv.freeResources(null, null);
		dv.getCaps(null);
		dv.getFlavorList(null);
		dv.getMonitoring(null);
		dv.getOrCreateFlavor(null, null, 0, 0, 0, null);
		dv.getPhysicalResources(null);
		dv.getQuota(null);
		dv.getServerGroup(null);
		dv.getType();
		dv.getZoneAvailableList(null);
		dv.isEqualMemFlavor(0, 0);
		dv.network(null);
		dv.rebootServer(null, null);
		dv.setQuota(null);
		dv.startServer(null, null);
		dv.stopServer(null, null);
		dv.storage(null);
		assertTrue(true);
	}

}
