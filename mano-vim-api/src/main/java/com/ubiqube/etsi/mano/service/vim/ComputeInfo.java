package com.ubiqube.etsi.mano.service.vim;

import java.util.Date;

import org.jspecify.annotations.Nullable;

import lombok.Data;

@Data
public class ComputeInfo {

	private String id;
	private String name;
	private ComputeStatus status;
	private String imageId;
	private String flavorId;
	private Date updated;
	private String created;
	@Nullable
	private String taskState;
}
