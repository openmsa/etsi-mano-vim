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

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.ExternalNetwork;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackmachinetemplatespec.template.spec.Image;
import io.x_k8s.cluster.infrastructure.v1beta1.openstackmachinetemplatespec.template.spec.image.Filter;

public class CommonFactory {
	private CommonFactory() {
		//
	}

	static String createCtrlPlaneName(final String clusterName) {
		return "%s-control-plane".formatted(clusterName);
	}

	static String createMdName(final String clusterName, final int id) {
		return "%s-md-%d".formatted(clusterName, id);
	}

	static String createCloudConfigName(final String clusterName) {
		return "%s-cloud-config".formatted(clusterName);
	}

	public static Image imageByName(final String name) {
		final Image image = new Image();
		final Filter filter = new Filter();
		filter.setName(name);
		image.setFilter(filter);
		return image;
	}

	public static Image imageById(final String id) {
		final Image image = new Image();
		image.setId(id);
		return image;
	}

	public static void setNameNamespace(final HasMetadata o, final String namespace, final String name) {
		o.getMetadata().setNamespace(namespace);
		o.getMetadata().setName(name);
	}

	public static ExternalNetwork extNetByName(final String name) {
		final ExternalNetwork extNet = new ExternalNetwork();
		final io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.externalnetwork.Filter filter = new io.x_k8s.cluster.infrastructure.v1beta1.openstackclusterspec.externalnetwork.Filter();
		filter.setName(name);
		extNet.setFilter(filter);
		return extNet;
	}

	public static ExternalNetwork extNetById(final String id) {
		final ExternalNetwork extNet = new ExternalNetwork();
		extNet.setId(id);
		return extNet;
	}
}
