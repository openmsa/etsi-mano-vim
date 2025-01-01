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

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.ii.OpenstackV3InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.vim.vnfi.CnfInformations;
import com.ubiqube.etsi.mano.dao.mano.vim.vnfi.JujuInformations;
import com.ubiqube.etsi.mano.dao.mano.vim.vnfi.VimCapability;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VimConnectionDto {

	private UUID id;

	private String vimId;

	private String vimType;

	private OpenstackV3InterfaceInfo interfaceInfo;

	private KeystoneAuthV3 accessInfo;

	private Map<String, String> extra;

	private CnfInformations cnfInfo;

	private JujuInformations jujuInfo;
	/**
	 * Capabilities of the vim. Read VimCapabilites Enum to figure out what we can
	 * do.
	 */
	private Set<VimCapability> vimCapabilities;

}
