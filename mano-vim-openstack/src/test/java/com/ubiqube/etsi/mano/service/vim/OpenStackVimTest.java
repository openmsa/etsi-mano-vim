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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.service.rest.model.AuthParamBasic;
import com.ubiqube.etsi.mano.service.rest.model.AuthType;
import com.ubiqube.etsi.mano.service.rest.model.AuthentificationInformations;

import ma.glasnost.orika.MapperFacade;

@WireMockTest
class OpenStackVimTest {

	private MapperFacade mapper;

	@Test
	void test(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(authBody(wri))));
		stubFor(get(urlPathMatching("/8774/v2.1/limits")).willReturn(aResponse()
				.withStatus(200)
				.withBody(limitBody(wri))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = createServer(wri);
		final ComputeParameters cp = new ComputeParameters(vci, null, null, null, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		os.getQuota(vci);
	}

	@Test
	void testCaps(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(authBody(wri))));
		stubFor(get(urlPathMatching("/9696/v2.0/extensions")).willReturn(aResponse()
				.withStatus(200)
				.withBody(extensionsBody(wri))));
		stubFor(get(urlPathMatching("/9696/v2.0/agents")).willReturn(aResponse()
				.withStatus(200)
				.withBody(agentsBody(wri))));
		final OpenStackVim os = new OpenStackVim(mapper);
		final VimConnectionInformation vci = createServer(wri);
		os.getCaps(vci);
	}

	private static String agentsBody(final WireMockRuntimeInfo wri) {
		try (InputStream is = wri.getClass().getResourceAsStream("/agents.json");
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String extensionsBody(final WireMockRuntimeInfo wri) {
		try (InputStream is = wri.getClass().getResourceAsStream("/extensions.json");
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String limitBody(final WireMockRuntimeInfo wri) {
		try (InputStream is = wri.getClass().getResourceAsStream("/limits.json");
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String authBody(final WireMockRuntimeInfo wri) {
		try (InputStream is = wri.getClass().getResourceAsStream("/auth.json");
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray()).replace("${URL}", wri.getHttpBaseUrl());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static VimConnectionInformation createServer(final WireMockRuntimeInfo wmRuntimeInfo) {
		final AuthentificationInformations auth = AuthentificationInformations.builder()
				.authParamBasic(AuthParamBasic.builder()
						.userName("user")
						.password("password")
						.build())
				.authType(List.of(AuthType.BASIC))
				.build();
		final VimConnectionInformation vci = new VimConnectionInformation();
		vci.setId(UUID.randomUUID());
		vci.setVimId(vci.getId().toString());
		final Map<String, String> ii = new HashMap<>();
		ii.put("endpoint", wmRuntimeInfo.getHttpBaseUrl());
		vci.setInterfaceInfo(ii);
		final Map<String, String> ai = new HashMap<>();
		ai.put("username", "username");
		ai.put("password", "password");
		ai.put("projectId", "projectId");
		ai.put("projectDomain", "projectDomain");
		ai.put("userDomain", "userDomain");
		vci.setAccessInfo(ai);
		return vci;
	}
}
