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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Ipv6RaMode {
	SLAAC("slaac"),
	DHCPV6_STATEFUL("dhcpv6-stateful"),
	DHCPV6_STATELESS("dhcpv6-stateless");

	private final String ipv6RaMode;

	Ipv6RaMode(final String ipv6RaMode) {
		this.ipv6RaMode = ipv6RaMode;
	}

	@JsonCreator
	public static Ipv6RaMode forValue(final String value) {
		if (value != null) {
			for (final Ipv6RaMode v : Ipv6RaMode.values()) {
				if (v.ipv6RaMode.equalsIgnoreCase(value)) {
					return v;
				}
			}
		}
		return null;
	}

	@JsonValue
	public String getIpv6RaMode() {
		return ipv6RaMode;
	}

}
