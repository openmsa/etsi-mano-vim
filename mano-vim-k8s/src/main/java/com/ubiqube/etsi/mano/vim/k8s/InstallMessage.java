package com.ubiqube.etsi.mano.vim.k8s;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstallMessage {
	@NotNull
	private String name;

	@Valid
	@NotNull
	private K8s k8s;

	@Valid
	private Registry registry;

}
