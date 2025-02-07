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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.api.types.ServiceType;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Extension;
import org.openstack4j.model.compute.AbsoluteLimit;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.BlockDeviceMappingCreate;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.openstack4j.model.compute.ext.AvailabilityZone;
import org.openstack4j.model.compute.ext.HypervisorStatistics;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.model.network.Agent;
import org.openstack4j.openstack.internal.OSClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.AffinityRule;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vim.vnfi.VimCapability;
import com.ubiqube.etsi.mano.openstack.OsUtils;
import com.ubiqube.etsi.mano.service.sys.ServerGroup;
import com.ubiqube.etsi.mano.service.vim.mon.VimMonitoring;

import jakarta.validation.constraints.Null;

@Service
public class OpenStackVim implements Vim {
	private static final long GIGA = 1024 * 1024 * 1024L;

	private static final long MEGA = 1_048_576L;

	private static final Logger LOG = LoggerFactory.getLogger(OpenStackVim.class);

	private static final ThreadLocal<Map<String, OSClientV3>> sessions = new ThreadLocal<>();

	public OpenStackVim() {
		LOG.info("""
				Booting Openstack VIM.
				   ___  ___   __   _____ __  __
				  / _ \\/ __|__\\ \\ / /_ _|  \\/
				 | (_) \\__ \\___\\ V / | || |\\/|
				  \\___/|___/    \\_/ |___|_|  |_|
				""");
	}

	/**
	 * @deprecated Will be removed since java 9.
	 */
	@Override
	@Deprecated
	protected void finalize() throws Throwable {
		sessions.remove();
		LOG.error("Finalize called.");
		super.finalize();
	}

	private static synchronized OSClientV3 getClient(final VimConnectionInformation vimConnectionInformation) {
		final Map<String, OSClientV3> sess = sessions.get();
		if (null == sess) {
			final Map<String, OSClientV3> newSess = new ConcurrentHashMap<>();
			final OSClientV3 osv3 = OsUtils.authenticate(vimConnectionInformation.getInterfaceInfo(), (KeystoneAuthV3) vimConnectionInformation.getAccessInfo());
			newSess.put(vimConnectionInformation.getVimId(), osv3);
			sessions.set(newSess);
			return osv3;
		}
		final OSClientV3 os = sess.computeIfAbsent(vimConnectionInformation.getVimId(), x -> {
			LOG.trace("OS connection: {} ", vimConnectionInformation.getVimId());
			return OsUtils.authenticate(vimConnectionInformation.getInterfaceInfo(), (KeystoneAuthV3) vimConnectionInformation.getAccessInfo());
		});
		OSClientSession.set((OSClientSession<?, ?>) os);
		return os;
	}

	@Override
	public void allocateResources(final VimConnectionInformation vimConnectionInformation, final String grantInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		os.placement();
		// XXX Do placement with blazar.
	}

	@Override
	public void freeResources(final VimConnectionInformation vimConnectionInformation, final String grantInformation) {
		// XXX Auto-generated method stub

	}

	@Override
	public String getType() {
		return "OPENSTACK_V3";
	}

	@Override
	public String createCompute(final ComputeParameters cp) {
		final VimConnectionInformation vim = cp.getVimConnectionInformation();
		final OSClientV3 os = OpenStackVim.getClient(vim);
		final ServerCreateBuilder bs = Builders.server();
		LOG.trace("Creating server: {}", cp);
		bs.image(cp.getImageId());
		bs.name(cp.getInstanceName());
		bs.flavor(cp.getFlavorId());
		if (cp.getPortsId().isEmpty()) {
			bs.networks(cp.getNetworks());
		} else {
			cp.getPortsId().forEach(bs::addNetworkPort);
		}
		if ((null != cp.getCloudInitData()) && !cp.getCloudInitData().isEmpty()) {
			bs.userData(Base64.getEncoder().encodeToString(cp.getCloudInitData().getBytes(Charset.defaultCharset())));
		}
		if (!cp.getAffinityRules().isEmpty()) {
			bs.addSchedulerHint("group", cp.getAffinityRules().get(0));
		}
		cp.getSecurityGroup().stream().forEach(bs::addSecurityGroup);
		for (int i = 0; i < cp.getStorages().size(); i++) {
			final BlockDeviceMappingCreate blockDevice = Builders.blockDeviceMapping()
					.uuid(cp.getStorages().get(i))
					.bootIndex(i)
					.build();
			bs.blockDevice(blockDevice);
		}
		final Server res = os.compute().servers().boot(bs.build());
		return res.getId();
	}

