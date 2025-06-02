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
package com.ubiqube.etsi.mano.vim.dummy;

import java.util.List;
import java.util.Map;

import com.ubiqube.etsi.mano.dao.mano.vim.IpPool;
import com.ubiqube.etsi.mano.dao.mano.vim.L3Data;
import com.ubiqube.etsi.mano.dao.mano.vim.SecurityGroup;
import com.ubiqube.etsi.mano.dao.mano.vim.VlProtocolData;
import com.ubiqube.etsi.mano.service.vim.NetowrkSearchField;
import com.ubiqube.etsi.mano.service.vim.Network;
import com.ubiqube.etsi.mano.service.vim.NetworkObject;
import com.ubiqube.etsi.mano.service.vim.Port;
import com.ubiqube.etsi.mano.service.vim.PortParameters;
import com.ubiqube.etsi.mano.service.vim.SubNetwork;

public class DummyNetwork implements Network {

	@Override
	public String createNetwork(final VlProtocolData vl, final String name, final String dnsDomain, final String qosPolicyId) {
		return null;
	}

	@Override
	public String createSubnet(final L3Data l3ProtocolData, final IpPool ipAllocationPool, final String networkId) {
		return null;
	}

	@Override
	public void deleteSubnet(final String resourceId) {
		//

	}

	@Override
	public void deleteVirtualLink(final String resourceId) {
		//

	}

	@Override
	public String createRouter(final String name, final String internalNetworkId, final String externalNetworkId) {
		return null;
	}

	@Override
	public void deleteRouter(final String resourceId) {
		//

	}

	@Override
	public Map<String, String> getPublicNetworks() {
		return Map.of();
	}

	@Override
	public Port createPort(final PortParameters portParameters) {
		return null;
	}

	@Override
	public void deletePort(final String uuid) {
		//

	}

	@Override
	public String createSecurityRule(final SecurityGroup sg, final String name) {
		//
		return null;
	}

	@Override
	public void deleteSecurityRule(final String vimResourceId) {
		//

	}

	@Override
	public String createSecurityGroup(final String name) {
		//
		return null;
	}

	@Override
	public void deleteSecurityGroup(final String vimResourceId) {
		//

	}

	@Override
	public List<NetworkObject> search(final NetowrkSearchField field, final List<String> vl) {
		return List.of();
	}

	@Override
	public SubNetwork findSubNetworkById(final String id) {
		return null;
	}
}
