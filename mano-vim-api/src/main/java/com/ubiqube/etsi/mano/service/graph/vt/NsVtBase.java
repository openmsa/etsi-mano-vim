/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
package com.ubiqube.etsi.mano.service.graph.vt;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsTask;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@Getter
@Setter
public abstract class NsVtBase<U extends NsTask> implements VirtualTaskV3<U> {

	private int rank;

	private U templateParameters;

	private SystemBuilder<U> systemBuilder;

	protected NsVtBase(final U nt) {
		this.templateParameters = nt;
	}

	@Override
	public void setName(final String name) {
		templateParameters.setToscaName(name);
	}

	@Override
	public void setAlias(final String alias) {
		templateParameters.setAlias(alias);
	}

	@Override
	public void setDelete(final boolean del) {
		templateParameters.setChangeType(ChangeType.REMOVED);
	}

	@Override
	public final boolean isDeleteTask() {
		return templateParameters.getChangeType() == ChangeType.REMOVED;
	}

	@Override
	public final String getVimConnectionId() {
		return templateParameters.getVimConnectionId();
	}

	@Override
	public final String getName() {
		return templateParameters.getAlias();
	}

	@Override
	public final String getAlias() {
		return templateParameters.getAlias();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getName() + ", " + getAlias() + ")";
	}
}
