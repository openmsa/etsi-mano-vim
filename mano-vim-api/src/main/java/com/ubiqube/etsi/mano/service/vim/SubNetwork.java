/**
 *     Copyright (C) 2019-2024 Ubiqube.
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

import java.util.Date;
import java.util.List;

import com.ubiqube.etsi.mano.dao.mano.vim.IpPool;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubNetwork {
	private String id;
	private String name;
	private boolean enableDHCP;
	private String networkId;
	private String tenantId;
	private List<String> dnsNames;
	private List<IpPool> pools;
	private List<HostRoute> hostRoutes;
	private IPVersionType ipVersion;
	private String gateway;
	private String cidr;
	private Ipv6AddressMode ipv6AddressMode;
	private Ipv6RaMode ipv6RaMode;
	private Date createdTime;

	private Date updatedTime;

}
