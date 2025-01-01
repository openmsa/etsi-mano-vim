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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Ipv6AddressMode {
	SLAAC("slaac"),
	DHCPV6_STATEFUL("dhcpv6-stateful"),
	DHCPV6_STATELESS("dhcpv6-stateless"),
	NULL("null");

	private final String ipv6AddressMode;

	Ipv6AddressMode(final String ipv6AddressMode) {
		this.ipv6AddressMode = ipv6AddressMode;
	}

	@JsonCreator
	public static Ipv6AddressMode forValue(final String value) {
		if (value != null) {
			for (final Ipv6AddressMode s : Ipv6AddressMode.values()) {
				if (s.ipv6AddressMode.equalsIgnoreCase(value)) {
					return s;
				}
			}
		}
		return null;
	}

	@JsonValue
	public String getIpv6AddressMode() {
		return ipv6AddressMode;
	}

}
