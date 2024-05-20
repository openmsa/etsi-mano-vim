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
package com.ubiqube.etsi.mano.openstack;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.client.IOSClientBuilder.V3;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

import com.ubiqube.etsi.mano.dao.mano.vim.AccessInfo;
import com.ubiqube.etsi.mano.dao.mano.vim.InterfaceInfo;

/**
 *
 * @author olivier
 *
 */
public class OsUtils {

	private OsUtils() {
		//
	}

	public static OSClientV3 authenticate(final InterfaceInfo interfaceInfo, final AccessInfo accessInfo) {
		final V3 base = OSFactory.builderV3().endpoint(interfaceInfo.getEndpoint());
		final String userDomain = accessInfo.getUserDomain();
		if (null != userDomain) {
			final Identifier domainIdentifier = Identifier.byName(userDomain);
			base.credentials(accessInfo.getUsername(), accessInfo.getPassword(), domainIdentifier);
		} else {
			base.credentials(accessInfo.getUsername(), accessInfo.getPassword());
		}
		final Config conf = Config.newConfig();
		Optional.ofNullable(interfaceInfo.getConnectionTimeout()).map(Integer::valueOf).ifPresent(conf::withConnectionTimeout);
		Optional.ofNullable(interfaceInfo.getReadTimeout()).map(Integer::valueOf).ifPresent(conf::withReadTimeout);
		Optional.ofNullable(interfaceInfo.getRetry()).map(Integer::valueOf).ifPresent(conf::withRetry);
		if ("true".equals(interfaceInfo.isNonStrictSsl())) {
			conf.withSSLVerificationDisabled();
		}
		if (null != interfaceInfo.getNatHost()) {
			conf.withEndpointNATResolution(interfaceInfo.getNatHost());
		}
		base.withConfig(conf);
		final String project = accessInfo.getProject();
		final String projectId = accessInfo.getProjectId();
		if (null != project) {
			base.scopeToProject(Identifier.byName(project));
		} else if (null != projectId) {
			base.scopeToProject(Identifier.byId(projectId));
		}
		return base.authenticate();
	}

	public static Properties loadGnocchiMapping() {
		try (InputStream mappting = Thread.currentThread().getContextClassLoader().getResourceAsStream("gnocchi-mapping.properties")) {
			final Properties props = new Properties();
			props.load(mappting);
			return props;
		} catch (final IOException e) {
			throw new OsVimGenericException(e);
		}
	}
}
