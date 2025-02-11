package com.ubiqube.etsi.mano.service.vim;

import lombok.Data;

@Data
public class VimVolume {
	private String id;
	private String name;
	private VolumeStatus status;
	private int size;

}
