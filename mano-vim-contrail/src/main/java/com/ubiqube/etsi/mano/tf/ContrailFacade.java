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
package com.ubiqube.etsi.mano.tf;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;

import net.juniper.contrail.api.ApiConnector;
import net.juniper.contrail.api.ApiConnectorFactory;
import net.juniper.contrail.api.ApiObjectBase;
import net.juniper.contrail.api.Status;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public class ContrailFacade {

	private static final Logger LOG = LoggerFactory.getLogger(ContrailFacade.class);

	public <U extends ApiObjectBase> U findById(final SystemConnections vimConnectionInformation, final Class<U> class1, final String id) {
		final ApiConnector conn = getConnection(vimConnectionInformation);
		try {
			return (U) conn.findById(class1, id);
		} catch (final IOException e) {
			throw new ContrailException(e);
		}

	}

	public String create(final SystemConnections vimConnectionInformation, final ApiObjectBase root) {
		final ApiConnector conn = getConnection(vimConnectionInformation);
		Status st;
		try {
			st = conn.create(root);
		} catch (final IOException e) {
			throw new ContrailException(e);
		}
		LOG.info("{} ", st.getClass());
		st.ifFailure(new ThrowingErrorHandler());
		return root.getUuid();
	}

	public String update(final SystemConnections vimConnectionInformation, final ApiObjectBase root) {
		final ApiConnector conn = getConnection(vimConnectionInformation);
		Status st;
		try {
			st = conn.update(root);
		} catch (final IOException e) {
			throw new ContrailException(e);
		}
		LOG.info("{} ", st.getClass());
		st.ifFailure(new ThrowingErrorHandler());
		return root.getUuid();
	}

	public String uuidToFq(final SystemConnections vimConnectionInformation, final Class<? extends ApiObjectBase> class1, final String uuid) {
		final ApiObjectBase obj = findById(vimConnectionInformation, class1, uuid);
		Objects.requireNonNull(obj, "Could not find " + class1 + " with id " + uuid);
		final List<String> qn = obj.getQualifiedName();
		return qn.stream().collect(Collectors.joining(":"));
	}

	public void delete(final SystemConnections vimConnectionInformation, final ApiObjectBase root) {
		final ApiConnector conn = getConnection(vimConnectionInformation);
		Status st;
		try {
			st = conn.delete(root);
		} catch (final IOException e) {
			throw new ContrailException(e);
		}
		LOG.info("{} ", st.getClass());
		st.ifFailure(new ThrowingErrorHandler());
	}

	@SuppressWarnings("static-method")
	private ApiConnector getConnection(final SystemConnections<InterfaceInfo, KeystoneAuthV3> vimConnectionInformation) {
		final String endpoint = vimConnectionInformation.getInterfaceInfo().getSdnEndpoint();
		final URI url = URI.create(endpoint);
		final String username = vimConnectionInformation.getAccessInfo().getUsername();
		final String password = vimConnectionInformation.getAccessInfo().getPassword();
		final ApiConnector apiConnector = ApiConnectorFactory.build(url).credentials(username, password);
		Optional.ofNullable(vimConnectionInformation.getAccessInfo().getProjectDomain()).ifPresent(apiConnector::domainName);
		Optional.ofNullable(vimConnectionInformation.getAccessInfo().getProjectId()).ifPresent(apiConnector::tenantId);
		Optional.ofNullable(vimConnectionInformation.getInterfaceInfo().getEndpoint()).ifPresent(x -> apiConnector.authServer("keystonev3", x));
		return apiConnector;
	}
}
