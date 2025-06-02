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

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.ubiqube.etsi.mano.dao.mano.vim.IpPool;
import com.ubiqube.etsi.mano.dao.mano.vim.L3Data;
import com.ubiqube.etsi.mano.dao.mano.vim.SecurityGroup;
import com.ubiqube.etsi.mano.dao.mano.vim.VlProtocolData;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface Network {
	String createNetwork(final VlProtocolData vl, String name, @Nullable String dnsDomain, @Nullable String qosPolicyId);

	String createSubnet(final L3Data l3ProtocolData, @Nullable final IpPool ipAllocationPool, final String networkId);

	void deleteSubnet(final String resourceId);

	void deleteVirtualLink(String resourceId);

	String createRouter(final String name, final String internalNetworkId, final String externalNetworkId);

	void deleteRouter(String resourceId);

	@NonNull
	Map<String, String> getPublicNetworks();

	Port createPort(PortParameters portParameters);

	void deletePort(final String uuid);

	String createSecurityRule(final SecurityGroup sg, String name);

	void deleteSecurityRule(String vimResourceId);

	String createSecurityGroup(String name);

	void deleteSecurityGroup(String vimResourceId);

	List<NetworkObject> search(NetowrkSearchField field, List<String> vl);

	SubNetwork findSubNetworkById(final String id);

}
