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

import com.ubiqube.etsi.mano.service.vim.CnfK8sParams.CnfK8sParamsBuilder;

class CnfK8sParamsTest {

	@Test
	void test() {
		TestBean.testClass(CnfK8sParams.class);
	}

	@Test
	void testBuilder() {
		final CnfK8sParamsBuilder builder = CnfK8sParams.builder()
				.clusterTemplate(null)
				.dnsServer(null)
				.externalNetworkId(null)
				.flavorId(null)
				.imageId(null)
				.keypair(null)
				.masterFlavor(null)
				.name(null)
				.networkDriver(null)
				.serverType(null)
				.volumeSize(null);
		assertNotNull(builder.build());
		builder.toString();
	}
}
