/**
 *     Copyright (C) 2019-2024 Ubiqube.
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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.vim.k8s.conn.CertificateAuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.conn.K8s;
import com.ubiqube.etsi.mano.vim.k8s.conn.K8s.K8sBuilder;
import com.ubiqube.etsi.mano.vim.k8s.conn.OpenIdAuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.conn.TokenAuthInfo;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

@SuppressWarnings("static-method")
class K8s2Test {

	@Test
	void test() {
		final K8sBuilder k = K8s.builder()
				.apiUrl(null)
				.caData(null)
				.namespace(null);
		assertNotNull(k.toString());
		final K8s k2 = k.build();
		assertNotNull(k2.toString());
		k2.setApiUrl(null);
		k2.setCaData(null);
		k2.setNamespace(null);
		k2.getApiUrl();
		k2.getCaData();
		k2.getNamespace();
		k2.setCertificateAuthInfo(CertificateAuthInfo.builder()
				.clientCertificate(null)
				.clientCertificateKey(null)
				.build());
		k2.setOpenIdAuthInfo(OpenIdAuthInfo.builder()
				.clientId(null)
				.clientSecret(null)
				.password(null)
				.remoteServerCertificate(null)
				.skipCertificateVerification(false)
				.username(null)
				.build());
		k2.setTokenAuthInfo(TokenAuthInfo.builder().token(null).build());
		final EqualsVerifierReport rep = EqualsVerifier
				.simple()
				.forClass(k2.getClass())
				.suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.SURROGATE_KEY)
				.report();
		System.out.println("" + rep.getMessage());
	}

}
