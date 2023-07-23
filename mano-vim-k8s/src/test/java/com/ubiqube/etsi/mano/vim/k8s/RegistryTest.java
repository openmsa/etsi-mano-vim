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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.vim.k8s.Registry.RegistryBuilder;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

class RegistryTest {

	@Test
	void test() {
		final RegistryBuilder r = Registry.builder()
				.name(null)
				.password(null)
				.username(null);
		assertNotNull(r.toString());
		final Registry o = r.build();
		o.setName(null);
		o.setPassword(null);
		o.setUsername(null);
		o.getName();
		o.getPassword();
		o.getUsername();
		assertNotNull(o.toString());
		final EqualsVerifierReport rep = EqualsVerifier
				.simple()
				.forClass(o.getClass())
				.suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.SURROGATE_KEY)
				.report();
		System.out.println("" + rep.getMessage());
	}

}
