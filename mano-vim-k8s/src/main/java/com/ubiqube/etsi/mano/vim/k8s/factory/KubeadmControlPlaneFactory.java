/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.vim.k8s.factory;

import java.util.List;
import java.util.Map;

import io.x_k8s.cluster.controlplane.v1beta1.KubeadmControlPlane;
import io.x_k8s.cluster.controlplane.v1beta1.KubeadmControlPlaneSpec;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.KubeadmConfigSpec;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.KubeadmConfigSpec.Format;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.MachineTemplate;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.ClusterConfiguration;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.Ignition;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.InitConfiguration;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.JoinConfiguration;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.clusterconfiguration.ApiServer;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.clusterconfiguration.ControllerManager;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.ignition.ContainerLinuxConfig;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.joinconfiguration.NodeRegistration;
import io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.machinetemplate.InfrastructureRef;

public class KubeadmControlPlaneFactory {

	private static final String EXTERNAL = "external";
	private static final String CLOUD_PROVIDER = "cloud-provider";

	private KubeadmControlPlaneFactory() {
		//
	}

	public static KubeadmControlPlane create(final String clusterName, final int replica, final String version, final String ignitionConfig) {
		final KubeadmControlPlane kcp = new KubeadmControlPlane();
		CommonFactory.setNameNamespace(kcp, "default", CommonFactory.createCtrlPlaneName(clusterName));
		final KubeadmControlPlaneSpec spec = new KubeadmControlPlaneSpec();
		spec.setKubeadmConfigSpec(createKubeadmConfigSpec(ignitionConfig));
		spec.setMachineTemplate(createMachineTemplate(clusterName));
		spec.setReplicas(replica);
		spec.setVersion(version);
		spec.setRolloutStrategy(null);
		kcp.setSpec(spec);
		return kcp;
	}

	private static MachineTemplate createMachineTemplate(final String clusterName) {
		final MachineTemplate machineTemplate = new MachineTemplate();
		final InfrastructureRef infraRef = new InfrastructureRef();
		infraRef.setApiVersion("infrastructure.cluster.x-k8s.io/v1beta1");
		infraRef.setKind("OpenStackMachineTemplate");
		infraRef.setName(CommonFactory.createCtrlPlaneName(clusterName));
		machineTemplate.setInfrastructureRef(infraRef);
		return machineTemplate;
	}

	private static KubeadmConfigSpec createKubeadmConfigSpec(final String ignitionConfig) {
		final KubeadmConfigSpec spec = new KubeadmConfigSpec();
		spec.setClusterConfiguration(createClusterConfiguration());
		spec.setFiles(List.of());
		spec.setInitConfiguration(createInitConfiguration());
		spec.setJoinConfiguration(createJoinConfiguration());
		if (null != ignitionConfig) {
			spec.setIgnition(createIgnitionConfig(ignitionConfig));
			spec.setFormat(Format.IGNITION);
		}
		return spec;
	}

	private static Ignition createIgnitionConfig(final String ignitionConfig) {
		final Ignition ign = new Ignition();
		final ContainerLinuxConfig container = new ContainerLinuxConfig();
		container.setAdditionalConfig(ignitionConfig);
		ign.setContainerLinuxConfig(container);
		return ign;
	}

	private static JoinConfiguration createJoinConfiguration() {
		final JoinConfiguration joinConfiguration = new JoinConfiguration();
		final NodeRegistration nodeReg = new NodeRegistration();
		nodeReg.setKubeletExtraArgs(Map.of(CLOUD_PROVIDER, EXTERNAL, "provider-id", "openstack:///'{{ instance_id }}'"));
		nodeReg.setName("{{ local_hostname }}");
		joinConfiguration.setNodeRegistration(nodeReg);
		return joinConfiguration;
	}

	private static InitConfiguration createInitConfiguration() {
		final InitConfiguration initConfiguration = new InitConfiguration();
		final io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.initconfiguration.NodeRegistration nr = new io.x_k8s.cluster.controlplane.v1beta1.kubeadmcontrolplanespec.kubeadmconfigspec.initconfiguration.NodeRegistration();
		nr.setKubeletExtraArgs(Map.of(CLOUD_PROVIDER, EXTERNAL, "provider-id", "openstack:///'{{ instance_id }}'"));
		nr.setName("{{ local_hostname }}");
		initConfiguration.setNodeRegistration(nr);
		return initConfiguration;
	}

	private static ClusterConfiguration createClusterConfiguration() {
		final ClusterConfiguration clusterConfiguration = new ClusterConfiguration();
		final ApiServer apiServer = new ApiServer();
		apiServer.setExtraArgs(Map.of(CLOUD_PROVIDER, EXTERNAL));
		clusterConfiguration.setApiServer(apiServer);
		final ControllerManager cm = new ControllerManager();
		cm.setExtraArgs(Map.of(CLOUD_PROVIDER, EXTERNAL));
		clusterConfiguration.setControllerManager(cm);
		return clusterConfiguration;
	}

}
