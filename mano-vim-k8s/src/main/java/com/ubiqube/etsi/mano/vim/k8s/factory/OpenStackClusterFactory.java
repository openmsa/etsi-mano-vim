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

import java.util.List;

import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackCluster;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackClusterSpec;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.IdentityRef;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.ManagedSecurityGroups;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.ManagedSubnets;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.managedsecuritygroups.AllNodesSecurityGroupRules;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.managedsecuritygroups.AllNodesSecurityGroupRules.RemoteManagedGroups;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.managedsubnets.AllocationPools;

public class OpenStackClusterFactory {
	private OpenStackClusterFactory() {
		//
	}

	public static OpenStackCluster create(final String clusterName, final String extNetId, final String cidr, final List<String> dnsList) {
		final OpenStackCluster osc = new OpenStackCluster();
		CommonFactory.setNameNamespace(osc, "default", clusterName);
		final OpenStackClusterSpec spec = new OpenStackClusterSpec();
		spec.setExternalNetwork(CommonFactory.extNetById(extNetId));
		final IdentityRef identityRef = new IdentityRef();
		identityRef.setCloudName("mano");
		identityRef.setName(CommonFactory.createCloudConfigName(clusterName));
		spec.setIdentityRef(identityRef);
		//
		final ManagedSecurityGroups msg = new ManagedSecurityGroups();
		final AllNodesSecurityGroupRules ansgr = createCalicoTcp();
		final AllNodesSecurityGroupRules ipip = createCalicoIpIp();
		final List<AllNodesSecurityGroupRules> allNodes = List.of(ansgr, ipip);
		msg.setAllNodesSecurityGroupRules(allNodes);
		msg.setAllowAllInClusterTraffic(null);
		spec.setManagedSecurityGroups(msg);
		//
		final ManagedSubnets msn = new ManagedSubnets();
		msn.setCidr(cidr);
		msn.setDnsNameservers(dnsList);
		final AllocationPools ap = new AllocationPools();
		ap.setStart("");
		ap.setEnd("");
//		final List<AllocationPools> listAllocPool = List.of();
//		msn.setAllocationPools(listAllocPool);
		final List<ManagedSubnets> listSubNet = List.of(msn);
		spec.setManagedSubnets(listSubNet);
		osc.setSpec(spec);
		return osc;
	}

	private static AllNodesSecurityGroupRules createCalicoTcp() {
		final AllNodesSecurityGroupRules ansgr = new AllNodesSecurityGroupRules();
		// This is SecurityGroup.
		ansgr.setDescription("Created by cluster-api-provider-openstack - BGP (calico)");
		ansgr.setDirection("ingress");
		ansgr.setEtherType("IPv4");
		ansgr.setName("BGP (Calico)");
		ansgr.setPortRangeMax(179L);
		ansgr.setPortRangeMin(179L);
		ansgr.setProtocol("tcp");
		final List<RemoteManagedGroups> listRemoteManaged = List.of(RemoteManagedGroups.CONTROLPLANE, RemoteManagedGroups.WORKER);
		ansgr.setRemoteManagedGroups(listRemoteManaged);
		return ansgr;
	}

	private static AllNodesSecurityGroupRules createCalicoIpIp() {
		final AllNodesSecurityGroupRules ansgr = new AllNodesSecurityGroupRules();
		// This is SecurityGroup.
		ansgr.setDescription("Created by cluster-api-provider-openstack - IP-in-IP (calico)");
		ansgr.setDirection("ingress");
		ansgr.setEtherType("IPv4");
		ansgr.setName("IP-in-IP (calico)");
		ansgr.setProtocol("4");
		final List<RemoteManagedGroups> listRemoteManaged = List.of(RemoteManagedGroups.CONTROLPLANE, RemoteManagedGroups.WORKER);
		ansgr.setRemoteManagedGroups(listRemoteManaged);
		return ansgr;
	}

}
