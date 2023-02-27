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
package com.ubiqube.etsi.mano.service.vim.sfc;

import java.util.List;

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgLoadbalancerTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
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

	public SfcLoadBalancerUow(final VirtualTaskV3<VnffgLoadbalancerTask> task, final SystemConnections vimConnectionInformation) {
		super(task, VnffgLoadbalancerNode.class);
		this.vimConnectionInformation = vimConnectionInformation;
		this.sfc = new OsSfc();
		this.task = task.getTemplateParameters();
	}

	@Override
	public String execute(final Context3d context) {
		final List<String> portPairs = context.get(PortPairNode.class);
		return sfc.createPortPairGroup(vimConnectionInformation, task.getToscaName(), portPairs);
	}

	@Override
	public String rollback(final Context3d context) {
		sfc.deletePortPairGroup(vimConnectionInformation, task.getVimResourceId());
		return null;
	}

}
