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
package com.ubiqube.etsi.mano.vim.dummy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Olivier Vignaud
 *
 */
class DummyStorageTest {

	@Test
	void test() {
		final DummyStorage s = new DummyStorage();
		s.createObjectStorage(null);
		s.createStorage(null, null);
		s.deleteObjectStorage(null);
		s.deleteStorage(null);
		s.getImageDetail(null);
		s.getImageList();
		s.getImagesInformations(null);
		s.getSwImageMatching(null);
		s.uploadSoftwareImage(null, null);
		s.getStorage(null);
		assertTrue(true);
	}

}
