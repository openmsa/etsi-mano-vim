package com.ubiqube.etsi.mano.vim.k8s.conn;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@ToString
public class OpenIdAuthInfo {
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String clientId;
	private String clientSecret;
	@Pattern(regexp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")
	private String remoteServerCertificate;
	private boolean skipCertificateVerification;
}
