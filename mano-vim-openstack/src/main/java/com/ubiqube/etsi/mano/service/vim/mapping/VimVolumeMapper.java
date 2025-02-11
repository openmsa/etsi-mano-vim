package com.ubiqube.etsi.mano.service.vim.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openstack4j.model.storage.block.Volume;

import com.ubiqube.etsi.mano.service.vim.VimVolume;

@Mapper
public interface VimVolumeMapper {
	@Mapping(target = "id", source = "id")
	VimVolume map(Volume volume);
}
