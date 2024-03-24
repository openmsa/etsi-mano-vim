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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.service.vim;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IPVersionType {
	V4(4),
	V6(6);

	private final int version;

	IPVersionType(final int version) {
		this.version = version;
	}

	@JsonCreator
	public static IPVersionType valueOf(final int value) {
		for (final IPVersionType v : IPVersionType.values()) {
			if (v.version == value) {
				return v;
			}
		}
		return V4;
	}

	/**
	 * Gets the version in Integer form
	 *
	 * @return the version as int
	 */
	@JsonValue
	public int getVersion() {
		return version;
	}

}
