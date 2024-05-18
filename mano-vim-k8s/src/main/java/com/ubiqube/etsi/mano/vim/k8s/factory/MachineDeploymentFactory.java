/**
 *     Copyright (C) 2019-2024 Ubiqube.
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
package com.ubiqube.etsi.mano.vim.k8s.factory;

import io.x_k8s.cluster.v1beta1.MachineDeployment;
import io.x_k8s.cluster.v1beta1.MachineDeploymentSpec;
import io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.Bootstrap;
import io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.bootstrap.ConfigRef;

public class MachineDeploymentFactory {
	private MachineDeploymentFactory() {
		//
	}

	public static MachineDeployment create(final String clusterName, final int replica, final int id, final String k8sVersion, final String domainZone) {
		final MachineDeployment md = new MachineDeployment();
		CommonFactory.setNameNamespace(md, "default", CommonFactory.createMdName(clusterName, id));
		final MachineDeploymentSpec mds = new MachineDeploymentSpec();
		mds.setClusterName(clusterName);
		mds.setReplicas(replica);
//		final Selector selector = new Selector();
//		selector.setMatchLabels(null);
//		mds.setSelector(selector);
		final io.x_k8s.cluster.v1beta1.machinedeploymentspec.Template template = new io.x_k8s.cluster.v1beta1.machinedeploymentspec.Template();
		final io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.Spec tmplSpec = new io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.Spec();
		final Bootstrap bootstrap = createBootstrap(clusterName, id);
		tmplSpec.setBootstrap(bootstrap);
		tmplSpec.setClusterName(clusterName);
		tmplSpec.setFailureDomain(domainZone);
		final io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.InfrastructureRef infraRef = createInfrastructureRef(clusterName, id);
		tmplSpec.setInfrastructureRef(infraRef);
		tmplSpec.setVersion(k8sVersion);
		template.setSpec(tmplSpec);
		mds.setTemplate(template);
		md.setSpec(mds);
		return md;
	}

	private static io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.InfrastructureRef createInfrastructureRef(final String clusterName, final int id) {
		final io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.InfrastructureRef infraRef = new io.x_k8s.cluster.v1beta1.machinedeploymentspec.template.spec.InfrastructureRef();
		infraRef.setApiVersion("infrastructure.cluster.x-k8s.io/v1beta1");
		infraRef.setKind("OpenStackMachineTemplate");
		infraRef.setName(CommonFactory.createMdName(clusterName, id));
		return infraRef;
	}

	private static Bootstrap createBootstrap(final String clusterName, final int id) {
		final Bootstrap bootstrap = new Bootstrap();
		final ConfigRef configRef = new ConfigRef();
		configRef.setApiVersion("bootstrap.cluster.x-k8s.io/v1beta1");
		configRef.setKind("KubeadmConfigTemplate");
		configRef.setName(CommonFactory.createMdName(clusterName, id));
		bootstrap.setConfigRef(configRef);
		return bootstrap;
	}

}
