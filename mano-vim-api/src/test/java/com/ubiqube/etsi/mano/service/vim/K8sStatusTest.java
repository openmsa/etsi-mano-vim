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
package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.service.vim.K8sStatus.K8sStatusBuilder;

class K8sStatusTest {

	@Test
	void test() {
		TestBean.testClass(K8sStatus.class);
	}

	@Test
	void testBuilder() {
		final K8sStatusBuilder b = K8sStatus.builder()
				.apiAddress(null)
				.masterAddresses(null)
				.status(null);
		assertNotNull(b.toString());
		assertNotNull(b.build());
	}
}
