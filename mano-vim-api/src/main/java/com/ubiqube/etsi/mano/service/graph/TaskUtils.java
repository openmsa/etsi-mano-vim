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
package com.ubiqube.etsi.mano.service.graph;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanStatusType;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsTask;

/**
 *
 * @author olivier
 *
 */
public class TaskUtils {
	private TaskUtils() {
		// Nothing.
	}

	public static <U extends NsTask> U createTask(final Supplier<U> newInstance, final NsTask toscaEntity) {
		final U task = newInstance.get();
		task.setId(UUID.randomUUID());
		task.setStartDate(LocalDateTime.now());
		task.setStatus(PlanStatusType.NOT_STARTED);
		task.setChangeType(ChangeType.ADDED);
		task.setToscaName(toscaEntity.getToscaName());
		task.setAlias(toscaEntity.getToscaName());
		task.setType(ResourceTypeEnum.VNFFG);
		return task;
	}

}
