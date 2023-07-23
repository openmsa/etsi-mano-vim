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

import com.ubiqube.etsi.mano.openstack.AlarmUtils;
import com.ubiqube.etsi.mano.openstack.OsUtils;
import com.ubiqube.etsi.mano.service.vim.mon.VimMonitoring;

public class OpenstackMonitoring implements VimMonitoring {

	private final OSClientV3 os;

	private final Properties props;

	public OpenstackMonitoring(final OSClientV3 os) {
		this.os = os;
		this.props = OsUtils.loadGnocchiMapping();
	}

	@Override
	public String registerAlarm(final UUID x, final String performanceMetric, final Double thresholdValue, final Double hysteresis, final String url) {
		return AlarmUtils.registerAlarm(os, props, x, performanceMetric, thresholdValue, hysteresis, url);
	}

	@Override
	public boolean removeAlarm(final String resource) {
		return AlarmUtils.removeAlarm(os, resource);
	}

}
