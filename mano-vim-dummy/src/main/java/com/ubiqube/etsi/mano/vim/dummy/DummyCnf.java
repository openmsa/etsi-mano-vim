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
package com.ubiqube.etsi.mano.vim.dummy;

import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;
import com.ubiqube.etsi.mano.service.vim.Cnf;
import com.ubiqube.etsi.mano.service.vim.CnfK8sParams;
import com.ubiqube.etsi.mano.service.vim.K8sStatus;

public class DummyCnf implements Cnf {

	@Override
	public String createK8sCluster(String clusterTemplateId, String keypair, Integer masterCount, String name, Integer nodeCount, String networkId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteK8s(String vimResourceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String createContainer(CnfK8sParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteContainer(String clusterInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K8sStatus k8sStatus(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K8sServers getClusterInformations(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public K8sServers sign(String userCn, K8sServers server) {
		// TODO Auto-generated method stub
		return null;
	}

}
