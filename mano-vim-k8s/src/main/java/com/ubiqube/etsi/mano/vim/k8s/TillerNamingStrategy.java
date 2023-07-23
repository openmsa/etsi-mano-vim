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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import java.util.UUID;

/**
 * Tiller doesn't seems to support namespace for chart name.
 *
 * @author Olivier Vignaud {@literal <ovi@ubiqube.com>}
 *
 */
public class TillerNamingStrategy implements K8sNamingStragegy {

	@Override
	public String getChartName(final UUID id, final String chartName) {
		return id.toString() + chartName;
	}

}
