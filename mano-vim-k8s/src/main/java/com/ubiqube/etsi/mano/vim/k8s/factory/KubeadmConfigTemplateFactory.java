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
import java.util.Map;

import io.x_k8s.cluster.bootstrap.v1beta1.KubeadmConfigTemplate;
import io.x_k8s.cluster.bootstrap.v1beta1.KubeadmConfigTemplateSpec;

public class KubeadmConfigTemplateFactory {
	private KubeadmConfigTemplateFactory() {
		//
	}

	public static KubeadmConfigTemplate create(final String clusterName, final int id) {
		final KubeadmConfigTemplate kct = new KubeadmConfigTemplate();
		CommonFactory.setNameNamespace(kct, "default", CommonFactory.createMdName(clusterName, id));
		final KubeadmConfigTemplateSpec spec = new KubeadmConfigTemplateSpec();
		final io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.Template tmpl = new io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.Template();
		final io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.Spec tmplSpec = new io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.Spec();
		tmplSpec.setFiles(List.of());
		final io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.spec.JoinConfiguration jc = new io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.spec.JoinConfiguration();
		final io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.spec.joinconfiguration.NodeRegistration nr = new io.x_k8s.cluster.bootstrap.v1beta1.kubeadmconfigtemplatespec.template.spec.joinconfiguration.NodeRegistration();
		nr.setKubeletExtraArgs(Map.of("cloud-provider", "external", "provider-id", "openstack:///'{{ instance_id }}'"));
		jc.setNodeRegistration(nr);
		nr.setName("{{ local_hostname }}");
		tmplSpec.setJoinConfiguration(jc);
		tmpl.setSpec(tmplSpec);
		spec.setTemplate(tmpl);
		kct.setSpec(spec);
		return kct;
	}

}
