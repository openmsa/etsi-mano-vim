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
package com.ubiqube.etsi.mano.service.vim.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.openstack4j.model.compute.Server;

import com.ubiqube.etsi.mano.service.vim.ComputeInfo;
import com.ubiqube.etsi.mano.service.vim.ComputeStatus;

@Mapper
public interface ComputeInfoMapping {

	ComputeInfo mapServerInfo(Server server);

	@ValueMapping(source = "ACTIVE", target = "COMPLETED")
	@ValueMapping(source = "BUILD", target = "START")
	@ValueMapping(source = "ERROR", target = "FAILED")
	@ValueMapping(source = "SHUTOFF", target = "STOPPED")
	@ValueMapping(source = "DELETED", target = "STOPPED")
	@ValueMapping(source = "HARD_REBOOT", target = "DEPLOYING")
	@ValueMapping(source = "PAUSED", target = "STOPPED")
	@ValueMapping(source = "SUSPENDED", target = "STOPPED")
	@ValueMapping(source = "<ANY_REMAINING>", target = "DEPLOYING")
	ComputeStatus mapServerStatus(Server.Status server);
}
