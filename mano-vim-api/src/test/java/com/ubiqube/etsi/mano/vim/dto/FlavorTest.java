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
package com.ubiqube.etsi.mano.vim.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FlavorTest {

	@Test
	void test() {
		final Flavor f = new Flavor();
		f.setAdditional(null);
		f.setDisabled(false);
		f.setDisk(0);
		f.setId(null);
		f.setName(null);
		f.setRam(0);
		f.setSwap(0);
		f.setVcpus(0);
		f.getAdditional();
		f.getDisk();
		f.getId();
		f.getName();
		f.getRam();
		f.getSwap();
		f.getVcpus();
		f.isDisabled();
		assertTrue(true);
	}

}
