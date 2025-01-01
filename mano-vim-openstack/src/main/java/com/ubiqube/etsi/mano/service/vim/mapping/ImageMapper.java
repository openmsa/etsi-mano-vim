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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openstack4j.model.image.v2.Image;

import com.ubiqube.etsi.mano.service.sys.SysImage;

@Mapper
public interface ImageMapper {

	@Mapping(target = "mindisk", source = "minDisk")
	SysImage map(Image img);
}
