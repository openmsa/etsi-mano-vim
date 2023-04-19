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

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;

@SuppressWarnings("static-method")
@WireMockTest
class OsSfcTest {

	@Test
	void testCreatePortPair(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OsSfc os = new OsSfc();
		final SystemConnections vci = OsHelper.createConnection(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/sfc/port_pairs")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/group-pair-create.json"))));
		os.createPortPair(vci, "test_pair", "13bd8aa9-36b3-447a-b9eb-3ea3f3fe1d2d", "2219446b-7173-4e38-8ecc-c8f47907f529");
		assertTrue(true);
	}

	@Test
	void testCreatePortGroup(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OsSfc os = new OsSfc();
		final SystemConnections vci = OsHelper.createConnection(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/sfc/port_pair_groups")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/port-pair-group-create.json"))));
		final List<String> lst = List.of("45302115-ddec-409b-8e25-6b0a5168b927", "d51152f1-167c-4e4d-841e-9251ba97646e");
		os.createPortPairGroup(vci, "pp", lst);
		assertTrue(true);
	}

	@Test
	void createCreateFlowClassifier(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OsSfc os = new OsSfc();
		final SystemConnections vci = OsHelper.createConnection(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/sfc/flow_classifiers")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/flow-classifier-create.json"))));
		final Classifier classifier = new Classifier();
		classifier.setClassifierName("name");
		classifier.setDestinationPortRangeMin(80L);
		classifier.setDestinationPortRangeMax(80L);
		classifier.setEtherType("IPv4");
		classifier.setProtocol("tcp");
		os.createFlowClassifier(vci, classifier, "13bd8aa9-36b3-447a-b9eb-3ea3f3fe1d2d", "8596640c-a91a-4abf-83fd-ecba8ad68955");
		assertTrue(true);
	}

	@Test
	void testCreateProtChain(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OsSfc os = new OsSfc();
		final SystemConnections vci = OsHelper.createConnection(wri);
		//
		stubFor(post(urlPathMatching("/9696/v2.0/sfc/port_chains")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/port-chain-create.json"))));
		final Set<String> flow = Set.of("94943a8b-bc87-49d6-9d0a-7a309c5c17b0");
		final Set<String> pp = Set.of("6ef63c9f-b686-4b70-baa4-34e7cd92b8ab");
		os.createPortChain(vci, "name", flow, pp);
		assertTrue(true);
	}
}
