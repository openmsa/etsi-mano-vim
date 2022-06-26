package com.ubiqube.etsi.mano.service.graph;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import com.ubiqube.etsi.mano.dao.mano.ChangeType;
import com.ubiqube.etsi.mano.dao.mano.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.v2.PlanStatusType;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsSfcTask;
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

	public static <U extends NsTask> U createTask(final Supplier<U> newInstance, final NsSfcTask toscaEntity) {
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
