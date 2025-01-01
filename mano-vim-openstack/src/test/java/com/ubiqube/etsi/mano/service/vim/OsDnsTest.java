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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;

@WireMockTest
class OsDnsTest {

	@Test
	void testCreateDnsZone(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/v2/zones")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/zones.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.dns(vci).createDnsZone("name");
		assertTrue(true);
	}

	private OpenStackVim createOsVim() {
		return new OpenStackVim();
	}

	@Test
	void testDeleteDnsZone(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.dns(vci).deleteDnsZone("");
		assertTrue(true);
	}

	@Test
	void testCreateDnsRecordSet(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8774/v2.1/servers/detail")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/server-detail.json"))));
		stubFor(get(urlPathMatching("/v2/recordsets")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/list-recordset.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.dns(vci).createDnsRecordSet("zone", "rightVdu01-Compute-0000", "middleVl01-Network-0000");
		assertTrue(true);
	}

	@Test
	void testDeleteDnsRecordSet(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/v2/zones/zone/recordsets/record")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/recordset-record.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.dns(vci).deleteDnsRecordSet("record", "zone", Set.of("ips"));
		assertTrue(true);
	}

	@Test
	void testDeleteDnsRecordSet2(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/v2/zones/zone/recordsets/record")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/recordset-record2.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.dns(vci).deleteDnsRecordSet("record", "zone", Set.of("ips"));
		assertTrue(true);
	}
}
