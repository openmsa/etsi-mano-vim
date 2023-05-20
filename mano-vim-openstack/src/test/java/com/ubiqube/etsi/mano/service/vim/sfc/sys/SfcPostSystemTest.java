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
package com.ubiqube.etsi.mano.service.vim.sfc.sys;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.dao.mano.nsd.VnffgDescriptor;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.vim.OsHelper;
import com.ubiqube.etsi.mano.vim.jpa.NsTaskJpa;

@ExtendWith(MockitoExtension.class)
@WireMockTest
class SfcPostSystemTest {
	@Mock
	private NsTaskJpa nsTaskJpa;
	@Mock
	private OrchestrationServiceV3<VnffgPostTask> orchestrationService;

	@Test
	void test(final WireMockRuntimeInfo wri) {
		final SfcPostSystem srv = new SfcPostSystem(nsTaskJpa);
		srv.getVimType();
		srv.getType();
		final VnffgPostTask task = new VnffgPostTask();
		final Classifier cla = new Classifier();
		task.setClassifier(cla);
		final VnffgDescriptor vnffg = new VnffgDescriptor();
		vnffg.setNfpd(List.of());
		task.setVnffg(vnffg);
		task.setChain(Set.of());
		final VirtualTaskV3<VnffgPostTask> vt = new PortSystemVt(task);
		final SystemConnections vim = OsHelper.createConnection(wri);
		final SystemBuilder<UnitOfWorkV3<VnffgPostTask>> sys = Mockito.mock(SystemBuilder.class);
		when(orchestrationService.createEmptySystemBuilder()).thenReturn(sys);
		srv.getImplementation(orchestrationService, vt, vim);
		assertTrue(true);
	}

}

class PortSystemVt implements VirtualTaskV3<VnffgPostTask> {
	private final VnffgPostTask task;

	public PortSystemVt(final VnffgPostTask task) {
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