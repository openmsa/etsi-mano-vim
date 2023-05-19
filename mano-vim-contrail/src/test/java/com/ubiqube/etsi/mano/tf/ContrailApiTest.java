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
package com.ubiqube.etsi.mano.tf;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;

@WireMockTest(httpsEnabled = true)
class ContrailApiTest {

	@Test
	void test(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/test001")).willReturn(aResponse()
				.withStatus(200)
				.withBody("{}")));
		stubFor(get(urlPathMatching("/virtual-network/left")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "virtual-network": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		stubFor(get(urlPathMatching("/virtual-network/right")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "virtual-network": {
						    "name": "right",
						    "uuid": "e6b6e704-f65e-11ed-b4eb-c8f750509d3b"
						  }
						}
						""")));
		stubFor(get(urlPathMatching("/service-instance/")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						"service-instance":
						{
							"name": "left",
							"uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						}
						}
						""")));
		stubFor(post(urlPathMatching("/network-policys")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						"network-policy":
						{
							"name": "left",
							"uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						}
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = new SystemConnections();
		vimConn.setExtra(Map.of());
		vimConn.setAccessInfo(Map.of());
		vimConn.setInterfaceInfo(Map.of("sdn-endpoint", wmRuntimeInfo.getHttpBaseUrl()));
		final Classifier clas = new Classifier();
		srv.createNetworkPolicy(vimConn, "", clas, "", "left", "right");
	}

}
