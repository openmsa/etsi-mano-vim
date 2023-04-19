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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;

import ma.glasnost.orika.MapperFacade;

@WireMockTest
class OpenStackVimTest {
	private static final long GIGA = 1024 * 1024 * 1024L;
	private static final long MEGA = 1048576L;
	private MapperFacade mapper;

	@Test
	void test(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/limits")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/limits.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getQuota(vci);
		assertTrue(true);
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
		final OpenStackVim os = new OpenStackVim(mapper);
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
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getOrCreateFlavor(vci, "name", 2, 568 * MEGA, 0 * GIGA, Map.of());
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
		final OpenStackVim os = new OpenStackVim(mapper);
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
		final OpenStackVim os = new OpenStackVim(mapper);
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
		final OpenStackVim os = new OpenStackVim(mapper);
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
		final OpenStackVim os = new OpenStackVim(mapper);
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
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.getCaps(vci);
		assertTrue(true);
	}

}
