/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.vim;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.common.NicType;
import com.ubiqube.etsi.mano.dao.mano.vim.IpPool;
import com.ubiqube.etsi.mano.dao.mano.vim.L2Data;
import com.ubiqube.etsi.mano.dao.mano.vim.L3Data;
import com.ubiqube.etsi.mano.dao.mano.vim.SecurityGroup;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vim.VlProtocolData;

@SuppressWarnings("static-method")
@WireMockTest
class NetworkTest {

	@Test
	void testCreateNetwork(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
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

	private static OpenStackVim createOsVim() {
		return new OpenStackVim();
	}

	@Test
	void testCreateSubNetwork(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
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
		final OpenStackVim os = createOsVim();
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
		final OpenStackVim os = createOsVim();
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
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/ports")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/ports-create.json"))));
		PortParameters pp = PortParameters.builder()
				.name("name")
				.networkId("networkId")
				.deviceId("deviceId")
				.macAddress(null)
				.nicType(NicType.NORMAL)
				.build();
		os.network(vci).createPort(pp);
		assertTrue(true);
	}

	@Test
	void testCreateSecurityGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
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
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/security-group-rules")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/security-group-rules-create.json"))));
		final SecurityGroup sg = new SecurityGroup();
		sg.setDirection("ingress");
		// sg.setEtherType("")
		sg.setPortRangeMin(80);
		sg.setPortRangeMax(80);
		sg.setProtocol("TCP");
		os.network(vci).createSecurityRule(sg, "40519590-e3b3-4103-a85d-f5aa2fd9f085");
		assertTrue(true);
	}

	@Test
	void testDeleteSecurityRule(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).deleteSecurityRule("");
		assertTrue(true);
	}

	@Test
	void testDeleteSecurityGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).deleteSecurityGroup("");
		assertTrue(true);
	}

	@Test
	void testSearchByName(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/networks")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/networks.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).search(NetowrkSearchField.NAME, List.of("public"));
		assertTrue(true);
	}

	@Test
	void testDeletePort(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).deletePort("");
		assertTrue(true);
	}

	@Test
	void testGetPublicNetworks(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/networks")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/networks.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).getPublicNetworks();
		assertTrue(true);
	}

	@Test
	void testDeleteVirtualLink(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).deleteVirtualLink("");
		assertTrue(true);
	}

	@Test
	void testDeleteSubnet(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).deleteSubnet("");
		assertTrue(true);
	}

	@Test
	void testFindSubNetworkById(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/subnets/")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/subnets-single1.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).findSubNetworkById("");
		assertTrue(true);
	}

	@Test
	void testFindSubNetworkById2(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/subnets/")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/subnets-single2.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.network(vci).findSubNetworkById("");
		assertTrue(true);
	}
}
