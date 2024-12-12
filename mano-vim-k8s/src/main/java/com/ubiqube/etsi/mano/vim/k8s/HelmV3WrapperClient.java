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

import java.io.File;
import java.util.Base64;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.vim.k8s.K8sServers;
import com.ubiqube.etsi.mano.service.rest.FluxRest;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.service.vim.k8s.K8sClient;
import com.ubiqube.etsi.mano.vim.k8s.conn.CertificateAuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.conn.K8s;

import reactor.core.publisher.Mono;

@Service
public class HelmV3WrapperClient implements K8sClient {

	@Override
	public String deploy(final Servers server, final K8sServers k8s, final File file, final String name) {
		final FluxRest fr = new FluxRest(server);
		final int size = 16 * 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();
		final WebClient wc = fr.getWebClientBuilder()
				.exchangeStrategies(strategies).build();
		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("config", buildInstallMessage(k8s, k8s.getUserKey(), name), MediaType.APPLICATION_JSON);
		builder.part("file", new FileSystemResource(file), MediaType.APPLICATION_OCTET_STREAM);
		final Mono<Object> res = wc.post()
				.uri(server.getUrl() + "/install")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(builder.build()))
				.exchangeToMono(response -> {
					if (HttpStatus.OK.equals(response.statusCode())) {
						return response.bodyToMono(String.class).thenReturn(response.statusCode());
					}
					throw new VimException("Error uploading file");
				});
		res.block();
		return name;
	}

	private static String buildInstallMessage(final K8sServers k8s, final String userKey, final String name) {
		final InstallMessage im = InstallMessage.builder()
				.k8s(K8s.builder()
						.apiUrl(k8s.getApiAddress())
						.caData(Base64.getEncoder().encodeToString(k8s.getCaPem().getBytes()))
						.certificateAuthInfo(CertificateAuthInfo.builder()
								.clientCertificate(Base64.getEncoder().encodeToString(k8s.getUserCrt().getBytes()))
								.clientCertificateKey(Base64.getEncoder().encodeToString(userKey.getBytes()))
								.build())
						.build())
				.name(name)
				.build();
		final ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(im);
		} catch (final JsonProcessingException e) {
			throw new VimException(e);
		}
	}

	@Override
	public void undeploy(final Servers server, final K8sServers k8s, final String userKey, final String vimResourceId) {
		final FluxRest fr = new FluxRest(server);
		final WebClient wc = fr.getWebClient();
		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("config", buildInstallMessage(k8s, userKey, vimResourceId), MediaType.APPLICATION_JSON);
		final Mono<Object> res = wc.post()
				.uri(server.getUrl() + "/uninstall/" + vimResourceId)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(builder.build()))
				.exchangeToMono(response -> {
					if (HttpStatus.OK.equals(response.statusCode())) {
						return response.bodyToMono(String.class).thenReturn(response.toBodilessEntity());
					}
					throw new VimException("Error uploading file");
				});
		res.block();
	}

}
