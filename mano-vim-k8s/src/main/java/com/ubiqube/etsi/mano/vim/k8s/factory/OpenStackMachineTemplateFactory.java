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

import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackMachineTemplate;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackMachineTemplateSpec;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackmachinetemplatespec.Template;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackmachinetemplatespec.template.Spec;

public class OpenStackMachineTemplateFactory {
	private OpenStackMachineTemplateFactory() {
		//
	}

	public static OpenStackMachineTemplate createControlPlane(final String clusterName, final String flavor, final String imageName, final String sshKey) {
		return createControl(CommonFactory.createCtrlPlaneName(clusterName), flavor, imageName, sshKey);
	}

	public static OpenStackMachineTemplate createMd(final String clusterName, final String flavor, final String imageName, final String sshKey) {
		return createControl(CommonFactory.createMdName(clusterName, 0), flavor, imageName, sshKey);
	}

	private static OpenStackMachineTemplate createControl(final String nodeName, final String flavor, final String imageName, final String sshKey) {
		final OpenStackMachineTemplate osmt = new OpenStackMachineTemplate();
		CommonFactory.setNameNamespace(osmt, "default", nodeName);
		final OpenStackMachineTemplateSpec osmtSpec = new OpenStackMachineTemplateSpec();
		final Template template = new Template();
		final Spec spec = new Spec();
		spec.setFlavor(flavor);
		// ServerGroup, SecurityGroup, Trunk, Metadata
		spec.setImage(CommonFactory.imageByName(imageName));
		spec.setSshKeyName(sshKey);
		template.setSpec(spec);
		osmtSpec.setTemplate(template);
		osmt.setSpec(osmtSpec);
		return osmt;
	}
}
