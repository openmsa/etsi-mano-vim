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
package com.ubiqube.etsi.mano.tf.vt;

import java.util.ArrayList;
import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.nodes.nfvo.VnffgPostNode;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.service.graph.vt.NsVtBase;
import com.ubiqube.etsi.mano.tf.entities.ServiceInstanceTask;
import com.ubiqube.etsi.mano.tf.node.ServiceInstanceNode;
import com.ubiqube.etsi.mano.tf.node.ServiceTemplateNode;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class ServiceInstanceVt extends NsVtBase<ServiceInstanceTask> {

	public ServiceInstanceVt(final ServiceInstanceTask nt) {
		super(nt);
	}

	public List<NamedDependency> getNameDependencies() {
		final List<NamedDependency> ret = new ArrayList<>();
		ret.add(new NamedDependency(ServiceTemplateNode.class, getTemplateParameters().getToscaName()));
		ret.add(new NamedDependency(Network.class, getTemplateParameters().getCpPorts().getIngressVl()));
		ret.add(new NamedDependency(Network.class, getTemplateParameters().getCpPorts().getEgressVl()));
		return ret;
	}

	public List<NamedDependency> getNamedProduced() {
		return List.of(new NamedDependency(ServiceInstanceNode.class, getTemplateParameters().getToscaName()));
	}

	@Override
	public Class<? extends Node> getType() {
		return VnffgPostNode.class;
	}

}
