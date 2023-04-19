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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.IpPool;
import com.ubiqube.etsi.mano.dao.mano.L2Data;
import com.ubiqube.etsi.mano.dao.mano.L3Data;
import com.ubiqube.etsi.mano.dao.mano.SecurityGroup;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.VlProtocolData;
import com.ubiqube.etsi.mano.dao.mano.common.NicType;

import ma.glasnost.orika.MapperFacade;

@WireMockTest
class NetworkTest {
	private MapperFacade mapper;

	@Test
	void testCreateNetwork(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		stubFor(post(urlPathMatching("/9696/v2.0/networks")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/network-create.json"))));
		final VlProtocolData vl = new VlProtocolData();
		final L2Data l2 = new L2Data();
		vl.setL2ProtocolData(l2);
		os.network(vci).createNetwork(vl, "name", "dns", "qos");
		assertTrue(true);
	}

	@Test
	void testCreateSubNetwork(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		stubFor(post(urlPathMatching("/9696/v2.0/subnets")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/subnets-create.json"))));
		final VlProtocolData vl = new VlProtocolData();
		final L2Data l2 = new L2Data();
		vl.setL2ProtocolData(l2);
		final L3Data l3 = new L3Data();
		l3.setL3Name("tu");
		l3.setIpVersion("IPV4");
		l3.setCidr("192.168.7.0/24");
		final IpPool ipPool = new IpPool();
		ipPool.setStartIpAddress("192.168.7.0");
		ipPool.setEndIpAddress("192.168.7.128");
		os.network(vci).createSubnet(l3, ipPool, "45fd5971-d66f-4bff-a96f-9b37fab600c6");
		assertTrue(true);
	}

	@Test
	void testCreateRouter(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		// /9696/v2.0/networks/45fd5971-d66f-4bff-a96f-9b37fab600c6
		stubFor(get(urlPathMatching("/9696/v2.0/networks/45fd5971-d66f-4bff-a96f-9b37fab600c6")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/network-single1.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/subnets/de793d43-ef7d-4745-b2d3-31707dc1d3a9")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/subnets-single1.json"))));
		stubFor(post(urlPathMatching("/9696/v2.0/routers")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/routers-create.json"))));
		os.network(vci).createRouter("name", "45fd5971-d66f-4bff-a96f-9b37fab600c6", "82dbcdf4-82d3-4e95-9244-550673250dad");
		assertTrue(true);
	}

	@Test
	void testDeleteRouter(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(get(urlPathMatching("/9696/v2.0/ports")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/ports.json"))));
		stubFor(delete(urlPathMatching("/9696/v2.0/routers/7213b2d9-c981-48aa-b32b-2defc87eadad")).willReturn(aResponse()
				.withStatus(200)
				.withBody("")));
		os.network(vci).deleteRouter("7213b2d9-c981-48aa-b32b-2defc87eadad");
		assertTrue(true);
	}

	@Test
	void testCreatePort(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/ports")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/ports-create.json"))));
		os.network(vci).createPort("name", "networkId", "deviceId", null, NicType.NORMAL);
		assertTrue(true);
	}

	@Test
	void testCreateSecurityGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/security-groups")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/security-groups-create.json"))));
		os.network(vci).createSecurityGroup("test");
		assertTrue(true);
	}

	@Test
	void testCreateSecurityRule(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/security-group-rules")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/security-group-rules-create.json"))));
		final SecurityGroup sg = new SecurityGroup();
		sg.setDirection("ingress");
		// sg.setEtherType("");
		sg.setPortRangeMin(80);
		sg.setPortRangeMax(80);
		sg.setProtocol("TCP");
		os.network(vci).createSecurityRule(sg, "40519590-e3b3-4103-a85d-f5aa2fd9f085");
		assertTrue(true);
	}

}
