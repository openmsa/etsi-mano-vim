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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.vim.AffinityRule;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;

@WireMockTest
class OpenStackVimTest {
	private static final long GIGA = 1024 * 1024 * 1024L;
	private static final long MEGA = 1048576L;

	@Test
	void test(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/limits")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/limits.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getQuota(vci);
		assertTrue(true);
	}

	private OpenStackVim createOsVim() {
		return new OpenStackVim();
	}

	@Test
	void testGetOrCreateFlavor_NoCreate(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors/detail")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors-detail.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getOrCreateFlavor(vci, "name", 2, 512 * MEGA, 0 * GIGA, Map.of());
		assertTrue(true);
	}

	@Test
	void testGetOrCreateFlavor_WithCreate(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors/detail")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors-detail.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavor-create.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getOrCreateFlavor(vci, "name", 2, 568 * MEGA, 0 * GIGA, Map.of());
		assertTrue(true);
	}

	@Test
	void testGetOrCreateFlavor_FailDisk(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors/detail")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors-detail.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavor-create.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final Map<String, String> map = Map.of();
		assertThrows(OpenStackException.class, () -> os.getOrCreateFlavor(vci, "name", 2, 568 * MEGA, 10 * MEGA, map));
		assertTrue(true);
	}

	@Test
	void testGetPhysicalResources(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/os-hypervisors/statistics")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/os-hypervisors-statistics.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getPhysicalResources(vci);
		assertTrue(true);
	}

	@Test
	void testGetFlavorList(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/flavors/detail")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavors-detail.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getFlavorList(vci);
		assertTrue(true);
	}

	@Test
	void testCreateComputeEmpty(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/servers")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/servers.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final ComputeParameters cp = new ComputeParameters(vci, null, null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		os.createCompute(cp);
		assertTrue(true);
	}

	@Test
	void testCreateCompute(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/servers")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/servers.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final List<String> networks = List.of("net");
		final List<String> storage = List.of("storage");
		final List<String> ports = List.of("port");
		final List<String> affinity = List.of("affinity");
		final ComputeParameters cp = new ComputeParameters(vci, null, null, null, networks, storage, "cloudInit", new ArrayList<>(), affinity, ports);
		os.createCompute(cp);
		assertTrue(true);
	}

	@Test
	void testCaps(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/extensions")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/extensions.json"))));
		stubFor(get(urlPathMatching("/9696/v2.0/agents")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/agents.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getCaps(vci);
		assertTrue(true);
	}

	@Test
	void testGetZoneAvailableList(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/os-availability-zone")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/availability-zone.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getZoneAvailableList(vci);
		assertTrue(true);
	}

	@Test
	void testDeleteCompute(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.deleteCompute(vci, "");
		assertTrue(true);
	}

	@Test
	void testGetServerGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/os-aggregates")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/server-group.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getServerGroup(vci);
		assertTrue(true);
	}

	@Test
	void testStartServer(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.startServer(vci, "");
		assertTrue(true);
	}

	@Test
	void testStopServer(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.stopServer(vci, "");
		assertTrue(true);
	}

	@Test
	void testRebootServer(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.rebootServer(vci, "");
		assertTrue(true);
	}

	@Test
	void testCreateServerGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/os-server-groups")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/os-server-groups.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final AffinityRule ar = new AffinityRule();
		ar.setId(UUID.randomUUID());
		ar.setAnti(false);
		os.createServerGroup(vci, ar);
		assertTrue(true);
	}

	@Test
	void testDeleteServerGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.deleteServerGroup(vci, "");
		assertTrue(true);
	}

	@Test
	void testAuthenticate(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.authenticate(vci);
		assertTrue(true);
	}

	@Test
	void testSimple(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final OpenStackVim os = createOsVim();
		os.getMonitoring(vci);
		os.network(vci);
		os.storage(vci);
		os.dns(vci);
		os.cnf(vci);
		os.canCreateFlavor();
		os.isEqualMemFlavor(1_048_576L, 1_048_576L);
		os.isEqualMemFlavor(1_048_576L, 2_048_576L);
		os.getType();
		os.allocateResources(vci, null);
		os.freeResources(vci, null);
		assertTrue(true);
	}

	@Test
	void testCreateFlavor(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/flavors")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flavor-create.json"))));
		stubFor(post(urlPathMatching("/8774/v2.1/flavors/228de8b2-ba46-4824-9489-af36d33c37f8/os-extra_specs")).willReturn(aResponse()
				.withStatus(200)
				.withBody("""
												{
						  "extra_specs": {
						    "hw:cpu_policy:": "shared",
						    "read": "openstack"
						  }
						}
												""")));
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final OpenStackVim os = createOsVim();
		os.createFlavor(vci, "name", 2, 568, 0, Map.of("spec", "value"));
		assertTrue(true);
	}

}
