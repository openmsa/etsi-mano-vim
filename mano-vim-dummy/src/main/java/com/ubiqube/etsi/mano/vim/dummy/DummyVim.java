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

import java.util.List;
import java.util.Map;

import com.ubiqube.etsi.mano.dao.mano.AffinityRule;
import com.ubiqube.etsi.mano.dao.mano.GrantInformationExt;
import com.ubiqube.etsi.mano.dao.mano.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vnfi.VimCapability;
import com.ubiqube.etsi.mano.service.sys.ServerGroup;
import com.ubiqube.etsi.mano.service.vim.Cnf;
import com.ubiqube.etsi.mano.service.vim.ComputeParameters;
import com.ubiqube.etsi.mano.service.vim.Dns;
import com.ubiqube.etsi.mano.service.vim.Network;
import com.ubiqube.etsi.mano.service.vim.PhysResources;
import com.ubiqube.etsi.mano.service.vim.ResourceQuota;
import com.ubiqube.etsi.mano.service.vim.Storage;
import com.ubiqube.etsi.mano.service.vim.Vim;
import com.ubiqube.etsi.mano.service.vim.mon.VimMonitoring;
import com.ubiqube.etsi.mano.vim.dto.Flavor;

public class DummyVim implements Vim {

	@Override
	public String getType() {
		return "dummy-vum";
	}

	@Override
	public Network network(final VimConnectionInformation vimConnectionInformation) {
		return new DummyNetwork();
	}

	@Override
	public Storage storage(final VimConnectionInformation vimConnectionInformation) {
		return new DummyStorage();
	}

	@Override
	public Dns dns(final VimConnectionInformation vimConnectionInformation) {
		return new DummyDns();
	}

	@Override
	public VimMonitoring getMonitoring(final VimConnectionInformation vimConnectionInformation) {
		return new DummyVimMonitoring();
	}

	@Override
	public String getOrCreateFlavor(final VimConnectionInformation vimConnectionInformation, final String name, final int numVcpu, final long virtualMemorySize, final long disk, final Map<String, String> flavorSpec) {
		return null;
	}

	@Override
	public String createCompute(final ComputeParameters computeParameters) {
		return null;
	}

	@Override
	public void deleteCompute(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		//
	}

	@Override
	public void startServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		// <
	}

	@Override
	public void stopServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		//

	}

	@Override
	public void rebootServer(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		//

	}

	@Override
	public List<ServerGroup> getServerGroup(final VimConnectionInformation vimConnectionInformation) {
		return List.of();
	}

	@Override
	public List<String> getZoneAvailableList(final VimConnectionInformation vimConnectionInformation) {
		return List.of("default");
	}

	@Override
	public ResourceQuota getQuota(final VimConnectionInformation vimConnectionInformation) {
		return null;
	}

	@Override
	public PhysResources getPhysicalResources(final VimConnectionInformation vimConnectionInformation) {
		return null;
	}

	@Override
	public List<VimCapability> getCaps(final VimConnectionInformation vimConnectionInformation) {
		return List.of();
	}

	@Override
	public void allocateResources(final VimConnectionInformation vimConnectionInformation, final GrantInformationExt x) {
		//

	}

	@Override
	public void freeResources(final VimConnectionInformation vimConnectionInformation, final GrantInformationExt x) {
		//

	}

	@Override
	public String createServerGroup(final VimConnectionInformation vimConnectionInformation, final AffinityRule ar) {
		return null;
	}

	@Override
	public void deleteServerGroup(final VimConnectionInformation vimConnectionInformation, final String vimResourceId) {
		//

	}

	@Override
	public void authenticate(final VimConnectionInformation vci) {
		//
	}

	@Override
	public List<Flavor> getFlavorList(final VimConnectionInformation vimConnectionInformation) {
		return List.of();
	}

	@Override
	public boolean canCreateFlavor() {
		return false;
	}

	@Override
	public String createFlavor(final VimConnectionInformation vimConnectionInformation, final String toscaName, final long numVirtualCpu, final long virtualMemSize, final long disk, final Map<String, String> add) {
		return null;
	}

	@Override
	public Cnf cnf(final VimConnectionInformation vimConnectionInformation) {
		return new DummyCnf();
	}

	@Override
	public boolean isEqualMemFlavor(final long tosca, final long os) {
		return false;
	}

}
