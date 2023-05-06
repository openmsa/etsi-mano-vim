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
package com.ubiqube.etsi.mano.service.graph.vt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

import com.ubiqube.etsi.mano.dao.mano.v2.PlanStatusType;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsdTask;

class NsVtBaseTest {

	@Test
	void test() {
		final NsdTask nsdTask = new NsdTask();
		nsdTask.setStatus(PlanStatusType.FAILED);
		final TestBaseVt bv = new TestBaseVt(nsdTask);
		bv.setAlias(null);
		bv.setDelete(false);
		bv.setName(null);
		bv.setRank(0);
		bv.setRemovedLiveInstanceId(null);
		bv.setSystemBuilder(null);
		bv.setTemplateParameters(nsdTask);
		bv.setVimConnectionId(null);
		bv.setVimResourceId(null);
		bv.getAlias();
		bv.getName();
		bv.getRank();
		bv.getStatus();
		bv.getSystemBuilder();
		bv.getTemplateParameters();
		bv.getToscaName();
		bv.getType();
		bv.getVimConnectionId();
		bv.getVimResourceId();
		bv.isDeleteTask();
		bv.toString();
		assertTrue(true);
	}

	@ParameterizedTest
	@EnumSource(value = PlanStatusType.class, mode = Mode.EXCLUDE, names = "REMOVED")
	void test2(final PlanStatusType en) {
		final NsdTask nsdTask = new NsdTask();
		nsdTask.setStatus(en);
		final TestBaseVt bv = new TestBaseVt(nsdTask);
		bv.getStatus();
		assertTrue(true);
	}

	@Test
	void testFail() {
		final NsdTask nsdTask = new NsdTask();
		nsdTask.setStatus(PlanStatusType.REMOVED);
		final TestBaseVt bv = new TestBaseVt(nsdTask);
		assertThrows(IllegalArgumentException.class, () -> bv.getStatus());
	}
}