	@Override
	public String getOrCreateFlavor(final VimConnectionInformation vimConnectionInformation, final String name, final int numVcpu, final long virtualMemorySize, final long disk, final Map<String, String> flavorSpec) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final List<Flavor> matchingFlavor = os.compute().flavors().list()
				.parallelStream()
				.filter(x -> x.getVcpus() == numVcpu)
				.filter(x -> x.getRam() == (virtualMemorySize / MEGA))
				.filter(x -> x.getDisk() == (disk / GIGA))
				.map(Flavor.class::cast)
				.filter(x -> {
					final OSClientV3 os2 = OpenStackVim.getClient(vimConnectionInformation);
					final Map<String, String> specs = os2.compute().flavors().listExtraSpecs(x.getId());
					return isMatching(flavorSpec, specs);
				})
				.toList();
		if (!matchingFlavor.isEmpty()) {
			return matchingFlavor.get(0).getId();
		}
		return createFlavor(os, name, numVcpu, virtualMemorySize, disk, flavorSpec).getId();
	}

	private static void checkParameters(final int numVcpu, final long virtualMemorySize, final long disk, final Map<String, String> flavorSpec) {
		if ((disk > 0) && (disk < GIGA)) {
			throw new OpenStackException("Openstack cannot allow a disk flavor below 1Gb.");
		}
		LOG.debug("Flavor cpu={} mem={} disk={}, spec={}", numVcpu, virtualMemorySize / MEGA, disk / GIGA, flavorSpec);
	}

	private static Flavor createFlavor(final OSClientV3 os, final String name, final int numVcpu, final long virtualMemorySize, final long disk, final Map<String, String> flavorSpec) {
		checkParameters(numVcpu, virtualMemorySize, disk, flavorSpec);
		final Flavor flavor = os.compute()
				.flavors()
				.create(Builders.flavor()
						.disk((int) (disk / GIGA))
						.ram((int) (virtualMemorySize / MEGA))
						.vcpus(numVcpu)
						.isPublic(true)
						.name(name)
						.build());
		if (flavorSpec.isEmpty()) {
			return flavor;
		}
		os.compute().flavors().createAndUpdateExtraSpecs(flavor.getId(), flavorSpec);
		return flavor;
	}

	private static boolean isMatching(final Map<String, String> flavorSpec, final Map<String, String> specs) {
		final Set<Entry<String, String>> entries = flavorSpec.entrySet();
		for (final Entry<String, String> entry : entries) {
			final String osEntry = entry.getKey();
			if (specs.get(osEntry) == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<String> getZoneAvailableList(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final List<? extends AvailabilityZone> list = os.compute().zones().list();
		return list.stream().filter(x -> x.getZoneState().getAvailable())
				.map(AvailabilityZone::getZoneName)
				.toList();
	}

	@Override
	public void deleteCompute(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final ActionResponse res = os.compute().servers().delete(resourceId);
		if (409 == res.getCode()) {
			return;
		}
		int cnt = 3000;
		while (cnt >= 0) {
			final Server res2 = os.compute().servers().get(resourceId);
			if (null == res2) {
				LOG.info("Compute resource {} released.", resourceId);
				return;
			}
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				LOG.error("Interrupted", e);
				Thread.currentThread().interrupt();
			}
			cnt--;
		}
		LOG.warn("Compute resource not released.");
	}

	@Override
	public List<ServerGroup> getServerGroup(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return os.compute().hostAggregates().list().stream().map(x -> new ServerGroup(x.getId(), x.getName(), x.getAvailabilityZone()))
				.toList();

	}

	@Override
	public void startServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		os.compute().servers().action(resourceId, Action.START);
	}

	@Override
	public void stopServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		os.compute().servers().action(resourceId, Action.STOP);
	}

	@Override
	public void rebootServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		os.compute().servers().reboot(resourceId, RebootType.HARD);
	}

	@Override
	public ResourceQuota getQuota(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final AbsoluteLimit usage = os.compute().quotaSets().limits().getAbsolute();
		final DefaultQuotas quotas = new DefaultQuotas();
		Optional.ofNullable(usage.getMaxSecurityGroups()).ifPresent(quotas::setSecurityGroupsMax);
		Optional.ofNullable(usage.getSecurityGroupRulesUsed()).ifPresent(quotas::setSecurityGroupsUsed);

		Optional.ofNullable(usage.getMaxTotalCores()).ifPresent(quotas::setVcpuMax);
		Optional.ofNullable(usage.getTotalCoresUsed()).ifPresent(quotas::setVcpuUsed);

		Optional.ofNullable(usage.getMaxTotalFloatingIps()).ifPresent(quotas::setFloatingIpMax);
		Optional.ofNullable(usage.getTotalFloatingIpsUsed()).ifPresent(quotas::setFloatingIpUsed);

		Optional.ofNullable(usage.getMaxTotalInstances()).ifPresent(quotas::setInstanceMax);
		Optional.ofNullable(usage.getTotalInstancesUsed()).ifPresent(quotas::setInstanceUsed);

		Optional.ofNullable(usage.getMaxTotalRAMSize()).ifPresent(x -> quotas.setRamMax(x * MEGA));
		Optional.ofNullable(usage.getTotalRAMUsed()).ifPresent(x -> quotas.setRamUsed(x * MEGA));

		Optional.ofNullable(usage.getMaxTotalKeypairs()).ifPresent(quotas::setKeyPairsMax);
		Optional.ofNullable(usage.getTotalKeyPairsUsed()).ifPresent(quotas::setKeyPairsUsed);

		return quotas;
	}

	@Override
	public List<VimCapability> getCaps(final VimConnectionInformation vimConnectionInformation) {
		final ExecutorService tpe = Executors.newFixedThreadPool(5);
		final CompletionService<List<VimCapability>> completionService = new ExecutorCompletionService<>(tpe);
		completionService.submit(getExtensions(vimConnectionInformation));
		completionService.submit(getAgents(vimConnectionInformation));
		completionService.submit(getWithServices(vimConnectionInformation));
		int received = 0;
		final List<VimCapability> res = new ArrayList<>();
		res.add(VimCapability.REQUIRE_SUBNET_ALLOCATION);
		Exception ex = null;
		while (received < 3) {
			try {
				final Future<List<VimCapability>> resultFuture = completionService.take();
				res.addAll(resultFuture.get());
				received++;
			} catch (final InterruptedException e) {
				ex = e;
				LOG.info("", e);
				Thread.currentThread().interrupt();
				break;
			} catch (final ExecutionException e) {
				ex = e;
				break;
			}
		}
		tpe.shutdown();
		try {
			tpe.awaitTermination(5, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new VimException(e);
		}
		if (null != ex) {
			throw new VimException(ex);
		}
		return res;
	}

	private Callable<List<VimCapability>> getWithServices(final VimConnectionInformation vimConnectionInformation) {
		return () -> {
			final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
			return os.getSupportedServices().stream().map(this::mapService).filter(Objects::nonNull).toList();
		};
	}

	private VimCapability mapService(final ServiceType x) {
		return switch (x) {
		case MAGNUM -> VimCapability.HAVE_CNF;
		case PLACEMENT -> VimCapability.HAVE_PLACEMENT;
		case TELEMETRY_GNOCCHI -> VimCapability.HAVE_TELEMETRY;
		case TELEMETRY_AODH -> VimCapability.HAVE_ALARMING;
		case NETWORK -> VimCapability.HAVE_NETWORK;
		case COMPUTE -> VimCapability.HAVE_COMPUTE;
		case DNS -> VimCapability.HAVE_DNS;
		default -> null;
		};
	}

	private Callable<List<VimCapability>> getExtensions(final VimConnectionInformation vimConnectionInformation) {
		return () -> {
			final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
			final List<? extends Extension> list = os.networking().network().listExtensions();
			return list.stream()
					.map(this::convertExtenstionToCaps)
					.filter(Objects::nonNull)
					.toList();
		};
	}

	private Callable<List<VimCapability>> getAgents(final VimConnectionInformation vimConnectionInformation) {
		return () -> {
			final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
			final List<? extends Agent> list = os.networking().agent().list();
			return list.stream()
					.map(this::convertAgentToCaps)
					.filter(Objects::nonNull)
					.toList();
		};
	}

	@Override
	public String createServerGroup(final VimConnectionInformation vimConnectionInformation, final AffinityRule ar) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final String affinity = ar.isAnti() ? "anti-affinity" : "affinity";
		final org.openstack4j.model.compute.ServerGroup res = os.compute().serverGroups().create(ar.getId().toString(), affinity);
		return res.getId();
	}

	private VimCapability convertExtenstionToCaps(final Extension ext) {
		LOG.info(ext.getAlias());
		if ("dns-integration".equals(ext.getAlias())) {
			return VimCapability.HAVE_DNS;
		}
		if ("availability_zone".equals(ext.getAlias())) {
			return VimCapability.HAVE_AVAILABILITY_ZONE;
		}
		if ("bgp".equals(ext.getAlias())) {
			return VimCapability.HAVE_BGP;
		}
		if ("net-mtu".equals(ext.getAlias())) {
			return VimCapability.HAVE_NET_MTU;
		}
		if ("qos".equals(ext.getAlias())) {
			return VimCapability.HAVE_QOS;
		}
		if ("router".equals(ext.getAlias())) {
			return VimCapability.HAVE_ROUTER;
		}
		if ("vlan-transparent".equals(ext.getAlias())) {
			return VimCapability.HAVE_VLAN_TRANSPARENT;
		}
		if ("extra_dhcp_opt".equals(ext.getAlias())) {
			return VimCapability.HAVE_DHCP;
		}
		if ("trunk".equals(ext.getAlias())) {
			return VimCapability.HAVE_TRUNK;
		}
		// Should we do contrail here ?
		return null;
	}

	private VimCapability convertAgentToCaps(final Agent agent) {
		if ("neutron-openvswitch-agent".equals(agent.getBinary())) {
			return VimCapability.HAVE_VXNET;
		}
		return null;
	}

	@Override
	public @Null VimMonitoring getMonitoring(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return new OpenstackMonitoring(os);
	}

	@Override
	public Network network(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return new OsNetwork(os, vimConnectionInformation);
	}

	@Override
	public Storage storage(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return new OsStorage(os);
	}

	@Override
	public Dns dns(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return new OsDns(os);
	}

	@Override
	public Cnf cnf(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		return new OsCnf(os);
	}

	@Override
	public PhysResources getPhysicalResources(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final HypervisorStatistics stats = os.compute().hypervisors().statistics();
		final OsPhysResources ret = new OsPhysResources();
		ret.setFreeDisk(stats.getFreeDisk());
		ret.setFreeRam(stats.getFreeRam());
		ret.setLeastAvailableDisk(stats.getLeastAvailableDisk());
		ret.setMemory(stats.getMemory());
		ret.setMemoryUsed(stats.getMemoryUsed());
		ret.setRunningVM(stats.getRunningVM());
		ret.setVirtualCPU(stats.getVirtualCPU());
		ret.setVirtualUsedCPU(stats.getVirtualUsedCPU());
		return ret;
	}

	@Override
	public void deleteServerGroup(final VimConnectionInformation vimConnectionInformation, final String vimResourceId) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		os.compute().serverGroups().delete(vimResourceId);
	}

	@Override
	public void authenticate(final VimConnectionInformation vci) {
		try {
			OsUtils.authenticate(vci.getInterfaceInfo(), (KeystoneAuthV3) vci.getAccessInfo());
		} catch (final AuthenticationException e) {
			throw new OpenStackException("Authentication failed: " + e.getMessage(), e);
		}
	}

	@Override
	public List<com.ubiqube.etsi.mano.vim.dto.Flavor> getFlavorList(final VimConnectionInformation vimConnectionInformation) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final List<? extends Flavor> flv = os.compute().flavors().list();
		return flv.stream().map(x -> mapFlavor(os, x)).toList();
	}

	private static com.ubiqube.etsi.mano.vim.dto.Flavor mapFlavor(final OSClientV3 os, final Flavor fIn) {
		final com.ubiqube.etsi.mano.vim.dto.Flavor f = new OsFlavor(os);
		f.setDisabled(fIn.isDisabled());
		f.setDisk(fIn.getDisk() * GIGA);
		f.setId(fIn.getId());
		f.setName(fIn.getName());
		f.setRam(fIn.getRam() * MEGA);
		f.setSwap(fIn.getSwap());
		f.setVcpus(fIn.getVcpus());
		return f;
	}

	@Override
	public boolean isEqualMemFlavor(final long tosca, final long os) {
		final long l = tosca / MEGA;
		final long r = os / MEGA;
		return r == l;
	}

	@Override
	public boolean canCreateFlavor() {
		return true;
	}

	@Override
	public String createFlavor(final VimConnectionInformation vimConnectionInformation, final String toscaName, final long numVirtualCpu, final long virtualMemSize, final long disk, final Map<String, String> add) {
		final OSClientV3 os = OpenStackVim.getClient(vimConnectionInformation);
		final Flavor flv = createFlavor(os, toscaName, (int) numVirtualCpu, virtualMemSize, disk, add);
		return flv.getId();
	}

	@Override
	public void populateConnection(final VimConnectionInformation vci) {
		final VimConnectionInformation vimConn = vci;
		final OSClientV3 client = OsUtils.authenticate(vci.getInterfaceInfo(), (KeystoneAuthV3) vci.getAccessInfo());
		final Token token = client.getToken();
		final Project prj = token.getProject();
		final KeystoneAuthV3 ai = (KeystoneAuthV3) vimConn.getAccessInfo();
		ai.setProjectDomain(prj.getDomain().getName());
		ai.setProjectId(prj.getId());
		ai.setProject(prj.getName());
	}

}