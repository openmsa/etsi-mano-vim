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
package com.ubiqube.etsi.mano.service.vim.sfc.sys;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.dao.mano.nsd.VnffgDescriptor;
import com.ubiqube.etsi.mano.dao.mano.nsd.VnffgInstance;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgPostNode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.graph.TaskUtils;
import com.ubiqube.etsi.mano.service.sys.SystemV3;
import com.ubiqube.etsi.mano.service.vim.sfc.SfcFlowClassifierUow;
import com.ubiqube.etsi.mano.service.vim.sfc.SfcPortChainUow;
import com.ubiqube.etsi.mano.service.vim.sfc.enity.SfcFlowClassifierTask;
import com.ubiqube.etsi.mano.service.vim.sfc.vt.SfcFlowClassifierVt;
import com.ubiqube.etsi.mano.service.vim.sfc.vt.SfcPortChainVt;
import com.ubiqube.etsi.mano.vim.jpa.NsTaskJpa;

/**
 *
 * @author olivier
 *
 */
@Service
public class SfcPostSystem implements SystemV3<VnffgPostTask> {
	private final NsTaskJpa nsTaskJpa;

	public SfcPostSystem(final NsTaskJpa nsTaskJpa) {
		this.nsTaskJpa = nsTaskJpa;
	}

	@Override
	public SystemBuilder<UnitOfWorkV3<VnffgPostTask>> getImplementation(final OrchestrationServiceV3<VnffgPostTask> orchestrationService, final VirtualTaskV3<VnffgPostTask> virtualTask, final SystemConnections vim) {
		final SystemBuilder builder = orchestrationService.createEmptySystemBuilder();
		final VnffgPostTask task = virtualTask.getTemplateParameters();
		final SfcFlowClassifierTask flowTask = createFlowTask(task);
		final SfcFlowClassifierUow flowUow = new SfcFlowClassifierUow(new SfcFlowClassifierVt(flowTask), vim);
		final VnffgPostTask chainTask = createChainPortTask(task, task.getClassifier());
		final SfcPortChainUow chain = new SfcPortChainUow(new SfcPortChainVt(chainTask), vim);
		builder.add(flowUow, chain);
		return builder;
	}

	private SfcFlowClassifierTask createFlowTask(final VnffgPostTask taskIn) {
		final SfcFlowClassifierTask task = TaskUtils.createTask(SfcFlowClassifierTask::new, taskIn);
		task.setId(UUID.randomUUID());
		final Classifier classifier = taskIn.getClassifier();
		task.setClassifier(classifier);
		task.setToscaName(classifier.getClassifierName());
		task.setAlias(classifier.getClassifierName());
		task.setType(ResourceTypeEnum.VNFFG);
		task.setSrcPort(taskIn.getSrcPort());
		task.setDstPort(taskIn.getDstPort());
		task.setElement(setSourceDest(taskIn.getVnffg()));
		return nsTaskJpa.save(task);
	}

	private static Set<String> setSourceDest(final VnffgDescriptor vnffg) {
		return vnffg.getNfpd().stream()
				.flatMap(x -> x.getInstances().stream())
				.map(VnffgInstance::getToscaName)
				.collect(Collectors.toSet());

	}

	private VnffgPostTask createChainPortTask(final VnffgPostTask task, final Classifier classifier) {
		final VnffgPostTask chainTask = TaskUtils.createTask(VnffgPostTask::new, task);
		chainTask.setChain(task.getChain().stream().map(x -> {
			x.setId(null);
			return x;
		}).collect(Collectors.toSet()));
		chainTask.setClassifier(classifier);
		chainTask.setToscaName(task.getToscaName());
		chainTask.setAlias(task.getAlias());
		chainTask.setType(ResourceTypeEnum.VNFFG);
		return nsTaskJpa.save(chainTask);
	}

	@Override
	public String getVimType() {
		return "OPENSTACK_V3";
	}

	@Override
	public Class<? extends Node> getType() {
		return VnffgPostNode.class;
	}
}
