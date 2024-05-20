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
package com.ubiqube.etsi.mano.vim.k8s;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ubiqube.etsi.mano.dao.mano.vim.AccessInfo;
import com.ubiqube.etsi.mano.dao.mano.vim.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.AuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.CloudRoot;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.CloudsObject;

@Service
public class CloudsManager {

	private final ObjectMapper mapper;

	public CloudsManager() {
		mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	public String vimConnectionToYaml(final VimConnectionInformation vci) {
		final CloudRoot cr = toCloudFormat(vci);
		try {
			return mapper.writeValueAsString(cr);
		} catch (final JsonProcessingException e) {
			throw new VimException(e);
		}
	}

	static CloudRoot toCloudFormat(final VimConnectionInformation vci) {
		final CloudRoot cr = new CloudRoot();
		cr.setClouds(Map.of("mano", toCloudObject(vci)));
		return cr;
	}

	private static CloudsObject toCloudObject(final VimConnectionInformation vci) {
		final AccessInfo ai = vci.getAccessInfo();
		final InterfaceInfo ii = vci.getInterfaceInfo();
		final CloudsObject o = new CloudsObject();
		o.setAuth(toAuth(ai, ii));
		o.setIface(Optional.ofNullable(ii).map(x -> x.getIface()).orElse("public"));
		o.setRegionName(Optional.ofNullable(ii).map(x -> x.getRegionName()).orElse("RegionOne"));
		return o;
	}

	private static AuthInfo toAuth(final AccessInfo ai, final InterfaceInfo ii) {
		final AuthInfo authInfo = new AuthInfo();
		authInfo.setAuthUrl(ii.getEndpoint());
		authInfo.setPassword(ai.getPassword());
		authInfo.setProjectDomainName(ai.getProjectDomain());
		authInfo.setProjectName(Optional.ofNullable(ai.getProjectName()).orElse("admin"));
		authInfo.setUserDomainName(ai.getUserDomain());
		authInfo.setUsername(ai.getUsername());
		return authInfo;
	}
}
