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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.HostRoute;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Ipv6AddressMode;
import org.openstack4j.model.network.Ipv6RaMode;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.NetworkType;
import org.openstack4j.model.network.Pool;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.network.builder.NetworkBuilder;
import org.openstack4j.model.network.builder.SubnetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.IpPool;
import com.ubiqube.etsi.mano.dao.mano.vim.L2Data;
import com.ubiqube.etsi.mano.dao.mano.vim.L3Data;
import com.ubiqube.etsi.mano.dao.mano.vim.SecurityGroup;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vim.VlProtocolData;

public class OsNetwork implements com.ubiqube.etsi.mano.service.vim.Network {
	private static final Logger LOG = LoggerFactory.getLogger(OsNetwork.class);

	private final OSClientV3 os;

	private final VimConnectionInformation vimConnectionInformation;

	public OsNetwork(final OSClientV3 os, final VimConnectionInformation vimConnectionInformation) {
		this.os = os;
		this.vimConnectionInformation = vimConnectionInformation;
	}

	@Override
	public String createNetwork(final VlProtocolData vl, final String name, final String dnsDomain, final String qosPolicyId) {
		final L2Data l2 = vl.getL2ProtocolData();
		final KeystoneAuthV3 ai = (KeystoneAuthV3) vimConnectionInformation.getAccessInfo();
		final NetworkBuilder bNet = Builders.network().tenantId(ai.getProjectId());
		bNet.name(name);
		Optional.ofNullable(l2.getMtu()).ifPresent(bNet::mtu);
		Optional.ofNullable(l2.getNetworkType()).ifPresent(x2 -> bNet.networkType(NetworkType.valueOf(x2.toUpperCase())));
		// Don't know how to use vlan_transarent.
		Optional.ofNullable(dnsDomain).ifPresent(bNet::dnsDomain);
		Optional.ofNullable(qosPolicyId).ifPresent(bNet::qosPolicyId);
		final Network network = os.networking().network().create(bNet.adminStateUp(true).build());
		LOG.debug("Network created: {} = {}", network.getId(), network.getStatus());
		return network.getId();
	}

	@Override
	public String createSubnet(final L3Data l3ProtocolData, @Nullable final IpPool ipAllocationPool, final String networkId) {
		final SubnetBuilder bSub = Builders.subnet()
				.name(l3ProtocolData.getL3Name());
		if (null != ipAllocationPool) {
			bSub.addPool(ipAllocationPool.getStartIpAddress(), ipAllocationPool.getEndIpAddress());
		}
		final KeystoneAuthV3 ai = (KeystoneAuthV3) vimConnectionInformation.getAccessInfo();
		bSub.cidr(l3ProtocolData.getCidr())
				.enableDHCP(Optional.ofNullable(l3ProtocolData.isDhcpEnabled()).orElse(false))
				.gateway(l3ProtocolData.getGatewayIp())
				.tenantId(ai.getProjectId())
				.ipVersion(convertIpVersion(l3ProtocolData.getIpVersion()))
				.networkId(networkId);
		final Subnet res = os.networking().subnet().create(bSub.build());
		LOG.debug("SubNetwork created: {}", res.getId());
		return res.getId();
	}

	private static IPVersionType convertIpVersion(final String ipVersion) {
		if ("ipv6".equals(ipVersion)) {
			return IPVersionType.valueOf("V6");
		}
		return IPVersionType.valueOf("V4");
	}

	@Override
	public void deleteSubnet(final String resourceId) {
		checkResult(os.networking().subnet().delete(resourceId));
	}

	@Override
	public void deleteVirtualLink(final String resourceId) {
		checkResult(os.networking().network().delete(resourceId));
	}

	@Override
	public String createRouter(final String name, final String internalNetworkId, final String externalNetworkId) {
		final Network net = os.networking().network().get(internalNetworkId);
		// XXX get first one ?
		final String psubnetId = net.getNeutronSubnets().get(0).getId();
		final Router routerBuilder = Builders.router()
				.name(name)
				.externalGateway(externalNetworkId)
				.build();
		final Router router = os.networking().router().create(routerBuilder);
		os.networking().router().attachInterface(router.getId(), AttachInterfaceType.SUBNET, psubnetId);
		return router.getId();
	}

	@Override
	public void deleteRouter(final String resourceId) {
		final List<? extends Port> routerList = os.networking().port().list();
		routerList.stream()
				.filter(x -> x.getDeviceId().equals(resourceId))
				.filter(x -> !"network:router_gateway".equals(x.getDeviceOwner()))
				.map(Port::getId)
				.forEach(x -> os.networking().router().detachInterface(resourceId, null, x));
		checkResult(os.networking().router().delete(resourceId));
	}

	@Override
	public Map<String, String> getPublicNetworks() {
		return os.networking().network().list().stream().filter(Network::isRouterExternal)
				.collect(Collectors.toMap(Network::getName, Network::getId));
	}

	@Override
	public com.ubiqube.etsi.mano.service.vim.Port createPort(final PortParameters params) {
		final Port port = Builders.port()
				.networkId(params.getNetworkId())
				.macAddress(params.getMacAddress())
				.name(params.getName())
				.deviceId(params.getDeviceId())
				.vNicType(params.getNicType().toString())
				.qosPolicyId(params.getQosPolicyId())
				.build();
		final Port p = os.networking().port().create(port);
		return map(p);
	}

