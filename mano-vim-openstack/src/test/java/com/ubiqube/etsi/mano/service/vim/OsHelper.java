/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
package com.ubiqube.etsi.mano.service.vim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.service.rest.model.AuthParamBasic;
import com.ubiqube.etsi.mano.service.rest.model.AuthType;
import com.ubiqube.etsi.mano.service.rest.model.AuthentificationInformations;

public class OsHelper {

	private OsHelper() {
		// Nothing.
	}

	public static String getFile(final WireMockRuntimeInfo wri, final String fileName) {
		try (InputStream is = wri.getClass().getResourceAsStream(fileName);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray()).replace("${URL}", wri.getHttpBaseUrl());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static VimConnectionInformation createServer(final WireMockRuntimeInfo wmRuntimeInfo) {
		final AuthentificationInformations auth = AuthentificationInformations.builder()
				.authParamBasic(AuthParamBasic.builder()
						.userName("user")
						.password("password")
						.build())
				.authType(List.of(AuthType.BASIC))
				.build();
		final VimConnectionInformation vci = new VimConnectionInformation();
		vci.setId(UUID.randomUUID());
		vci.setVimId(vci.getId().toString());
		final Map<String, String> ii = new HashMap<>();
		ii.put("endpoint", wmRuntimeInfo.getHttpBaseUrl());
		vci.setInterfaceInfo(ii);
		final Map<String, String> ai = new HashMap<>();
		ai.put("username", "username");
		ai.put("password", "password");
		ai.put("projectId", "projectId");
		ai.put("projectDomain", "projectDomain");
		ai.put("userDomain", "userDomain");
		vci.setAccessInfo(ai);
		return vci;
	}

	public static SystemConnections createConnection(final WireMockRuntimeInfo wmRuntimeInfo) {
		final SystemConnections vci = new SystemConnections();
		vci.setId(UUID.randomUUID());
		vci.setVimId(vci.getId().toString());
		final Map<String, String> ii = new HashMap<>();
		ii.put("endpoint", wmRuntimeInfo.getHttpBaseUrl());
		vci.setInterfaceInfo(ii);
		final Map<String, String> ai = new HashMap<>();
		ai.put("username", "username");
		ai.put("password", "password");
		ai.put("projectId", "projectId");
		ai.put("projectDomain", "projectDomain");
		ai.put("userDomain", "userDomain");
		vci.setAccessInfo(ai);
		return vci;
	}
}
