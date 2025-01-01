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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.AuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.CloudRoot;
import com.ubiqube.etsi.mano.vim.k8s.model.cloud.CloudsObject;

@SuppressWarnings("static-method")
class CloudManagerTest {

	@Test
	void testName() throws Exception {
		final CloudRoot cr = new CloudRoot();
		final CloudsObject co = new CloudsObject();
		final AuthInfo auth = new AuthInfo();
		auth.setAuthUrl("http://localhost/");
		co.setAuth(auth);
		cr.setClouds(Map.of("mano", co));
		final ObjectMapper mapper = new ObjectMapper();
		System.out.println("" + mapper.writeValueAsString(cr));
		assertTrue(true);
	}

	@Test
	void testCloudsManager() {
		final CloudsManager cm = new CloudsManager();
		final VimConnectionInformation vci = new VimConnectionInformation();
		vci.setId(UUID.randomUUID());
		vci.setVimId(vci.getId().toString());
		final InterfaceInfo ii = new InterfaceInfo();
		ii.setEndpoint("http://localhost:890/");
		vci.setInterfaceInfo(ii);
		final KeystoneAuthV3 ai = KeystoneAuthV3.builder()
				.username("username")
				.password("password")
				.projectId("projectId")
				.projectDomain("projectDomain")
				.userDomain("userDomain")
				.build();
		vci.setAccessInfo(ai);
		cm.vimConnectionToYaml(vci);
		assertTrue(true);
	}
}
