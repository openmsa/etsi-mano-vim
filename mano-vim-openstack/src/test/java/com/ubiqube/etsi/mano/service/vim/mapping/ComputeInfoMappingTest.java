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
package com.ubiqube.etsi.mano.service.vim.mapping;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.openstack4j.openstack.compute.domain.NovaServer;

import com.ubiqube.etsi.mano.service.vim.ComputeInfo;

import uk.co.jemos.podam.api.PodamFactoryImpl;

class ComputeInfoMappingTest {
	private final ComputeInfoMapping mapper = Mappers.getMapper(ComputeInfoMapping.class);
	private final PodamFactoryImpl podam;

	public ComputeInfoMappingTest() {
		podam = new PodamFactoryImpl();
		podam.getStrategy().setDefaultNumberOfCollectionElements(1);
	}

	@Test
	void testNull() {
		assertNull(mapper.mapServerInfo(null));
		assertNull(mapper.mapServerStatus(null));
	}

	@Test
	void test() {
		NovaServer pojo = podam.manufacturePojo(NovaServer.class);
		assertNotNull(mapper.mapServerInfo(pojo));
	}

	@ParameterizedTest
	@EnumSource(org.openstack4j.model.compute.Server.Status.class)
	void testEnum(final org.openstack4j.model.compute.Server.Status st) throws NoSuchFieldException, IllegalAccessException {
		NovaServer pojo = podam.manufacturePojo(NovaServer.class);
		setStatus(pojo, st);
		ComputeInfo r = mapper.mapServerInfo(pojo);
		assertNotNull(r);
	}

	public static void setStatus(final Object obj, final Enum<?> status) throws NoSuchFieldException, IllegalAccessException {
		Field statusField = obj.getClass().getDeclaredField("status");
		statusField.setAccessible(true);
		statusField.set(obj, status);
	}
}
