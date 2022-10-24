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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.microbean.helm.ReleaseManager;
import org.microbean.helm.Tiller;
import org.microbean.helm.chart.URLChartLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ubiqube.etsi.mano.service.vim.VimException;

import hapi.chart.ChartOuterClass.Chart;
import hapi.chart.ChartOuterClass.Chart.Builder;
import hapi.release.ReleaseOuterClass.Release;
import hapi.services.tiller.Tiller.InstallReleaseRequest;
import hapi.services.tiller.Tiller.InstallReleaseResponse;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class TillerClient {

	private static final Logger LOG = LoggerFactory.getLogger(TillerClient.class);

	private final DefaultKubernetesClient client;
	private final Tiller tiller;

	private final String name;
	private static final long TIMEOUT = 300L;

	private TillerClient(final Config config, final String name) {
		this.client = new DefaultKubernetesClient(config);
		this.name = name;
		try {
			this.tiller = new Tiller(client, "magnum-tiller");
		} catch (final MalformedURLException e) {
			throw new VimException(e);
		}
	}

	private static ConfigBuilder getBaseBuilder(final URL url, final String ca) {
		return new ConfigBuilder()
				.withMasterUrl(url.toString())
				.withCaCertData(ca);
	}

	/**
	 *
	 * @param url
	 * @param ca   base64 String of the DER
	 * @param crt  base64 String of the DER
	 * @param uk   Full PEM key
	 * @param name
	 * @return
	 */
	public static TillerClient ofCerts(final URL url, final byte[] ca, final byte[] crt, final String uk, final String name) {
		final Config c = getBaseBuilder(url, new String(ca))
				.withClientCertData(new String(crt))
				.withClientKeyData(uk)
				.build();
		return new TillerClient(c, name);
	}

	public static TillerClient ofToken(final URL url, final String ca, final String username, final String token, final String name) {
		final Config c = getBaseBuilder(url, ca)
				.withUsername(username)
				.withOauthToken(token)
				.build();
		return new TillerClient(c, name);
	}

	public String deploy(final File file) {
		try (final ReleaseManager releaseManager = new ReleaseManager(tiller)) {
			final Chart.Builder chart = getChart(file);
			final InstallReleaseRequest.Builder requestBuilder = InstallReleaseRequest.newBuilder();
			requestBuilder.setTimeout(TIMEOUT);
			requestBuilder.setName(name); // Set the Helm release name
			requestBuilder.setWait(true); // Wait for Pods to be ready
			requestBuilder.setNamespace(UUID.randomUUID().toString());
			final Future<InstallReleaseResponse> releaseFuture = releaseManager.install(requestBuilder, chart);
			assert releaseFuture != null;
			final Release release = releaseFuture.get().getRelease();
			LOG.debug("release => {}", release.getInfo().getStatus().getCode());
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new VimException(e);
		} catch (final IOException | ExecutionException e) {
			throw new VimException(e);
		}
		return null;
	}

	private static Builder getChart(final File file) {
		try (final URLChartLoader chartLoader = new URLChartLoader()) {
			return chartLoader.load(file.toURI().toURL());
		} catch (final IOException e) {
			throw new VimException(e);
		}

	}

}
