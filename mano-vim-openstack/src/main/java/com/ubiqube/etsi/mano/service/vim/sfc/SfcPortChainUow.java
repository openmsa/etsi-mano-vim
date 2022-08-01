/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
package com.ubiqube.etsi.mano.service.vim.sfc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.orchestrator.Context;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency2d;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.orchestrator.uow.Relation;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.service.graph.AbstractUnitOfWork;
import com.ubiqube.etsi.mano.service.vim.OsSfc;
import com.ubiqube.etsi.mano.service.vim.sfc.node.FlowClassifierNode;
import com.ubiqube.etsi.mano.service.vim.sfc.node.PortChainNode;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class SfcPortChainUow extends AbstractUnitOfWork<VnffgPostTask> {

	private final SystemConnections vci;
	private final OsSfc sfc;
	private final VnffgPostTask task;

	public SfcPortChainUow(final VirtualTask<VnffgPostTask> task, final SystemConnections vci) {
		super(task, PortChainNode.class);
		this.vci = vci;
		sfc = new OsSfc();
		this.task = task.getParameters();
	}

	@Override
	public String execute(final Context context) {
		final String flows = context.get(FlowClassifierNode.class, task.getClassifier().getClassifierName());
		final Set<String> ppg = task.getChain().stream().map(x -> context.get(VnffgLoadbalancerNode.class, x.getValue())).collect(Collectors.toSet());
		return sfc.createPortChain(vci, task.getToscaName(), Set.of(flows), ppg);
	}

	@Override
	public String rollback(final Context context) {
		sfc.deletePortChain(vci, task.getVimResourceId());
		return null;
	}

	@Override
	public List<NamedDependency> getNameDependencies() {
		final List<NamedDependency> ret = new ArrayList<>();
		ret.add(new NamedDependency(FlowClassifierNode.class, task.getClassifier().getClassifierName()));
		task.getChain().stream().map(x -> new NamedDependency(VnffgLoadbalancerNode.class, x.getValue())).forEach(ret::add);
		return ret;
	}

	@Override
	public List<NamedDependency> getNamedProduced() {
		return List.of(new NamedDependency(getNode(), task.getToscaName()));
	}

	@Override
	public List<NamedDependency2d> get2dDependencies() {
		final List<NamedDependency2d> ret = new ArrayList<>();
		ret.add(new NamedDependency2d(FlowClassifierNode.class, task.getClassifier().getClassifierName(), Relation.ONE_TO_ONE));
		task.getChain().stream().map(x -> new NamedDependency2d(VnffgLoadbalancerNode.class, x.getValue(), Relation.ONE_TO_MANY)).forEach(ret::add);
		return ret;
	}
}
