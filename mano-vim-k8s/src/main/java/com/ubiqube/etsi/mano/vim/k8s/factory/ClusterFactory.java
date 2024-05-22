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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.vim.k8s.factory;

import java.util.List;

import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.ClusterSpec;
import io.x_k8s.cluster.v1beta1.clusterspec.ClusterNetwork;
import io.x_k8s.cluster.v1beta1.clusterspec.ControlPlaneRef;
import io.x_k8s.cluster.v1beta1.clusterspec.clusternetwork.Pods;

public class ClusterFactory {
	private ClusterFactory() {
		//
	}

	public static Cluster create(final String clusterName, final List<String> cidrs, final String serviceDomain) {
		final Cluster cluster = new Cluster();
		CommonFactory.setNameNamespace(cluster, "default", clusterName);
		final ClusterSpec spec = new ClusterSpec();
		final ClusterNetwork cn = new ClusterNetwork();
		final Pods pods = new Pods();
		pods.setCidrBlocks(cidrs);
		cn.setPods(pods);
		cn.setServiceDomain(serviceDomain);
		spec.setClusterNetwork(cn);
		final ControlPlaneRef cpr = createControlPlaneRef(clusterName);
		spec.setControlPlaneRef(cpr);

		final io.x_k8s.cluster.v1beta1.clusterspec.InfrastructureRef infraRef = createInfrastructureRef(clusterName);
		spec.setInfrastructureRef(infraRef);
		cluster.setSpec(spec);
		return cluster;
	}

	private static ControlPlaneRef createControlPlaneRef(final String clusterName) {
		final ControlPlaneRef cpr = new ControlPlaneRef();
		cpr.setApiVersion("controlplane.cluster.x-k8s.io/v1beta1");
		cpr.setKind("KubeadmControlPlane");
		cpr.setName(CommonFactory.createCtrlPlaneName(clusterName));
		return cpr;
	}

	private static io.x_k8s.cluster.v1beta1.clusterspec.InfrastructureRef createInfrastructureRef(final String clusterName) {
		final io.x_k8s.cluster.v1beta1.clusterspec.InfrastructureRef infraRef = new io.x_k8s.cluster.v1beta1.clusterspec.InfrastructureRef();
		infraRef.setApiVersion("infrastructure.cluster.x-k8s.io/v1beta1");
		infraRef.setKind("OpenStackCluster");
		infraRef.setName(clusterName);
		return infraRef;
	}

}
