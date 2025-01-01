/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.openstack;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.api.telemetry.AlarmService;
import org.openstack4j.api.telemetry.TelemetryService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.telemetry.Alarm;

@ExtendWith(MockitoExtension.class)
class AlarmUtilsTest {
	@Mock
	private OSClientV3 os;
	@Mock
	private TelemetryService telemetry;
	@Mock
	private AlarmService alarm;

	@Test
	void testRegisterBadProps() {
		final Properties props = new Properties();
		assertThrows(OsVimGenericException.class, () -> AlarmUtils.registerAlarm(os, props, null, "props", null, null, null));
	}

	@Test
	void testRegisterFail() {
		final Properties props = new Properties();
		props.put("props", "value");
		assertThrows(OsVimGenericException.class, () -> AlarmUtils.registerAlarm(os, props, null, "props", null, null, null));
	}

	@Test
	void testRegister() {
		mockAlarm();
		final Properties props = new Properties();
		props.put("props", "value,v1");
		final Alarm alarmValue = Mockito.mock(Alarm.class);
		when(alarm.create(any())).thenReturn(alarmValue);
		AlarmUtils.registerAlarm(os, props, UUID.randomUUID(), "props", 123d, null, null);
		assertTrue(true);
	}

	@Test
	void testRemove() {
		mockAlarm();
		final ActionResponse value = Mockito.mock(ActionResponse.class);
		when(alarm.delete(any())).thenReturn(value);
		AlarmUtils.removeAlarm(os, null);
		assertTrue(true);
	}

	private void mockAlarm() {
		when(os.telemetry()).thenReturn(telemetry);
		when(telemetry.alarms()).thenReturn(alarm);
	}
}
