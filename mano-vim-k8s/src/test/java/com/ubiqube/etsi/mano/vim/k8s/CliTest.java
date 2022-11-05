/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.dao.mano.AuthParamOauth2;
import com.ubiqube.etsi.mano.dao.mano.AuthentificationInformations;
import com.ubiqube.etsi.mano.dao.mano.OAuth2GrantType;
import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;

@SuppressWarnings("static-method")
class CliTest {
	private static final String CRT = """
			-----BEGIN CERTIFICATE-----
			MIIDITCCAgmgAwIBAgIIXGco+gO+pMIwDQYJKoZIhvcNAQELBQAwFTETMBEGA1UE
			AxMKa3ViZXJuZXRlczAeFw0yMjA5MTIxNDUwMjJaFw0yMzA5MTIxNDUwMjVaMDQx
			FzAVBgNVBAoTDnN5c3RlbTptYXN0ZXJzMRkwFwYDVQQDExBrdWJlcm5ldGVzLWFk
			bWluMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs5Zt3yp5KLJmLeSz
			SkBhBO3rkeMgRLnjUCxvHjxNfVZAC+BMrt+ab+FqTQGgvoCDG3TKSuZX2lUcs4wT
			tNDzNYGDfRmLRhcdqGR127BNJNBwIhIcPYpTLSd3ff5KDHiGcksJPXsNxbYIh9So
			Pf7F6aM0XzPwUN0xQEBZnM1Xsa0UgO8rguY57yE0JqX6nCU/fIfv4RafC/D/Vmn8
			OKIJRX3i0h3k5XoML8nkdl20CNNhDOfdfMoLBZ6m9qtUeQlyRyS1Yo+61UjFQ0u4
			842EKVoS+rwKuTh3gtCaZzT/TRuhWWVupV89P+0GTGWH45s4h58hEtBh3xiGpIdZ
			Sq3cAwIDAQABo1YwVDAOBgNVHQ8BAf8EBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUH
			AwIwDAYDVR0TAQH/BAIwADAfBgNVHSMEGDAWgBS2Nu654EYAE+38OP1A2FimJ/bh
			cDANBgkqhkiG9w0BAQsFAAOCAQEAGa7Qph1BS6lfPwIHJKqMwl6hSjFn7/VktgWU
			Qd8eEMNzCaoYwmiplWPhpUhgTOaagRcCkkCs7LR6Aruavv4lTCN+uy2ZXzB7xNIT
			lyJzW8neKLMtOgOcBn8KdbA9tHpbIgLbT//JPoh+FXmK/ZrCoMNvIB7ja6/L22ax
			Ag61wxo2+51Yic9DmFbPuShve+EOFobMwjJOyK64zKAWgIwZsuWBJbRrjyvvg0p6
			oRvAcYwlGzJ8qq52deZbH47+4W+jmUZJj0wnil9+EYP7pyG7pBHJ+C7PMpmAlejH
			S9P4lUwIf55ZEyfkNOOJ64FmaK4WRkkkyEfRtNfOKVU34pN1xA==
			-----END CERTIFICATE-----
									""";
	private static final String CA = """
			-----BEGIN CERTIFICATE-----
			MIIC/jCCAeagAwIBAgIBADANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwprdWJl
			cm5ldGVzMB4XDTIyMDkxMjE0NTAyMloXDTMyMDkwOTE0NTAyMlowFTETMBEGA1UE
			AxMKa3ViZXJuZXRlczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAK5+
			t2YDcMSAWBAhuLVeFHklKZhquiyLn/02C7jR1ix17Kvz9ctq2hKwNI27HIJqtkWJ
			UztMWr/p5CGMFYr4QPbZK+DUKKLAeIIAaehlRB1tPXSz9EonfwyU6ejO/ytyk32r
			E1XJVrL5zQWqt+LAsRDKr2sF+P8t5jDkaaQDTOALYOvO3JqOmjZrbmhhUJCO82Kh
			t8sOQKEIvbpG+RPdT9HvjfN94yKn8LSexzNlFgKuvQVjVxmveMsSgOb0arUetL4h
			ZT7q2clp9K0x+x+EW7rX99nlQE9d+4ayz86WouYt3CqEa77CgLGMEmoF3F6Ew7mv
			01eEtvjNfqm/WHTb7ckCAwEAAaNZMFcwDgYDVR0PAQH/BAQDAgKkMA8GA1UdEwEB
			/wQFMAMBAf8wHQYDVR0OBBYEFLY27rngRgAT7fw4/UDYWKYn9uFwMBUGA1UdEQQO
			MAyCCmt1YmVybmV0ZXMwDQYJKoZIhvcNAQELBQADggEBAGwkewqTgShAf/lne3HC
			YZW0PkH4L4tggq7p5CFAIMQeU7Ttf428aKZv8GQXDwUlZqg5yp2dBHr+zygcztsr
			yANXwv7szUwaRauGb+zU1gY1dPil1IfpVVvyrUV2sl7E25FvTlmin0QtNz6NQFFQ
			oz6XORj+lodyHiqaRSS9D8/27OFEWxK/0Vb94jBLBogYKaSvx2P1lxMSpKevw57z
			J3PcZ0pnHG38gw5Ymyryz4gRn2Ra6+X+IR+hPZWhSgJrrANGmIS/yK5vDsK55tev
			yvx9f/HSqkufj1FHTIrJuHUErHACShf9O27zlPHO4v4LISNG3uAmv0bwWzEqobjP
			els=
			-----END CERTIFICATE-----
			""";

