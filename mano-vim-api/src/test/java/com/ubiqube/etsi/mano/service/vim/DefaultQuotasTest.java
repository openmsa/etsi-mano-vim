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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings("static-method")
class DefaultQuotasTest {

	@Test
	void test() {
		TestBean.testClass(DefaultQuotas.class);
	}

	@Test
	void test002() {
		DefaultQuotas dq = new DefaultQuotas();
		dq.setFloatingIpMax(-1);
		assertEquals(Integer.MAX_VALUE, dq.getFloatingFree());
		dq.setRamMax(-1);
		assertEquals(Long.MAX_VALUE, dq.getRamFree());
		dq.setKeyPairsMax(-1);
		assertEquals(Integer.MAX_VALUE, dq.getKeyPairsFree());
		dq.setInstanceMax(-1);
		assertEquals(Integer.MAX_VALUE, dq.getInstanceFree());
		dq.setVcpuMax(-1);
		assertEquals(Integer.MAX_VALUE, dq.getVcpuFree());
	}
}