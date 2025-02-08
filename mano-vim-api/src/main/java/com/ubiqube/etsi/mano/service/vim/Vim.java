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

import com.ubiqube.etsi.mano.dao.mano.vim.AffinityRule;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vim.vnfi.VimCapability;
import com.ubiqube.etsi.mano.service.sys.ServerGroup;
import com.ubiqube.etsi.mano.service.vim.mon.VimMonitoring;
import com.ubiqube.etsi.mano.vim.dto.Flavor;

/**
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public interface Vim {

	String getType();

	@NonNull
	Network network(final VimConnectionInformation vimConnectionInformation);

	@NonNull
	Storage storage(final VimConnectionInformation vimConnectionInformation);

	@NonNull
	Dns dns(final VimConnectionInformation vimConnectionInformation);

	@NonNull
	VimMonitoring getMonitoring(VimConnectionInformation vimConnectionInformation);

	@NonNull
	String getOrCreateFlavor(VimConnectionInformation vimConnectionInformation, String name, int numVcpu, long virtualMemorySize, long disk, Map<String, String> flavorSpec);

	String createCompute(ComputeParameters computeParameters);

	ComputeInfo getCompute(VimConnectionInformation vimConnectionInformation, String resourceId);

	void deleteCompute(VimConnectionInformation vimConnectionInformation, String resourceId);

	void startServer(VimConnectionInformation vimConnectionInformation, String resourceId);

	void stopServer(VimConnectionInformation vimConnectionInformation, String resourceId);

	void rebootServer(VimConnectionInformation vimConnectionInformation, String resourceId);

	List<ServerGroup> getServerGroup(final VimConnectionInformation vimConnectionInformation);

	List<String> getZoneAvailableList(VimConnectionInformation vimConnectionInformation);

	ResourceQuota getQuota(final VimConnectionInformation vimConnectionInformation);

	PhysResources getPhysicalResources(final VimConnectionInformation vimConnectionInformation);

	List<VimCapability> getCaps(final VimConnectionInformation vimConnectionInformation);

	void allocateResources(VimConnectionInformation vimConnectionInformation, String x);

	void freeResources(VimConnectionInformation vimConnectionInformation, String x);

	String createServerGroup(final VimConnectionInformation vimConnectionInformation, final AffinityRule ar);

	void deleteServerGroup(VimConnectionInformation vimConnectionInformation, String vimResourceId);

	void authenticate(VimConnectionInformation vci);

	List<Flavor> getFlavorList(VimConnectionInformation vimConnectionInformation);

	boolean canCreateFlavor();

	String createFlavor(VimConnectionInformation vimConnectionInformation, String toscaName, long numVirtualCpu, long virtualMemSize, long disk, Map<String, String> add);

	Cnf cnf(VimConnectionInformation vimConnectionInformation);

	boolean isEqualMemFlavor(final long tosca, final long os);

	/**
	 * Because when setting up a connection, we sometimes use partial parameters
	 * regarding the standardized ETSI connection.
	 *
	 * @param vci A VimConnection
	 */
	void populateConnection(VimConnectionInformation vci);

}
