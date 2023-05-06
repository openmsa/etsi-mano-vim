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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.service.vim.ComputeParameters.ComputeParametersBuilder;

class ComputeParametersTest {

	@Test
	void test() {
		TestBean.testClass(ComputeParameters.class);
	}

	@Test
	void testBuilder() {
		final ComputeParametersBuilder cp = ComputeParameters.builder()
				.affinityRules(null)
				.cloudInitData(null)
				.flavorId(null)
				.imageId(null)
				.instanceName(null)
				.instanceName(null)
				.networks(null)
				.portsId(null)
				.securityGroup(null)
				.storages(null)
				.vimConnectionInformation(null);
		assertNotNull(cp.toString());
		cp.hashCode();
		final ComputeParameters res = cp.build();
		assertNotNull(res.toString());
	}
}
