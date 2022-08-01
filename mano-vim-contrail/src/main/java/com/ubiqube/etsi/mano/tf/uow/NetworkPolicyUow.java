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
package com.ubiqube.etsi.mano.tf.uow;

import java.util.List;

import com.ubiqube.etsi.mano.orchestrator.Context;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency;
import com.ubiqube.etsi.mano.orchestrator.NamedDependency2d;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.nodes.vnfm.Network;
import com.ubiqube.etsi.mano.orchestrator.uow.Relation;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTask;
import com.ubiqube.etsi.mano.service.graph.AbstractUnitOfWork;
import com.ubiqube.etsi.mano.tf.ContrailApi;
import com.ubiqube.etsi.mano.tf.entities.NetworkPolicyTask;
import com.ubiqube.etsi.mano.tf.node.NetworkPolicyNode;
import com.ubiqube.etsi.mano.tf.node.ServiceInstanceNode;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class NetworkPolicyUow extends AbstractUnitOfWork<NetworkPolicyTask> {

	private final SystemConnections vimConnectionInformation;
	private final NetworkPolicyTask task;

	public NetworkPolicyUow(final VirtualTask<NetworkPolicyTask> task, final SystemConnections vimConnectionInformation) {
		super(task, NetworkPolicyNode.class);
		this.vimConnectionInformation = vimConnectionInformation;
		this.task = task.getParameters();
	}

	@Override
	public String execute(final Context context) {
		final ContrailApi api = new ContrailApi();
		final NetworkPolicyTask p = getTask().getParameters();
		final String serviceInstance = context.get(ServiceInstanceNode.class, p.getServiceInstance());
		final String left = context.get(Network.class, p.getLeftId());
		final String right = context.get(Network.class, p.getRightId());
		return api.createNetworkPolicy(vimConnectionInformation, p.getToscaName(), p.getClassifier(), serviceInstance, left, right);
	}

	@Override
	public String rollback(final Context context) {
		final ContrailApi api = new ContrailApi();
		api.deleteNetworkPolicy(vimConnectionInformation, getTask().getParameters().getVimResourceId());
		return null;
	}

	@Override
	public List<NamedDependency> getNameDependencies() {
		return List.of(new NamedDependency(ServiceInstanceNode.class, task.getServiceInstance()),
				new NamedDependency(Network.class, task.getLeftId()), new NamedDependency(Network.class, task.getRightId()));
	}

	@Override
	public List<NamedDependency> getNamedProduced() {
		return List.of(new NamedDependency(getNode(), task.getAlias()));
	}

	@Override
	public List<NamedDependency2d> get2dDependencies() {
		return List.of(new NamedDependency2d(ServiceInstanceNode.class, task.getServiceInstance(), Relation.MANY_TO_ONE),
				new NamedDependency2d(Network.class, task.getLeftId(), Relation.MANY_TO_ONE),
				new NamedDependency2d(Network.class, task.getRightId(), Relation.MANY_TO_ONE));
	}

}
