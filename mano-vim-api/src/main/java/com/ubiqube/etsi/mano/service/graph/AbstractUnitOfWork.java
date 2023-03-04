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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.graph;

import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

import jakarta.annotation.Nonnull;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public abstract class AbstractUnitOfWork<U> implements UnitOfWorkV3<U> {
	@Nonnull
	private final VirtualTaskV3<U> task;
	@Nonnull
	private final Class<? extends Node> node;

	protected AbstractUnitOfWork(final VirtualTaskV3<U> task, final Class<? extends Node> node) {
		this.task = task;
		this.node = node;
	}

	@Override
	public final VirtualTaskV3<U> getTask() {
		return task;
	}

	@Override
	public final Class<? extends Node> getType() {
		return node;
	}

	@Override
	public void setResource(final String res) {
		task.setVimResourceId(res);

	}

}