	private static final String PRIV = """
			-----BEGIN RSA PRIVATE KEY-----
			MIIEpQIBAAKCAQEAs5Zt3yp5KLJmLeSzSkBhBO3rkeMgRLnjUCxvHjxNfVZAC+BM
			rt+ab+FqTQGgvoCDG3TKSuZX2lUcs4wTtNDzNYGDfRmLRhcdqGR127BNJNBwIhIc
			PYpTLSd3ff5KDHiGcksJPXsNxbYIh9SoPf7F6aM0XzPwUN0xQEBZnM1Xsa0UgO8r
			guY57yE0JqX6nCU/fIfv4RafC/D/Vmn8OKIJRX3i0h3k5XoML8nkdl20CNNhDOfd
			fMoLBZ6m9qtUeQlyRyS1Yo+61UjFQ0u4842EKVoS+rwKuTh3gtCaZzT/TRuhWWVu
			pV89P+0GTGWH45s4h58hEtBh3xiGpIdZSq3cAwIDAQABAoIBAQCadD1o21G687w3
			qmOc78PBhpK9NRdypGFheeXTnk+TBZQt4qpxGHTt1nQjaMwEK+kWuxqk0nkqmVsG
			/OmR8MfL8+jPtiF3lbf3NPe8i2O9ztsyIltRfl9NDY2SuhqboSkYsXpv+x58NbQe
			hoJwV2eGKsyHONdt5N9QAEhSOLU8ZEvQqYDeOOOMHwTPon2ukuEf9ILI+A/u3O96
			W7xxS72UjauDKOiSBfq4LujwVWCj/PKPmb940utz4dlXltAnWzJpERwHsYt+MJoZ
			Rzdk9V4rFBaA2hdOGmzxUoQrI/0wEGkGvDfxKDlkYOWByip9uWh5gVWwAsxqeBw/
			NaEQQAShAoGBANR/bxTo/rsAo5nvRujeh3t2DI5egGiIXZGl8JG6SduyVu3qIRBc
			tlM8zb1214sc5cJUhI7Q213XYQ+Ep7NvN/IVCjGR0hc6asiVZmajyeMLMfFjLpKd
			qTfgOrQcedehAu0weN8ShS5tFPYjb4uGWVS6IBqRpY6Vyqc0mpyy+ccRAoGBANha
			Pdti2Eb3mVXJQzQpszkBXW7rgO4uYq30cRUoMuUN3LFm3bvKagSdgBr+NJtg5COC
			/P9xABfXfYfHU75VFXq1PxXSheohsgHZMjUAnD345iUaZTbZje+PL3FeZFT6CmRd
			Jj9+uL7jqBH266gKj255YL6CsO4M09AB1gRpvDnTAoGBAJvPjO2yIgWQZGVc04uw
			j3rxhidmF5WOV72SBSF1jO5wh9kPUYsjz/ScoucHmuhAGiAxxLjLX+4hHOQ6CpVB
			Oa5hywWpvHiXtvC4y0r7Ue8OqGIsrVuICyoTWjfyoUyAnRq57gbzF4CBL1uDhCi0
			LBA6IYwZu2EfaGbN5Yh8mzVhAoGBAIEk3OeoCRHTmmPqz4LDoYfVmMAtM7j5V04f
			K6FFbZvsul/q11DRcpdurJlwRFa7b3MDLaINAdE5gGUcRpDGmRu2NQIBuI4ZrvgL
			fjzFMH6NpFNhfilPk7n77oXtolZXKvzd3YbpTb4khp6yldio9RUsvuL5gEOYxJbV
			gbsAoqqhAoGAUZ6xOwaM3OiWFrwqSOgGHnjjfBytx2KjnFZ4xWRPPo6nT3zA7jfW
			jbsqocCxfHNhB9e1o5FIrvy4t0TcVYVcLj7hlqNcAc+unCGkH6/kCPUAH/rbnsiV
			mXHt8+0vE9Iz7f1SbwPVhio6/HJgG6cQbb+kWiJfZgu1wN06ZLVzpvo=
			-----END RSA PRIVATE KEY-----
									""";

	private Servers createServers() {
		return Servers.builder()
				.authentification(AuthentificationInformations.builder()
						.authParamOauth2(AuthParamOauth2.builder()
								.clientSecret("RJuAdY7c7iRgoVoqM7nVQtWSqKOGq4oJ")
								.clientId("mano-helm")
								.grantType(OAuth2GrantType.CLIENT_CREDENTIAL)
								.tokenEndpoint("http://mano-auth:8080/auth/realms/mano-realm/protocol/openid-connect/token")
								.build())
						.build())
				.url("http://localhost:8080/")
				.build();
	}

	private K8sServers createK8s() {
		return K8sServers.builder()
				.apiAddress("https://10.31.1.184:6443")
				.caPem(CA)
				.userCrt(CRT)
				.build();
	}

	@Test
	void dummyTest() {
		assertTrue(true);
	}

	void testName() throws Exception {
		final HelmV3WrapperClient cli = new HelmV3WrapperClient();
		final File file = new File("/home/olivier/Downloads/helm/wordpress-0.1.tgz");
		final Servers server = createServers();
		final K8sServers k8s = createK8s();
		cli.deploy(server, k8s, PRIV, file, "deployement-name");
	}

	void testUninstall() throws Exception {
		final HelmV3WrapperClient cli = new HelmV3WrapperClient();
		final Servers server = createServers();
		final K8sServers k8s = createK8s();
		cli.undeploy(server, k8s, PRIV, "deployement-name");
	}
}
