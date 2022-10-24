package com.ubiqube.etsi.mano.vim.k8s;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Registry {

	private String name;
	@NotNull
	private String username;
	@NotNull
	private String password;
}
