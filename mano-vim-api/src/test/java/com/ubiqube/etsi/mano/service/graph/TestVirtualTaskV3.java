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

import java.util.UUID;

import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsdTask;
import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class TestVirtualTaskV3 implements VirtualTaskV3<NsdTask> {

	@Override
	public boolean isDeleteTask() {
		return false;
	}

	@Override
	public String getVimConnectionId() {
		return null;
	}

	@Override
	public void setVimConnectionId(final String conn) {
		//
	}

	@Override
	public void setName(final String name) {
		//
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Class<? extends Node> getType() {
		return null;
	}

	@Override
	public void setAlias(final String alias) {
		//

	}

	@Override
	public String getAlias() {
		return null;
	}

	@Override
	public int getRank() {
		return 0;
	}

	@Override
	public void setRank(final int rank) {
		//

	}

	@Override
	public NsdTask getTemplateParameters() {
		return null;
	}

	@Override
	public void setTemplateParameters(final NsdTask u) {
		//

	}

	@Override
	public void setDelete(final boolean del) {
		//

	}

	@Override
	public void setSystemBuilder(final SystemBuilder<NsdTask> db) {
		//

	}

	@Override
	public SystemBuilder<NsdTask> getSystemBuilder() {
		//
		return null;
	}

	@Override
	public String getVimResourceId() {
		//
		return null;
	}

	@Override
	public void setVimResourceId(final String res) {
		//

	}

	@Override
	public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
		//

	}

	@Override
	public String getToscaName() {
		return null;
	}

	@Override
	public ResultType getStatus() {
		return null;
	}

}
