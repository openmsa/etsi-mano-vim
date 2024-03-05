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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.vim.k8s.K8sServers;
import com.ubiqube.etsi.mano.service.vim.VimException;

/**
 *
 * @author Olivier Vignaud
 *
 */
@SuppressWarnings("static-method")
@WireMockTest
class HelmV3WrapperClientTest {

	@Test
	void testDeploy(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/install")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withStatus(200)));
		final HelmV3WrapperClient srv = new HelmV3WrapperClient();
		final Servers server = Servers.builder()
				.url(URI.create(wri.getHttpBaseUrl()))
				.build();
		final K8sServers k8s = K8sServers.builder()
				.caPem("ca-pem")
				.userCrt("userCrt")
				.build();
		final File file = new File("src/test/resources/logback.xml");
		srv.deploy(server, k8s, "uk", file, "test");
		assertTrue(true);
	}

	@Test
	void testDeploy2(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/install")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withStatus(204)));
		final HelmV3WrapperClient srv = new HelmV3WrapperClient();
		final Servers server = Servers.builder()
				.url(URI.create(wri.getHttpBaseUrl()))
				.build();
		final K8sServers k8s = K8sServers.builder()
				.caPem("ca-pem")
				.userCrt("userCrt")
				.build();
		final File file = new File("src/test/resources/logback.xml");
		assertThrows(VimException.class, () -> srv.deploy(server, k8s, "uk", file, "test"));
	}

	@Test
	void testUnDeploy(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/uninstall/resource")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withStatus(200)));
		final HelmV3WrapperClient srv = new HelmV3WrapperClient();
		final Servers server = Servers.builder()
				.url(URI.create(wri.getHttpBaseUrl()))
				.build();
		final K8sServers k8s = K8sServers.builder()
				.caPem("ca-pem")
				.userCrt("userCrt")
				.build();
		srv.undeploy(server, k8s, "uk", "resource");
		assertTrue(true);
	}

	@Test
	void testUnDeploy2(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/uninstall/resource")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withStatus(204)));
		final HelmV3WrapperClient srv = new HelmV3WrapperClient();
		final Servers server = Servers.builder()
				.url(URI.create(wri.getHttpBaseUrl()))
				.build();
		final K8sServers k8s = K8sServers.builder()
				.caPem("ca-pem")
				.userCrt("userCrt")
				.build();
		assertThrows(VimException.class, () -> srv.undeploy(server, k8s, "uk", "resource"));
	}
}
