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
package com.ubiqube.etsi.mano.openstack;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.telemetry.Alarm;
import org.openstack4j.model.telemetry.Alarm.AggregationMethod;
import org.openstack4j.model.telemetry.Alarm.ComparisonOperator;
import org.openstack4j.model.telemetry.Alarm.Type;
import org.openstack4j.openstack.telemetry.domain.CeilometerAlarm;
import org.openstack4j.openstack.telemetry.domain.CeilometerAlarm.CeilometerGnocchiResourcesThresholdRule;

import com.ubiqube.etsi.mano.service.vim.Metric;
import com.ubiqube.etsi.mano.service.vim.MetricFunction;

public class AlarmUtils {
	private AlarmUtils() {
		// Nothing.
	}

	public static String registerAlarm(final OSClientV3 os, final Properties props, final UUID x, final String performanceMetric, final Double thresholdValue, final Double hysteresis, final String url) {
		final CeilometerGnocchiResourcesThresholdRule rule = new CeilometerAlarm.CeilometerGnocchiResourcesThresholdRule();
		final Metric metric = map(props, performanceMetric);
		rule.setAggregationMethod(AggregationMethod.MEAN);
		rule.setThreshold(thresholdValue.floatValue());
		rule.setComparisonOperator(ComparisonOperator.GT);
		// XXX: If it's not equal to original metric it will fail.
		rule.setEvaluationPeriods(600);
		rule.setMetric(metric.getName());
		rule.setResourceId(x.toString());
		final Alarm alarm = Builders.alarm()
				.alarmActions(Arrays.asList(url))
				.gnocchiResourcesThresholdRule(rule)
				.type(Type.THRESHOLD)
				.build();
		return os.telemetry().alarms().create(alarm).getAlarmId();
	}

	public static boolean removeAlarm(final OSClientV3 os, final String resource) {
		final ActionResponse resp = os.telemetry().alarms().delete(resource);
		return resp.isSuccess();
	}

	private static Metric map(final Properties props, final String x) {
		final String prop = props.getProperty(x);
		if (null == prop) {
			throw new OsVimGenericException("Unable to map monitoring key : " + x);
		}
		final String[] metric = prop.split(",");
		if (metric.length != 2) {
			throw new OsVimGenericException("bad mapping key : " + x + "/" + prop + ". Should have one ','");
		}
		return new Metric(metric[0], MetricFunction.fromValue(metric[1]));
	}
}
