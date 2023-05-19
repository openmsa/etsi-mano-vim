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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;

import ma.glasnost.orika.MapperFacade;

@WireMockTest
class OsDnsTest {
	private MapperFacade mapper;

	@Test
	void test(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/v2/zones")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/zones.json"))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		//
		os.dns(vci).createDnsZone("name");
		assertTrue(true);
	}

}
