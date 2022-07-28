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

import java.util.List;

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgLoadbalancerTask;
import com.ubiqube.etsi.mano.orchestrator.Context;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.service.graph.AbstractUnitOfWork;
import com.ubiqube.etsi.mano.service.vim.OsSfc;
import com.ubiqube.etsi.mano.service.vim.sfc.node.PortPairNode;

/**
 *
 * @author olivier
 *
 */
public class SfcLoadBalancerUow extends AbstractUnitOfWork<VnffgLoadbalancerTask> {

	private final SystemConnections vimConnectionInformation;
	private final OsSfc sfc;
	private final VnffgLoadbalancerTask task;

	public SfcLoadBalancerUow(final VirtualTask<VnffgLoadbalancerTask> task, final SystemConnections vimConnectionInformation) {
		super(task, VnffgLoadbalancerNode.class);
		this.vimConnectionInformation = vimConnectionInformation;
		this.sfc = new OsSfc();
		this.task = task.getParameters();
	}

	@Override
	public String execute(final Context context) {
		final List<String> portPairs = task.getConstituant().stream().map(x -> context.get(PortPairNode.class, x.getValue())).toList();
		return sfc.createPortPairGroup(vimConnectionInformation, task.getToscaName(), portPairs);
	}

	@Override
	public String rollback(final Context context) {
		sfc.deletePortPairGroup(vimConnectionInformation, task.getVimResourceId());
		return null;
	}

	@Override
	public List<NamedDependency> getNameDependencies() {
		return task.getConstituant().stream().map(x -> new NamedDependency(PortPairNode.class, x.getValue())).toList();
	}

	@Override
	public List<NamedDependency> getNamedProduced() {
		return List.of(new NamedDependency(getNode(), task.getToscaName()));
	}

}
