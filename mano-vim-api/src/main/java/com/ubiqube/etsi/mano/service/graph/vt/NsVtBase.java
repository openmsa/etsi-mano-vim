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

	private final U nt;
	private SystemBuilder db;
	private int rank;

	private U TemplateParameters;

	protected NsVtBase(final U nt) {
		this.nt = nt;
	}

	@Override
	public void setName(final String name) {
		nt.setToscaName(name);
	}

	@Override
	public void setAlias(final String alias) {
		nt.setAlias(alias);
	}

	@Override
	public void setDelete(final boolean del) {
		nt.setChangeType(ChangeType.REMOVED);
	}

	@Override
	public final boolean isDeleteTask() {
		return nt.getChangeType() == ChangeType.REMOVED;
	}

	@Override
	public final String getVimConnectionId() {
		return nt.getVimConnectionId();
	}

	@Override
	public final String getName() {
		return nt.getAlias();
	}

	@Override
	public final String getAlias() {
		return nt.getAlias();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getName() + ", " + getAlias() + ")";
	}
}
