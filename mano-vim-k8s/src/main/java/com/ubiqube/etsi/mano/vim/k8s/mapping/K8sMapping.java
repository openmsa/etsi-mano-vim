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
package com.ubiqube.etsi.mano.vim.k8s.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.ubiqube.etsi.mano.vim.k8s.conn.K8s;

import io.fabric8.kubernetes.api.model.NamedAuthInfo;
import io.fabric8.kubernetes.api.model.NamedCluster;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface K8sMapping {
	@Mapping(target = "certificateAuthInfo", ignore = true)
	@Mapping(target = "openIdAuthInfo", ignore = true)
	@Mapping(target = "tokenAuthInfo", ignore = true)
	@Mapping(target = "apiUrl", source = "cluster.server")
	@Mapping(target = "caData", source = "cluster.certificateAuthorityData")
	@Mapping(target = "namespace", ignore = true)
	void map(NamedCluster cluster, @MappingTarget K8s tgt);

	@Mapping(target = "openIdAuthInfo", ignore = true)
	@Mapping(target = "tokenAuthInfo.token", source = "user.token")
	@Mapping(target = "apiUrl", ignore = true)
	@Mapping(target = "caData", ignore = true)
	@Mapping(target = "certificateAuthInfo.clientCertificate", source = "user.clientCertificateData")
	@Mapping(target = "certificateAuthInfo.clientCertificateKey", source = "user.clientKeyData")
	@Mapping(target = "namespace", ignore = true)
	void map(NamedAuthInfo cluster, @MappingTarget K8s tgt);

}