	private static com.ubiqube.etsi.mano.service.vim.Port map(final Port p) {
		final com.ubiqube.etsi.mano.service.vim.Port ret = new com.ubiqube.etsi.mano.service.vim.Port();
		ret.setId(UUID.fromString(p.getId()));
		ret.setName(p.getName());
		ret.setMacAddress(p.getMacAddress());
		ret.setSecurityGroups(p.getSecurityGroups().stream().map(UUID::fromString).toList());
		ret.setFixedIps(p.getFixedIps().stream().map(OsNetwork::map).toList());
		return ret;
	}

	private static FixedIp map(final IP x) {
		final FixedIp ret = new FixedIp();
		ret.setIp(x.getIpAddress());
		ret.setSubnetId(x.getSubnetId());
		return ret;
	}

	@Override
	public void deletePort(final String uuid) {
		os.networking().port().delete(uuid);
	}

	@Override
	public String createSecurityGroup(final String name) {
		final org.openstack4j.model.network.SecurityGroup securityGroup = Builders.securityGroup()
				.name(name)
				.build();
		final org.openstack4j.model.network.SecurityGroup res = os.networking().securitygroup().create(securityGroup);
		return res.getId();
	}

	@Override
	public String createSecurityRule(final SecurityGroup sg, final String name) {
		final SecurityGroupRule group = Builders.securityGroupRule()
				.direction(sg.getDirection())
				.ethertype(sg.getEtherType())
				.portRangeMin(sg.getPortRangeMin())
				.portRangeMax(sg.getPortRangeMax())
				.protocol(sg.getProtocol())
				.securityGroupId(name)
				.build();
		final SecurityGroupRule res = os.networking().securityrule().create(group);
		return res.getId();
	}

	@Override
	public void deleteSecurityRule(final String vimResourceId) {
		os.networking().securityrule().delete(vimResourceId);
	}

	@Override
	public void deleteSecurityGroup(final String vimResourceId) {
		os.networking().securitygroup().delete(vimResourceId);
	}

	private static void checkResult(final ActionResponse action) {
		if (!action.isSuccess() && (action.getCode() != 404)) {
			throw new VimException(action.getCode() + " " + action.getFault());
		}
	}

	@Override
	public List<NetworkObject> search(final NetowrkSearchField field, final List<String> vl) {
		return vl.stream()
				.flatMap(x -> {
					final Map<String, String> filteringParams = new HashMap<>();
					filteringParams.put(field.toString(), x);
					return os.networking().network().list(filteringParams).stream();
				})
				.map(x -> new NetworkObject(x.getId(), x.getName()))
				.toList();
	}

	@Override
	public SubNetwork findSubNetworkById(final String id) {
		final Subnet res = os.networking().subnet().get(id);
		Objects.requireNonNull(res, "Unable to find sub-network: " + id);
		return map(res);
	}

	private static SubNetwork map(final Subnet res) {
		final SubNetwork ret = new SubNetwork();
		ret.setCidr(res.getCidr());
		ret.setCreatedTime(res.getCreatedTime());
		ret.setDnsNames(res.getDnsNames());
		ret.setEnableDHCP(res.isDHCPEnabled());
		ret.setGateway(res.getGateway());
		ret.setHostRoutes(map(res.getHostRoutes()));
		ret.setId(res.getId());
		ret.setIpv6AddressMode(map(res.getIpv6AddressMode()));
		ret.setIpv6RaMode(map(res.getIpv6RaMode()));
		ret.setIpVersion(map(res.getIpVersion()));
		ret.setName(res.getName());
		ret.setNetworkId(res.getNetworkId());
		ret.setPools(mapPool(res.getAllocationPools()));
		ret.setTenantId(res.getTenantId());
		ret.setUpdatedTime(res.getUpdatedTime());
		return ret;
	}

	private static List<IpPool> mapPool(final List<? extends Pool> allocationPools) {
		return allocationPools.stream()
				.map(x -> {
					final IpPool ret = new IpPool();
					ret.setEndIpAddress(x.getEnd());
					ret.setStartIpAddress(x.getStart());
					return ret;
				}).toList();
	}

	private static com.ubiqube.etsi.mano.service.vim.Ipv6AddressMode map(final Ipv6AddressMode ipv6AddressMode) {
		if (null == ipv6AddressMode) {
			return null;
		}
		return com.ubiqube.etsi.mano.service.vim.Ipv6AddressMode.forValue(ipv6AddressMode.getIpv6AddressMode());
	}

	private static com.ubiqube.etsi.mano.service.vim.Ipv6RaMode map(final Ipv6RaMode ipv6RaMode) {
		if (null == ipv6RaMode) {
			return null;
		}
		return com.ubiqube.etsi.mano.service.vim.Ipv6RaMode.forValue(ipv6RaMode.getIpv6RaMode());
	}

	private static com.ubiqube.etsi.mano.service.vim.IPVersionType map(final IPVersionType ipVersion) {
		if (null == ipVersion) {
			return com.ubiqube.etsi.mano.service.vim.IPVersionType.V4;
		}
		return com.ubiqube.etsi.mano.service.vim.IPVersionType.valueOf(ipVersion.name());
	}

	private static List<com.ubiqube.etsi.mano.service.vim.HostRoute> map(final List<? extends HostRoute> hostRoutes) {
		return hostRoutes.stream()
				.map(x -> {
					final com.ubiqube.etsi.mano.service.vim.HostRoute ret = new com.ubiqube.etsi.mano.service.vim.HostRoute();
					ret.setDestination(x.getDestination());
					ret.setNexthop(x.getNexthop());
					return ret;
				}).toList();
	}

}
