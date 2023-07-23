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
package com.ubiqube.etsi.mano.service.vim;

import java.util.Properties;
import java.util.UUID;

import org.openstack4j.api.OSClient.OSClientV3;

import com.ubiqube.etsi.mano.dao.mano.VnfMonitoringParameter;
import com.ubiqube.etsi.mano.openstack.AlarmUtils;
import com.ubiqube.etsi.mano.openstack.OsUtils;
import com.ubiqube.etsi.mano.orchestrator.OrchestrationServiceV3;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class OsMonitoringSystem {

	private final OSClientV3 os;

	private final Properties props;

	public OsMonitoringSystem(final OSClientV3 os) {
		this.os = os;
		this.props = OsUtils.loadGnocchiMapping();
	}

	@SuppressWarnings("static-method")
	public String getProviderId() {
		return "OS-GNOCCHI-V1";
	}

	public SystemBuilder getImplementation(final OrchestrationServiceV3 orchestrationService, final VirtualTaskV3<VnfMonitoringParameter> virtualTask, final SystemConnections vim) {
		final VnfMonitoringParameter mp = virtualTask.getTemplateParameters();
		// final MonitoringTask task = createTask(MonitoringTask::new);
		// task.setVnfCompute(mp.getVnfCompute());
		// task.setAlias(mp.getName());
		// task.setBlueprint(mp.getBlueprint());
		// task.setChangeType(ChangeType.ADDED);
		// task.setType(ResourceTypeEnum.MONITORING);
		// task.setMonitoringParams(mp.getMonitoringParameters());
		// task.setToscaName(mp.getName());
		// return SystemBuilder.of(new MonitoringUow());
		return null;
	}

	public String registerAlarm(final UUID x, final String performanceMetric, final Double thresholdValue, final Double hysteresis, final String url) {
		return AlarmUtils.registerAlarm(os, props, x, performanceMetric, thresholdValue, hysteresis, url);
	}

	public boolean removeAlarm(final String resource) {
		return AlarmUtils.removeAlarm(os, resource);
	}

}
