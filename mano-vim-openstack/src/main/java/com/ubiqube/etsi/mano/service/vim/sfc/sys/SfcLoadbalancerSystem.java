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

import com.ubiqube.etsi.mano.dao.mano.vnffg.VnffgLoadbalancerTask;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;
import com.ubiqube.etsi.mano.service.sys.SystemV3;
import com.ubiqube.etsi.mano.service.vim.sfc.SfcLoadBalancerUow;

/**
 *
 * @author olivier
 *
 */
@Service
public class SfcLoadbalancerSystem implements SystemV3<VnffgLoadbalancerTask> {

	@Override
	public SystemBuilder<UnitOfWorkV3<VnffgLoadbalancerTask>> getImplementation(final OrchestrationServiceV3<VnffgLoadbalancerTask> orchestrationService, final VirtualTaskV3<VnffgLoadbalancerTask> virtualTask, final SystemConnections vim) {
		return orchestrationService.systemBuilderOf(new SfcLoadBalancerUow(virtualTask, vim));
	}

	@Override
	public String getVimType() {
		return "OPENSTACK_V3";
	}

}
