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
package com.ubiqube.etsi.mano.vim.dummy;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.ubiqube.etsi.mano.dao.mano.SoftwareImage;
import com.ubiqube.etsi.mano.dao.mano.VnfStorage;
import com.ubiqube.etsi.mano.service.sys.SysImage;
import com.ubiqube.etsi.mano.service.vim.Storage;
import com.ubiqube.etsi.mano.vim.dto.SwImage;

public class DummyStorage implements Storage {

	@Override
	public void deleteStorage(final String resourceId) {
		//

	}

	@Override
	public void deleteObjectStorage(final String resourceId) {
		//

	}

	@Override
	public Optional<SwImage> getSwImageMatching(final SoftwareImage img) {
		return Optional.empty();
	}

	@Override
	public SwImage uploadSoftwareImage(final InputStream is, final SoftwareImage softwareImage) {
		return null;
	}

	@Override
	public String createStorage(final VnfStorage vnfStorage, final String aliasName) {
		return null;
	}

	@Override
	public String createObjectStorage(final VnfStorage vnfStorage) {
		return null;
	}

	@Override
	public SysImage getImagesInformations(final String name) {
		return null;
	}

	@Override
	public List<SwImage> getImageList() {
		return List.of();
	}

	@Override
	public SoftwareImage getImageDetail(final String id) {
		return null;
	}

}
