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
package com.ubiqube.etsi.mano.tf;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.vnffg.Classifier;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;

@WireMockTest(httpsEnabled = true)
@SuppressWarnings("static-method")
class ContrailApiTest {

	@Test
	void test(final WireMockRuntimeInfo wmRuntimeInfo) {
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
		final SystemConnections vimConn = createConn();
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.createNetworkPolicy(vimConn, "", clas, "", "left", "right");
		assertTrue(true);
	}

	@Test
	void testDeletePortTuple(final WireMockRuntimeInfo wmRuntimeInfo) {
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.deletePortTuple(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testDeleteServiceTemplate(final WireMockRuntimeInfo wmRuntimeInfo) {
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.deleteServiceTemplate(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testDeleteServiceInstance(final WireMockRuntimeInfo wmRuntimeInfo) {
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.deleteServiceInstance(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testDeleteNetworkPolicy(final WireMockRuntimeInfo wmRuntimeInfo) {
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.deleteNetworkPolicy(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testRollbackWmi(final WireMockRuntimeInfo wmRuntimeInfo) {
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		final Classifier clas = new Classifier();
		srv.rollbackVmi(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testRollbackWmi2(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/port-tuple/")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "port-tuple": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b",
						    "virtual_machine_interface_back_refs": [
						    	{
						    		"uuid": "47a45a92-f66e-11ed-a1b4-c8f750509d3b"
						    	}
						    ]
						  }
						}
						""")));
		stubFor(get(urlPathMatching("/virtual-machine-interface/47a45a92-f66e-11ed-a1b4-c8f750509d3b")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "virtual-machine-interface": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b",
						    "instance_ip_back_refs" :[
						    	{
						    		"uuid": "b148c540-f674-11ed-a59b-c8f750509d3b",
						    		to: [
										"to-right"
						    		]
						    	}
						    ]
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.rollbackVmi(vimConn, "");
		assertTrue(true);
	}

	@Test
	void testCreatePortTuple(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/service-instance/serverInstance")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "service-instance": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		stubFor(post(urlPathMatching("/port-tuples")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "port-tuple": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.createPortTuple(vimConn, "name", "serverInstance");
		assertTrue(true);
	}

	@Test
	void testCreateServiceTemplate(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/port-tuple/")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "port-tuple": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		stubFor(post(urlPathMatching("/service-templates")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "service-template": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.createServiceTemplate(vimConn, "name");
		assertTrue(true);
	}

	@Test
	void testCreateServiceInstance(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/service-template/template")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "service-template": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
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
		stubFor(post(urlPathMatching("/service-instances")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "service-instance": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections vimConn = createConn();
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.createServiceInstance(vimConn, "name", "template", "left", "right");
		assertTrue(true);
	}

	private SystemConnections<InterfaceInfo, KeystoneAuthV3> createConn() {
		final SystemConnections<InterfaceInfo, KeystoneAuthV3> vimConn = new SystemConnections<>();
		vimConn.setAccessInfo(new KeystoneAuthV3());
		vimConn.setInterfaceInfo(new InterfaceInfo());
		vimConn.setExtra(Map.of());
		return vimConn;
	}

	@Test
	void testUpdatePort(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/port-tuple/portId")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "port-tuple": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b",
						    "virtual_machine_interface_back_refs": [
						    	{
						    		"uuid": "47a45a92-f66e-11ed-a1b4-c8f750509d3b"
						    	}
						    ]
						  }
						}
						""")));
		stubFor(put(urlPathMatching("/virtual-machine-interface/uuid")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "virtual-machine-interface": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections<InterfaceInfo, KeystoneAuthV3> vimConn = new SystemConnections<>();
		vimConn.setAccessInfo(new KeystoneAuthV3());
		vimConn.setInterfaceInfo(new InterfaceInfo());
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.updatePort(vimConn, "uuid", "portId", "mode");
		assertTrue(true);
	}

	@Test
	void testUpdatePort2(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(get(urlPathMatching("/port-tuple/portId")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "port-tuple": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b",
						    "virtual_machine_interface_back_refs": [
						    	{
						    		"uuid": "47a45a92-f66e-11ed-a1b4-c8f750509d3b"
						    	}
						    ]
						  }
						}
						""")));
		stubFor(put(urlPathMatching("/virtual-machine-interface/uuid")).willReturn(aResponse()
				.withStatus(200)
				.withHeader("Content-Type", "application/json")
				.withBody("""
						{
						  "virtual-machine-interface": {
						    "name": "left",
						    "uuid": "62acfbbc-f620-11ed-a711-c8f750509d3b"
						  }
						}
						""")));
		final ContrailApi srv = new ContrailApi();
		final SystemConnections<InterfaceInfo, KeystoneAuthV3> vimConn = new SystemConnections<>();
		vimConn.setAccessInfo(new KeystoneAuthV3());
		vimConn.setInterfaceInfo(new InterfaceInfo());
		vimConn.setExtra(Map.of());
		vimConn.getInterfaceInfo().setSdnEndpoint(wmRuntimeInfo.getHttpBaseUrl());
		srv.updatePort(vimConn, "uuid", null, "mode");
		assertTrue(true);
	}
}
