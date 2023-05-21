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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.vim.k8s.InstallMessage.InstallMessageBuilder;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

class InstallMessageTest {

	@Test
	void test() {
		final InstallMessageBuilder i = InstallMessage.builder()
				.k8s(null)
				.name(null)
				.registry(null);
		assertNotNull(i.toString());
		final InstallMessage b = i.build();
		b.setK8s(null);
		b.setName(null);
		b.setRegistry(null);
		b.getK8s();
		b.getName();
		b.getRegistry();
		assertNotNull(b.toString());
		final EqualsVerifierReport rep = EqualsVerifier
				.simple()
				.forClass(b.getClass())
				.suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.SURROGATE_KEY)
				.report();
		System.out.println("" + rep.getMessage());
	}

}
