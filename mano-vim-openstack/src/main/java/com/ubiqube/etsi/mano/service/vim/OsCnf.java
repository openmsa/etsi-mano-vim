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

import java.util.Optional;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.magnum.Carequest;
import org.openstack4j.model.magnum.Certificate;
import org.openstack4j.model.magnum.Cluster;
import org.openstack4j.openstack.magnum.MagnumCarequest;
import org.openstack4j.openstack.magnum.MagnumCluster;

import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;
import com.ubiqube.etsi.mano.dao.mano.vnfi.StatusType;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class OsCnf implements Cnf {

	private final OSClientV3 os;

	public OsCnf(final OSClientV3 os) {
		this.os = os;
	}

	@Override
	public String createK8sCluster(final String clusterTemplateId, final String keypair, final Integer masterCount, final String name, final Integer nodeCount, final String networkId) {
		final Cluster cluster = MagnumCluster.builder()
				.clusterTemplateId(clusterTemplateId)
				.keypair(keypair)
				.masterCount(masterCount)
				.name(name)
				.nodeCount(nodeCount)
				.fixedNetwork(networkId)
				.build();
		final Cluster res = os.magnum().createCluster(cluster);
		return res.getUuid();
	}

	@Override
	public void deleteK8s(final String vimResourceId) {
		os.magnum().deleteCluster(vimResourceId);
	}

	@Override
	public String deleteContainer(final String clusterInstanceId) {
		final ActionResponse res = os.magnum().deleteContainer(clusterInstanceId);
		return res.toString();
	}

	@Override
	public String createContainer(final CnfK8sParams params) {
		//
		return null;
	}

	@Override
	public K8sStatus k8sStatus(final String string) {
		final Cluster res = os.magnum().showCluster(string);
		return K8sStatus.builder()
				.masterAddresses(res.getMasterAddresses())
				.status(StatusType.valueOf(res.getStatus()))
				.apiAddress(res.getApiAddress())
				.build();
	}

	@Override
	public K8sServers getClusterInformations(final String id) {
		final K8sStatus status = k8sStatus(id);
		final Optional<Certificate> ca = Optional.ofNullable(os.magnum().getCertificate(id));
		return K8sServers.builder()
				.apiAddress(status.getApiAddress())
				.caPem(ca.map(Certificate::getPem).orElse(null))
				.masterAddresses(status.getMasterAddresses())
				.vimResourceId(id)
				.status(status.getStatus())
				.build();
	}

	@Override
	public K8sServers sign(final String csr, final K8sServers server) {
		final Carequest ca = MagnumCarequest.builder()
				.bayUuid(server.getVimResourceId())
				.csr(csr)
				.build();
		final Certificate sign = os.magnum().signCertificate(ca);
		server.setUserCrt(sign.getPem());
		return server;
	}

}
