/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
package com.ubiqube.etsi.mano.openstack;

import java.util.Map;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.client.IOSClientBuilder.V3;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;

/**
 *
 * @author olivier
 *
 */
public class OsUtils {

	private OsUtils() {
		//
	}

	public static OSClientV3 authenticate(final Map<String, String> interfaceInfo, final Map<String, String> accessInfo) {
		final Map<String, String> ii = interfaceInfo;
		final V3 base = OSFactory.builderV3()
				.endpoint(ii.get("endpoint"));
		final Map<String, String> ai = accessInfo;
		final String userDomain = ai.get("userDomain");
		if (null != userDomain) {
			final Identifier domainIdentifier = Identifier.byName(userDomain);
			base.credentials(ai.get("username"), ai.get("password"), domainIdentifier);
		} else {
			base.credentials(ai.get("username"), ai.get("password"));
		}
		final Config conf = Config.newConfig();
		if ("true".equals(ii.get("non-strict-ssl"))) {
			conf.withSSLVerificationDisabled();
		}
		if (null != ii.get("nat-host")) {
			conf.withEndpointNATResolution(ii.get("nat-host"));
		}
		base.withConfig(conf);
		final String project = ai.get("project");
		final String projectId = ai.get("projectId");
		if (null != project) {
			base.scopeToProject(Identifier.byName(project));
		} else if (null != projectId) {
			base.scopeToProject(Identifier.byId(projectId));
		}
		return base.authenticate();
	}

}
