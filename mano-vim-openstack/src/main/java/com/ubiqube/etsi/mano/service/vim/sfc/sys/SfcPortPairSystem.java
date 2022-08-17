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
package com.ubiqube.etsi.mano.service.vim.sfc.sys;

import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgPortPairTask;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.PortPairNode;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;
import com.ubiqube.etsi.mano.service.vim.sfc.SfcPortPairUow;

/**
 *
 * @author olivier
 *
 */
@Service
public class SfcPortPairSystem implements SystemV3<VnffgPortPairTask> {

	@Override
	public SystemBuilder<UnitOfWorkV3<VnffgPortPairTask>> getImplementation(final OrchestrationServiceV3<VnffgPortPairTask> orchestrationService, final VirtualTaskV3<VnffgPortPairTask> virtualTask, final SystemConnections vim) {
		return orchestrationService.systemBuilderOf(new SfcPortPairUow(virtualTask, vim));
	}

	@Override
	public String getVimType() {
		return "OPENSTACK_V3";
	}

	@Override
	public Class<? extends Node> getType() {
		return PortPairNode.class;
	}
}
