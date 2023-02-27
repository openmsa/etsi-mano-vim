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

import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public interface Cnf {

	String createK8sCluster(final String clusterTemplateId, final String keypair, final Integer masterCount, final String name, final Integer nodeCount, String networkId);

	void deleteK8s(String vimResourceId);

	String createContainer(CnfK8sParams params);

	String deleteContainer(String clusterInstanceId);

	K8sStatus k8sStatus(String string);

	K8sServers getClusterInformations(String id);

	K8sServers sign(String userCn, K8sServers server);
}
