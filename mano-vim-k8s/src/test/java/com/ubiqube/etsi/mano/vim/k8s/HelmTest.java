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
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.Future;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.junit.jupiter.api.Test;
import org.microbean.helm.ReleaseManager;
import org.microbean.helm.Tiller;
import org.microbean.helm.chart.URLChartLoader;

import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.utils.ColumnEncryptor;

import hapi.chart.ChartOuterClass.Chart;
import hapi.release.ReleaseOuterClass.Release;
import hapi.services.tiller.Tiller.InstallReleaseRequest;
import hapi.services.tiller.Tiller.InstallReleaseResponse;
import hapi.services.tiller.Tiller.UninstallReleaseRequest;
import hapi.services.tiller.Tiller.UninstallReleaseResponse;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@SuppressWarnings("static-method")
class HelmTest {
	@Test
	void dummy() {
		assertTrue(true);
	}

	void testName() throws Exception {
		final URI uri = URI.create("file:wordpress-0.1.tgz");
		assert uri != null;
		final URL url = uri.toURL();
		assert url != null;
		Chart.Builder chart = null;
		try (final URLChartLoader chartLoader = new URLChartLoader()) {
			chart = chartLoader.load(url);
		}
		assert chart != null;
		final Config config = new ConfigBuilder()
				.withMasterUrl("https://10.31.1.184:8443/")
				.withCaCertFile("ca.crt")
				.withClientCertFile("client.crt")
				.withClientKeyFile("client.key")
				.build();
		try (final DefaultKubernetesClient client = new DefaultKubernetesClient(config);
				final Tiller tiller = new Tiller(client);
				final ReleaseManager releaseManager = new ReleaseManager(tiller)) {

			final InstallReleaseRequest.Builder requestBuilder = InstallReleaseRequest.newBuilder();
			assert requestBuilder != null;
			requestBuilder.setTimeout(300L);
			requestBuilder.setName("test-charts"); // Set the Helm release name
			requestBuilder.setWait(true); // Wait for Pods to be ready
			requestBuilder.setNamespace(UUID.randomUUID().toString());
			// Install the loaded chart with no user-supplied overrides.
			// To override any values, call the requestBuilder.getValuesBuilder() method,
			// and add values to the resulting Builder, using its setRaw(String) method,
			// which takes a YAML string.
			//
			// Why setRaw(String)? Due to limitations in Tiller itself, Tiller will use
			// only the return value from Config.Builder#getRaw()
			// (https://microbean.github.io/microbean-helm/apidocs/hapi/chart/ConfigOuterClass.Config.Builder.html#getRaw--),
			// which is taken to be a YAML String representing the user-supplied overrides.
			// Tiller ignores any other values-related "getter" methods.
			final Future<InstallReleaseResponse> releaseFuture = releaseManager.install(requestBuilder, chart);
			assert releaseFuture != null;
			final Release release = releaseFuture.get().getRelease();
			System.out.println("release => " + release.getInfo().getStatus().getCode());
			assert release != null;
		}
	}

	void testDelete() throws Exception {
		final Config config = new ConfigBuilder()
				.withMasterUrl("https://10.31.1.184:8443/")
				.withCaCertFile("ca.crt")
				.withClientCertFile("client.crt")
				.withClientKeyFile("client.key")
				// .withNamespace("754c6457-3580-49c9-a7f6-c49b87daa55c")
				.build();
		try (final DefaultKubernetesClient client = new DefaultKubernetesClient(config);
				final Tiller tiller = new Tiller(client);
				final ReleaseManager releaseManager = new ReleaseManager(tiller)) {
			final UninstallReleaseRequest request = UninstallReleaseRequest.newBuilder()
					.setName("test-charts")
					.setPurge(true)
					.build();
			final Future<UninstallReleaseResponse> ret = releaseManager.uninstall(request);
			final UninstallReleaseResponse obj = ret.get();
			System.out.println("" + obj.getInfo() + "  " + obj);
		}
	}

