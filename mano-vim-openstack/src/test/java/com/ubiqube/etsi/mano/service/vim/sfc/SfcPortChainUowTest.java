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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.service.vim.sfc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.common.ListKeyPair;
import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.vim.OsHelper;

/**
 *
 * @author Olivier Vignaud
 *
 */
@SuppressWarnings("static-method")
@WireMockTest
class SfcPortChainUowTest {

	@Test
	void testRollback(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wmRuntimeInfo, "/auth.json"))));
		final SystemConnections vci = OsHelper.createConnection(wmRuntimeInfo);
		final VnffgPostTask nt = new VnffgPostTask();
		nt.setVimResourceId("");
		final VirtualTaskV3<VnffgPostTask> task = new PortChainVt(nt);
		final SfcPortChainUow srv = new SfcPortChainUow(task, vci);
		srv.rollback(null);
		assertTrue(true);
	}

	@Test
	void testExecute(final WireMockRuntimeInfo wmRuntimeInfo) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wmRuntimeInfo, "/auth.json"))));
		stubFor(post(urlPathMatching("/9696/v2.0/sfc/port_chains")).willReturn(aResponse()
				.withStatus(200)
				.withBody("""
						{
							"port_chain": {}
						}
						""")));
		//
		final SystemConnections vci = OsHelper.createConnection(wmRuntimeInfo);
		final VnffgPostTask nt = new VnffgPostTask();
		nt.setClassifier(new Classifier());
		final ListKeyPair chain = new ListKeyPair(null, 0);
		nt.setChain(Set.of(chain));
		final VirtualTaskV3<VnffgPostTask> task = new PortChainVt(nt);
		final SfcPortChainUow srv = new SfcPortChainUow(task, vci);
		final Context3d ctx = Mockito.mock(Context3d.class);
		when(ctx.get(any(), any())).thenReturn("");
		srv.execute(ctx);
		assertTrue(true);
	}

}

class PortChainVt implements VirtualTaskV3<VnffgPostTask> {
	VnffgPostTask task;

	public PortChainVt(final VnffgPostTask task) {
		this.task = task;
	}

	@Override
	public boolean isDeleteTask() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getVimConnectionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVimConnectionId(final String conn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(final String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Node> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAlias(final String alias) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAlias() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRank() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRank(final int rank) {
		// TODO Auto-generated method stub

	}

	@Override
	public VnffgPostTask getTemplateParameters() {
		return task;
	}

	@Override
	public void setTemplateParameters(final VnffgPostTask u) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDelete(final boolean del) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSystemBuilder(final SystemBuilder<VnffgPostTask> db) {
		// TODO Auto-generated method stub

	}

	@Override
	public SystemBuilder<VnffgPostTask> getSystemBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVimResourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVimResourceId(final String res) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getToscaName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultType getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}