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
package com.ubiqube.etsi.mano.service.vim.uow;

import com.ubiqube.etsi.mano.dao.mano.v2.MonitoringTask;
import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.uow.UnitOfWorkV3;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class GnocchiUnitOfWork implements UnitOfWorkV3<MonitoringTask> {

	@Override
	public VirtualTaskV3<MonitoringTask> getTask() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(final Context3d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rollback(final Context3d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Node> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(final String res) {
		// TODO Auto-generated method stub

	}

}