	void testFullCrypto() {
		final Config config = new ConfigBuilder()
				.withMasterUrl("https://10.31.1.49:6443/")
				.withCaCertFile("ca-inari.crt")
				.withClientCertFile("client-inari.crt")
				.withClientKeyFile("client-inari.pem")
				.build();
		try (final DefaultKubernetesClient client = new DefaultKubernetesClient(config)) {
			final PodList podList = client.inAnyNamespace().pods().list();
			podList.getItems().forEach(x -> System.out.println("" + x.getSpec().getNodeName()));
		}
	}

	void testFullCrypto001() {
		final Config config = new ConfigBuilder()
				.withMasterUrl("https://10.31.1.49:6443/")
				.withCaCertFile("ca-inari.crt")
				.withOauthToken("gAAAAABilOhftb8qBy6w_513QWgth-KQ2xuAtF28CJhQY_PfgZ8z8nj_GH39xP7xg8binJRZGh447RhPt4BdpWRw09ZsJWngJzbS6xMPOMYG--50ZJahS2Qb4HevPkupFi0Dn1435JBIovKQgM2kEVmBrNQS4fnQkJfETwnkPWgZYsYi5q7NeaM")
				.build();
		try (final DefaultKubernetesClient client = new DefaultKubernetesClient(config)) {
			final PodList podList = client.pods().list();
			podList.getItems().forEach(x -> System.out.println("" + x.getSpec()));
		}
	}

	private static final String K8S_PRIV = "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";
	private static final String CA = """
			-----BEGIN CERTIFICATE-----
			MIIC0DCCAbigAwIBAgIQPgJw0qU8QE2dsERjVtMjYTANBgkqhkiG9w0BAQsFADAQ
			MQ4wDAYDVQQDDAVWZHVfMTAeFw0yMjEwMjUwOTAwMDVaFw0yNzEwMjUwOTAwMDVa
			MBAxDjAMBgNVBAMMBVZkdV8xMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC
			AQEAodqkMA1GkiOQVk+nOLMNDMaZVn0ZT1WyZcR2UhtUyJnDq8dqHT/lJnMLCEUz
			pf+/zqk4R0+t68/APpT9JXa9htAGG457jZOvIHU1k//X1XXz0Es00OW9vL7HKn+j
			gWNVZ+7gWOCtlkWlUwOvFXoz8gTH6mfaDfEgXqRUcZKVbxkO6U1NQjhTF5Ft7RP/
			68YQNTgWq/ug7azFwRjcx523wMp2GZyjlUif6KuTFpFG8C7L4T3BK3naSbwVOI3y
			mswBBvMEEH8ne/5f2WnXSocvsK0+Bp9FjGl7TsxVeY1PtaVTPY6kk4uu4glWwBNH
			BZpOYSbTHMPbMoA+0jLv9HxzLQIDAQABoyYwJDASBgNVHRMBAf8ECDAGAQH/AgEA
			MA4GA1UdDwEB/wQEAwICBDANBgkqhkiG9w0BAQsFAAOCAQEAYZwQgHUc31UARKak
			qGRtjgaoAqb9JkaIgnlTksC86H7O0qOpcr8mOafthhxoE0gY8/Z+Guj9qHR9Yw1D
			xCZv+YK++r+0gG3MwmZJMkDVXgpd0k13+S7YyXDFCC/hauuSWuVNpVncSnDgyv+a
			8i13h9nZ4MAQzkMJ/qbR57mwMewIncRI95z1sccq3wO/VTFin6AKPgJ6FxV1QaQj
			KmMONgiQ9y8yeGgiTHBclcfHBmKGrxSm4u2hqOBOYFm7LnT+Ef3ZsMSz8rRT03pi
			VRcfUHYjApstE2ANk6pVXvToa92g0+8i2+HjoxdH9hdA3ig6+1fFbMAdi/LTSrqu
			Pv3P+g==
			-----END CERTIFICATE-----
						""";
	private static final String USER_KEY = """
			-----BEGIN CERTIFICATE-----
			MIIDzDCCArSgAwIBAgIQAVpHxCttQQWuJf5zfNWR7DANBgkqhkiG9w0BAQsFADAQ
			MQ4wDAYDVQQDDAVWZHVfMTAeFw0yMjEwMjUwOTEzMDhaFw0yNzEwMjUwOTEzMDha
			MDQxGTAXBgNVBAMMEGt1YmVybmV0ZXMtYWRtaW4xFzAVBgNVBAoMDnN5c3RlbTpt
			YXN0ZXJzMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAzOWf1gnkfpFt
			y+q6QuwjcCD0ZNm0RRGKGkIl8+KKzYA1q1+95+92fgFLl7BTYTfi0DYjOyN306rw
			MJtq87VjenaeUPlGGeZ3sxTPJVjqU+MG384GsKAgl+fadSXf0TDk+/Ktc8AxKU9e
			H7+AI82l+QHJWtqNTS1QTeXNq3UJV7AKwOor0RB2em71oFskY1Jo4DlYk1CKkA8S
			cUcnvr8mFvub9Vqthf1XiYqEPphr/PQzeJduQandcUNNSRa0Q17KFVPKQq87coJm
			x5ryXv9KEFgyYVL6QGR0jHP6b60y+/nRhzNUoL3bfgIB+AeC7iT/ZwXE4EeOjhaz
			/csYaeXYyz+Ydlxkkdxn1YlNVqdb6tZe+BH8KgVC+DscNSvEo9/v4j2ONn3XeOhj
			xX3BC4GdfEotvNWP1Vm32su6lmkHqG4Ev/CSV+RcELMN64hZuHhd1UFYVEtECZs2
			rRgSsoGsG3dvIoMrH52N9yhDm5Puj0Tw6AqK6s07dzXqiYdtFWmiDU5W0gab5XqJ
			z/zK7483hnqF+cni8k8LGhLWjGZtWrNXCfTe9zuPFkuf6Ky37kWGESgoYcYQa7JC
			sBaq/1/dtNoeLV4pe7PPiN/rbYBHdgPMoOubFeaN207HfdtZQg1fIhJDc5D6h7Fq
			cwFI8VJUOxTEpu7Lm5myHWNDHZMuF1cCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEA
			K6dydoLTB6JpgPFEgO0Mp8+hNkaYb6nmH7caqmIS/PiC3UCIN25wcpzCAM8jjxlJ
			Z9T23/qy5gCFu2bqaN+JWyYgnDlZF2ho9XWcf8drMV59my9U7hQxRwBg84Uv0A3b
			H3zEXcOehMGHSr6jF3yLFvAO18moSUP5Ubp99uI9gfz/myp7z0GWfomnN/Z6zScA
			2hs5umzPAsSALTkZqzHy9RQYdN1o5rT4901AA3tv+Lllzd+zM+8zr/+YpYSXj+P7
			9wujWY6uMlU7RGWvo87yCiyVZ9ArElh9IKSBeVDOETqH6MHo+U/hJoCTB5VU1Dpf
			6aLB6UKh2JWkIIV0GA4mpg==
			-----END CERTIFICATE-----
						""";

