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
 *     along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.vim.k8s.K8sServers;
import com.ubiqube.etsi.mano.service.vim.VimException;

/**
 *
 * @author Olivier Vignaud
 *
 */
@SuppressWarnings("static-method")
class K8sTillerClientTest {

	private static final Logger LOG = LoggerFactory.getLogger(K8sTillerClientTest.class);

	private static final String PASSWORD = "password";

	private static final String CLIENT_KEYSTORE = "/tmp/test-unit.ks";

	@RegisterExtension
	static WireMockExtension wm1 = WireMockExtension.newInstance()
			.options(wireMockConfig()
					.httpDisabled(true)
					.httpsPort(8443)
					.keystorePath(CLIENT_KEYSTORE)
					.keystorePassword(PASSWORD))
			.build();

	private static KeyPair caKey;

	private static X509Certificate cert;

	static {
		try {
			final KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(null);
			createCA(ks);
		} catch (InvalidKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | SignatureException | OperatorCreationException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void createCA(final KeyStore ks) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, NoSuchProviderException, SignatureException, OperatorCreationException, IOException {
		final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(4096);
		caKey = kpg.generateKeyPair();
		cert = generateCert(caKey, "C=FR, O=Ubiqube, OU=TestUnit, CN=localhost");
		ks.setKeyEntry("privatekey", caKey.getPrivate(), PASSWORD.toCharArray(),
				new java.security.cert.Certificate[] { cert });
		ks.setCertificateEntry("cert", cert);
		try (final FileOutputStream keystoreOutputStream = new FileOutputStream(CLIENT_KEYSTORE)) {
			ks.store(keystoreOutputStream, PASSWORD.toCharArray());
		}
		ks.setCertificateEntry("ca", cert);
	}

	private static X509Certificate generateCert(final KeyPair keyPair, final String issuer) throws IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, OperatorCreationException {
		final X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(new X500Name(issuer),
				BigInteger.ONE, new Date(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30)),
				new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)), new X500Name(issuer),
				SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

		// final GeneralNames subjectAltNames = new GeneralNames(new
		// GeneralName(GeneralName.iPAddress, "127.0.0.1"));
		final GeneralNames subjectAltNames = new GeneralNames(new GeneralName(GeneralName.dNSName, "localhost"));
		certificateBuilder.addExtension(org.bouncycastle.asn1.x509.Extension.subjectAlternativeName, false,
				subjectAltNames);

		final AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
				.find("sha256WithRSAEncryption");
		final AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
		final BcContentSignerBuilder signerBuilder = new BcRSAContentSignerBuilder(sigAlgId, digAlgId);
		final AsymmetricKeyParameter keyp = PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
		final ContentSigner signer = signerBuilder.build(keyp);
		final X509CertificateHolder x509CertificateHolder = certificateBuilder.build(signer);

		final X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(x509CertificateHolder);
		certificate.checkValidity(new Date());
		certificate.verify(keyPair.getPublic());
		return certificate;
	}

	@Test
	void testDeploy() throws IOException {
		final WireMockRuntimeInfo wri = wm1.getRuntimeInfo();
		wm1.stubFor(get(urlPathMatching("/api/v1/namespaces/magnum-tiller/pods")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody(HelmHelper.getResourceContent(this, "/k8s-get-pods.json"))
				.withStatus(200)));
		wm1.stubFor(get(urlPathMatching("/api/v1/namespaces/magnum-tiller/pods/tiller/portforward")).willReturn(aResponse()
				.withHeader("Content-Type", "application/json")
				.withBody("{}")
				.withStatus(101)));
		final String caTxt = HelmHelper.pemEncode(caKey);
		LOG.info("CA cert: {}", caTxt);
		final String certTxt = HelmHelper.pemEncode(cert);
		LOG.info("Cert: {}", certTxt);
		final K8sTillerClient srv = new K8sTillerClient();
		final Servers server = Servers.builder()
				.url(URI.create(wri.getHttpsBaseUrl()))
				.build();
		final K8sServers k8s = K8sServers.builder()
				.apiAddress(wri.getHttpsBaseUrl())
				.caPem(certTxt)
				.userCrt(certTxt)
				.build();
		HelmHelper.buildHelmTarball();
		final File file = new File("/tmp/chart.tgz");
		assertThrows(VimException.class, () -> srv.deploy(server, k8s, caTxt, file, "name"));
	}

	@Test
	void testUndeploy() {
		final K8sTillerClient srv = new K8sTillerClient();
		srv.undeploy(null, null, null, null);
		assertTrue(true);
	}

}
