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

import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.microbean.helm.ReleaseManager;
import org.microbean.helm.Tiller;
import org.microbean.helm.chart.URLChartLoader;

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

	@Test
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

	@Test
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

	@Test
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
}
