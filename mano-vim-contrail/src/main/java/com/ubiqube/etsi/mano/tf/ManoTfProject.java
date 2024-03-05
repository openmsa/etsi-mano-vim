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
package com.ubiqube.etsi.mano.tf;

import java.util.ArrayList;
import java.util.List;

import net.juniper.contrail.api.types.Project;

public class ManoTfProject extends Project {

	private static final long serialVersionUID = 1L;
	private final List<String> tfParent;

	public ManoTfProject(final String parent) {
		this.tfParent = new ArrayList<>();
		tfParent.add(parent);
	}

	@Override
	public List<String> getDefaultParent() {
		return tfParent;
	}

	@Override
	public void setName(final String name) {
		tfParent.add(name);
	}

	@Override
	public List<String> getQualifiedName() {
		return tfParent;
	}
}