	void testApi() throws IOException {
		final ColumnEncryptor ce = new ColumnEncryptor();
		final String privPlain = ce.convertToEntityAttribute(K8S_PRIV);
		System.out.println(privPlain);
		final byte[] ca = convertToDer(CA);
		final byte[] crt = convertToDer(USER_KEY);
		final URL url = new URL("https://10.31.1.43:8443");
		final TillerClient tc = TillerClient.ofCerts(url, Base64.getEncoder().encode(ca), Base64.getEncoder().encode(crt), new String(Base64.getEncoder().encode(privPlain.getBytes())), "test-deployement");
		final File f = new File("/home/olivier/Downloads/helm/wordpress-0.1.tgz");
		tc.deploy(f);
		assertTrue(true);
	}

	private byte[] convertToDer(final String pem) throws IOException {
		final PEMParser ca = new PEMParser(new StringReader(pem));
		final Object obj = ca.readObject();
		if (obj instanceof final X509CertificateHolder x) {
			return x.getEncoded();
		}
		if (obj instanceof final PEMKeyPair kp) {
			return kp.getPrivateKeyInfo().getEncoded();
		}
		if (obj instanceof final SubjectPublicKeyInfo sp) {
			return sp.getEncoded();
		}
		throw new VimException("Unknown class" + obj.getClass().getName());
	}

}
