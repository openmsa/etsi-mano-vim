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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

class SubNetworkTest {

	@Test
	void test() {
		final SubNetwork sn = new SubNetwork();
		sn.setCidr("");
		sn.setCreatedTime(Date.from(Instant.now()));
		sn.setUpdatedTime(Date.from(Instant.now()));
		sn.setDnsNames(List.of());
		sn.setEnableDHCP(false);
		sn.setGateway("");
		sn.setHostRoutes(List.of());
		sn.setId("");
		sn.setIpv6AddressMode(Ipv6AddressMode.DHCPV6_STATEFUL);
		sn.setIpv6RaMode(Ipv6RaMode.DHCPV6_STATELESS);
		sn.setIpVersion(IPVersionType.V6);
		sn.setName("");
		sn.setNetworkId("");
		sn.setPools(List.of());
		sn.setTenantId("");
		assertNotNull(sn);
		assertNotNull(sn.getCidr());
		assertNotNull(sn.getCreatedTime());
		assertNotNull(sn.getDnsNames());
		assertNotNull(sn.getGateway());
		assertNotNull(sn.getHostRoutes());
		assertNotNull(sn.getId());
		assertNotNull(sn.getIpv6AddressMode());
		assertNotNull(sn.getIpv6RaMode());
		assertNotNull(sn.getIpVersion());
		assertNotNull(sn.getName());
		assertNotNull(sn.getNetworkId());
		assertNotNull(sn.getPools());
		assertNotNull(sn.getTenantId());
		assertNotNull(sn.getUpdatedTime());
	}

}
