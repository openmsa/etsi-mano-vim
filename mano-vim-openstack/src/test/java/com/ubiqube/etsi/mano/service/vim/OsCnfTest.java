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
package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.magnum.MagnumService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.magnum.Certificate;
import org.openstack4j.model.magnum.Cluster;
import org.openstack4j.openstack.magnum.MagnumCluster;

import com.ubiqube.etsi.mano.dao.mano.vim.k8s.K8sServers;

@ExtendWith(MockitoExtension.class)
class OsCnfTest {
	@Mock
	private OSClientV3 os;
	@Mock
	private MagnumService magnum;

	@Test
	void testCreateContainer() {
		final OsCnf srv = new OsCnf(os);
		srv.createContainer(null);
		assertTrue(true);
	}

	@Test
	void testCreateCluster() {
		when(os.magnum()).thenReturn(magnum);
		final Cluster cluster = MagnumCluster.builder().build();
		when(magnum.createCluster(any())).thenReturn(cluster);
		final OsCnf srv = new OsCnf(os);
		srv.createK8sCluster(null, null, null, null, null, null);
		assertTrue(true);
	}

	@Test
	void testDeleteContainer() {
		when(os.magnum()).thenReturn(magnum);
		final OsCnf srv = new OsCnf(os);
		final ActionResponse actionResponse =ActionResponse.actionSuccess();
		when(magnum.deleteContainer(any())).thenReturn(actionResponse);
		srv.deleteContainer(null);
		assertTrue(true);
	}

	@Test
	void testDeleteK8s() {
		when(os.magnum()).thenReturn(magnum);
		final OsCnf srv = new OsCnf(os);
		srv.deleteK8s(null);
		assertTrue(true);
	}

	@Test
	void testGetClisterInformation() {
		when(os.magnum()).thenReturn(magnum);
		final Cluster cluster = MagnumCluster.builder()
				.status("CREATE_IN_PROGRESS")
				.build();
		when(magnum.showCluster(null)).thenReturn(cluster);
		final OsCnf srv = new OsCnf(os);
		srv.getClusterInformations(null);
		assertTrue(true);
	}

	@Test
	void testK8sStatus() {
		when(os.magnum()).thenReturn(magnum);
		final Cluster cluster = MagnumCluster.builder()
				.status("CREATE_IN_PROGRESS")
				.build();
		when(magnum.showCluster(null)).thenReturn(cluster);
		final OsCnf srv = new OsCnf(os);
		srv.k8sStatus(null);
		assertTrue(true);
	}

	@Test
	void testSign() {
		final OsCnf srv = new OsCnf(os);
		final K8sServers server = new K8sServers();
		when(os.magnum()).thenReturn(magnum);
		final Certificate certificate = Mockito.mock(Certificate.class);
		when(magnum.signCertificate(any())).thenReturn(certificate);
		srv.sign(null, server);
		assertTrue(true);
	}
}
