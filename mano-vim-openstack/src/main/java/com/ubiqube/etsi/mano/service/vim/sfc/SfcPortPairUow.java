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

import com.ubiqube.etsi.mano.dao.mano.nsd.CpPair;
import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPortPairTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.VnfPortNode;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.graph.AbstractUnitOfWork;
import com.ubiqube.etsi.mano.service.vim.OsSfc;
import com.ubiqube.etsi.mano.service.vim.sfc.node.PortPairNode;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class SfcPortPairUow extends AbstractUnitOfWork<VnffgPortPairTask> {
	private final SystemConnections vimConnectionInformation;
	private final OsSfc sfc;
	private final VnffgPortPairTask task;
	private final CpPair cpPair;

	public SfcPortPairUow(final VirtualTaskV3<VnffgPortPairTask> task, final SystemConnections vimConnectionInformation) {
		super(task, PortPairNode.class);
		this.vimConnectionInformation = vimConnectionInformation;
		sfc = new OsSfc();
		this.task = task.getTemplateParameters();
		this.cpPair = task.getTemplateParameters().getCpPair();
	}

	@Override
	public String execute(final Context3d context) {
		final String egress = context.get(VnfPortNode.class, cpPair.getEgress());
		final String igress = context.get(VnfPortNode.class, cpPair.getIngress());
		return sfc.createPortPair(vimConnectionInformation, task.getToscaName(), egress, igress);
	}

	@Override
	public String rollback(final Context3d context) {
		sfc.deletePortPair(vimConnectionInformation, task.getVimResourceId());
		return null;
	}

}
