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
package com.ubiqube.etsi.mano.service.vim.sfc.vt;

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPostTask;
import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgLoadbalancerNode;
import com.ubiqube.etsi.mano.service.graph.vt.NsVtBase;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class SfcPortChainVt extends NsVtBase<VnffgPostTask> {

	private final VnffgPostTask task;

	public SfcPortChainVt(final VnffgPostTask nt) {
		super(nt);
		this.task = nt;
	}

	@Override
	public Class<? extends Node> getType() {
		return VnffgLoadbalancerNode.class;
	}

	@Override
	public ResultType getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
