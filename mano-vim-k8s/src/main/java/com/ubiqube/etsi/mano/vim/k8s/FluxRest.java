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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriComponentsBuilder;

import com.ubiqube.etsi.mano.dao.mano.AuthParamOauth2;
import com.ubiqube.etsi.mano.dao.mano.AuthentificationInformations;
import com.ubiqube.etsi.mano.dao.mano.cnf.ConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.service.vim.VimException;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.netty.http.client.HttpClient;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class FluxRest {
	private static final String VERSION = "Version";
	private static final Logger LOG = LoggerFactory.getLogger(FluxRest.class);

	private final WebClient webClient;
	private final String rootUrl;
	private final String id = UUID.randomUUID().toString();

	public FluxRest(final Servers server) {
		this.rootUrl = server.getUrl();
		webClient = createWebClient(server);
	}

	public WebClient getWebClient() {
		return webClient;
	}

	public static FluxRest of(final ConnectionInformation ci) {
		final Servers srv = new Servers();
		srv.setAuthentification(ci.getAuthentification());
		srv.setCapabilities(ci.getCapabilities());
		srv.setId(ci.getId());
		srv.setIgnoreSsl(ci.isIgnoreSsl());
		srv.setName(ci.getName());
		srv.setServerStatus(ci.getServerStatus());
		srv.setTupleVersion(ci.getVersion());
		srv.setUrl(ci.getUrl());
		return new FluxRest(srv);
	}

	private WebClient createWebClient(final Servers server) {
		final Builder wcb = WebClient.builder();
		final SslContext sslContext = buildSslContext(server);
		final ClientHttpConnector conn = new ReactorClientHttpConnector(getHttpClient(sslContext));
		wcb.clientConnector(conn);
		createAuthPart(wcb, server.getAuthentification());
		wcb.baseUrl(rootUrl);
		return wcb.build();
	}

	private static SslContext buildSslContext(final Servers server) {
		if (server.isIgnoreSsl()) {
			return bypassAllSsl();
		}
		if (server.getTlsCert() != null) {
			return allowSslOneCert(server.getTlsCert());
		}
		return defaultSslContext();
	}

	private static SslContext buildSslContext(final AuthParamOauth2 oAuth) {
		if (Boolean.TRUE.equals(oAuth.getO2IgnoreSsl())) {
			return bypassAllSsl();
		}
		if (oAuth.getO2AuthTlsCert() != null) {
			return allowSslOneCert(oAuth.getO2AuthTlsCert());
		}
		return defaultSslContext();
	}

	private static SslContext defaultSslContext() {
		try {
			return SslContextBuilder.forClient().build();
		} catch (final SSLException e) {
			throw new VimException(e);
		}
	}

	private void createAuthPart(final Builder wcb, final AuthentificationInformations auth) {
		if (auth == null) {
			return;
		}
		Optional.ofNullable(auth.getAuthParamBasic()).ifPresent(x -> wcb.filter(ExchangeFilterFunctions.basicAuthentication(x.getUserName(), x.getPassword())));
		Optional.ofNullable(auth.getAuthParamOauth2()).ifPresent(x -> {
			final AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
					getRegistration(x.getTokenEndpoint(), x.getClientId(), x.getClientSecret(), "openid", "helm"),
					new InMemoryReactiveOAuth2AuthorizedClientService(getRegistration(x.getTokenEndpoint(), x.getClientId(), x.getClientSecret(), "openid", "helm")));
			Optional.ofNullable(x.getO2IgnoreSsl()).filter(Boolean::booleanValue).ifPresent(y -> authorizedClientManager.setAuthorizedClientProvider(getAuthorizedClientProvider(auth)));
			final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
			oauth2.setDefaultClientRegistrationId(id);
			wcb.filter(oauth2);
		});
	}

	private static ReactiveOAuth2AuthorizedClientProvider getAuthorizedClientProvider(final AuthentificationInformations auth) {
		final SslContext sslContext = buildSslContext(auth.getAuthParamOauth2());
		final ClientHttpConnector httpConnector = new ReactorClientHttpConnector(getHttpClient(sslContext));
		final WebClientReactiveClientCredentialsTokenResponseClient accessTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
		accessTokenResponseClient.setWebClient(WebClient.builder().clientConnector(httpConnector).build());
		return ReactiveOAuth2AuthorizedClientProviderBuilder
				.builder()
				.clientCredentials(c -> c.accessTokenResponseClient(accessTokenResponseClient)).build();
	}

	private static SslContext allowSslOneCert(final String tlsCert) {
		try {
			final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			trustStore.setCertificateEntry(UUID.randomUUID().toString(),
					CertificateFactory.getInstance("X.509")
							.generateCertificate(new ByteArrayInputStream(tlsCert.getBytes())));

			final TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);

			return SslContextBuilder.forClient()
					.trustManager(trustManagerFactory)
					.build();
		} catch (final IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
			throw new VimException(e);
		}
	}

	private static SslContext bypassAllSsl() {
		try {
			return SslContextBuilder.forClient()
					.trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
		} catch (final SSLException e) {
			throw new VimException(e);
		}
	}

	private static HttpClient getHttpClient(final SslContext context) {
		return HttpClient.create().secure(t -> t.sslContext(context));
	}

	private ReactiveClientRegistrationRepository getRegistration(final String tokenUri, final String clientId, final String clientSecret, final String... scope) {
		final ClientRegistration registration = ClientRegistration
				.withRegistrationId(id)
				.tokenUri(tokenUri)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope(scope)
				.build();
		return new InMemoryReactiveClientRegistrationRepository(registration);
	}

	public final <T> ResponseEntity<T> getWithReturn(final URI uri, final Class<T> clazz, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		final Mono<ResponseEntity<T>> resp = makeBaseQuery(uri, HttpMethod.GET, null, map)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(clazz);
		return resp.block();
	}

	public final <T> T get(final URI uri, final Class<T> clazz, final String version) {
		return call(uri, HttpMethod.GET, clazz, version);
	}

	public final <T> ResponseEntity<T> postWithReturn(final URI uri, final Object body, final Class<T> clazz, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		final Mono<ResponseEntity<T>> resp = makeBaseQuery(uri, HttpMethod.POST, body, map)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(clazz);
		return resp.block();
	}

	public final <T> ResponseEntity<T> deleteWithReturn(final URI uri, final Object body, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		final ResponseSpec resp = makeBaseQuery(uri, HttpMethod.DELETE, body, map)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve();
		return (ResponseEntity<T>) resp.toBodilessEntity().block();
	}

	public final <T> T post(final URI uri, final Class<T> clazz, final String version) {
		return call(uri, HttpMethod.POST, clazz, version);
	}

	public final <T> T post(final URI uri, final Object body, final Class<T> clazz, final String version) {
		return call(uri, HttpMethod.POST, body, clazz, version);
	}

	/**
	 *
	 * @param <T>     Return class.
	 * @param uri     URI to call.
	 * @param body    The body as an {@link InputStream}.
	 * @param clazz   Return Class.
	 * @param version MANO Version null other wise.
	 * @return
	 */
	public final <T> T put(final URI uri, final InputStream body, final Class<T> clazz, final String contentType) {
		return innerCall(uri, HttpMethod.PUT, body, clazz, Map.of("Content-Type", contentType));
	}

	public final <T> T delete(final URI uri, final Class<T> clazz, final String version) {
		return call(uri, HttpMethod.DELETE, clazz, version);
	}

	public final <T> T call(final URI uri, final HttpMethod method, final Class<T> clazz, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		return innerCall(uri, method, null, clazz, map);
	}

	public final <T> T call(final URI uri, final HttpMethod method, final Object body, final Class<T> clazz, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		return innerCall(uri, method, body, clazz, map);
	}

	public <T> T get(final URI uri, final ParameterizedTypeReference<T> myBean, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		final Mono<ResponseEntity<T>> resp = makeBaseQuery(uri, HttpMethod.GET, null, map)
				.retrieve()
				.toEntity(myBean);
		return getBlockingResult(resp, null, Map.of(VERSION, version));
	}

	public UriComponentsBuilder uriBuilder() {
		return UriComponentsBuilder.fromHttpUrl(rootUrl);
	}

	/**
	 *
	 * @param uri     URI to get the content/
	 * @param path    Path to store the temporary file.
	 * @param version Version header to add if needed, null otherwise.
	 */
	public void download(final URI uri, final Path path, final String version) {
		final RequestHeadersSpec<?> wc = webClient
				.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
		if (null != version) {
			wc.header(VERSION, version);
		}
		final Publisher<DataBuffer> dataBufferFlux = wc.retrieve()
				.bodyToFlux(DataBuffer.class);
		DataBufferUtils.write(dataBufferFlux, path, StandardOpenOption.CREATE).block();
	}

	private RequestHeadersSpec<?> makeBaseQuery(final URI uri, final HttpMethod method, final Object requestObject, final Map<String, String> headers) {
		final RequestHeadersSpec<?> wc = webClient
				.mutate()
				.build()
				.method(method)
				.uri(uri)
				.contentType(Optional.ofNullable(headers.get("Content-Type")).map(MediaType::parseMediaType).orElse(MediaType.APPLICATION_JSON));
		if (null != requestObject) {
			if (requestObject instanceof final InputStream is) {
				((RequestBodySpec) wc).body(BodyInserters.fromResource(new InputStreamResource(is)));
			} else {
				((RequestBodySpec) wc).bodyValue(requestObject);
			}
		}
		Optional.ofNullable(headers.get(VERSION)).ifPresent(x -> wc.header(VERSION, x));
		return wc;
	}

	private final <T> T innerCall(final URI uri, final HttpMethod method, final Object requestObject, final Class<T> clazz, final Map<String, String> headers) {
		final Mono<ResponseEntity<T>> resp = makeBaseQuery(uri, method, requestObject, headers)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(clazz);
		return getBlockingResult(resp, clazz, headers);
	}

	private <T> T getBlockingResult(final Mono<ResponseEntity<T>> resp, final Class<T> clazz, final Map<String, String> headers) {
		final ResponseEntity<T> resp2 = resp.block();
		if (null == resp2) {
			return null;
		}
		final Optional<URI> uri = Optional.ofNullable(resp2.getHeaders().getLocation()).filter(x -> !x.toString().isEmpty());
		if (uri.isPresent()) {
			LOG.info("Location: {}", uri);
			return innerCall(uri.get(), HttpMethod.GET, null, clazz, headers);
		}
		return resp2.getBody();
	}

	public void upload(final URI uri, final Path path, final String accept, final String version) {
		final MultiValueMap<String, ?> multipart = fromPath(path, accept);
		upload(uri, multipart, version);
	}

	public void upload(final URI uri, final InputStream is, final String accept, final String version) {
		final MultiValueMap<String, ?> multipart = fromInputStream(is, accept);
		upload(uri, multipart, version);
	}

	public void upload(final URI uri, final MultiValueMap<String, ?> multipartData, final String version) {
		final RequestHeadersSpec<?> wc = webClient.put()
				.uri(uri)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(BodyInserters.fromMultipartData(multipartData));
		if (null != version) {
			wc.header(VERSION, version);
		}
		wc.retrieve()
				.bodyToMono(String.class)
				.block();
	}

	private static MultiValueMap<String, ?> fromPath(final Path path, final String accept) {
		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new FileSystemResource(path), MediaType.valueOf(accept));
		return builder.build();
	}

	private static MultiValueMap<String, ?> fromInputStream(final InputStream is, final String accept) {
		final MultipartBodyBuilder builder = new MultipartBodyBuilder();
		builder.part("file", new InputStreamResource(is), MediaType.valueOf(accept));
		return builder.build();
	}

	public <T> T patch(final URI uri, final Class<T> clazz, final String ifMatch, final Map<String, Object> patch, final String version) {
		final Map<String, String> map = Optional.ofNullable(version).map(x -> Map.of(VERSION, x)).orElseGet(Map::of);
		final RequestHeadersSpec<?> base = makeBaseQuery(uri, HttpMethod.PATCH, patch, map);
		if (ifMatch != null) {
			base.header(HttpHeaders.IF_MATCH, ifMatch);
		}
		final Mono<ResponseEntity<T>> resp = base
				.retrieve()
				.toEntity(clazz);
		return getBlockingResult(resp, null, Map.of(VERSION, version));
	}

	public void doDownload(final String url, final Consumer<InputStream> target, final String version) {
		final ExceptionHandler eh = new ExceptionHandler();
		try (final PipedOutputStream osPipe = new PipedOutputStream();
				final PipedInputStream isPipe = new PipedInputStream(osPipe)) {
			final Flux<DataBuffer> wc = webClient
					.get()
					.uri(url)
					.accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL)
					.header(VERSION, version)
					.retrieve()
					.onRawStatus(i -> i != 200, exepctionFunction(osPipe))
					.bodyToFlux(DataBuffer.class);

			DataBufferUtils.write(wc, osPipe)
					.doFinally(onComplete(osPipe))
					.onErrorResume(Throwable.class, e -> {
						eh.setE(e);
						eh.setMessage(e.getMessage());
						return Mono.error(e);
					})
					.subscribe(DataBufferUtils.releaseConsumer());
			target.accept(isPipe);
			wc.blockLast();
		} catch (final IOException e) {
			LOG.error("", e);
		}
	}

	private static Consumer<SignalType> onComplete(final PipedOutputStream osPipe) {
		return s -> closePipe(osPipe);
	}

	private static Function<ClientResponse, Mono<? extends Throwable>> exepctionFunction(final PipedOutputStream osPipe) {
		return response -> {
			closePipe(osPipe);
			throw new VimException("An error occured." + response.rawStatusCode());
		};
	}

	private static void closePipe(final OutputStream osPipe) {
		try (osPipe) {
			//
		} catch (final IOException e) {
			throw new VimException(e);
		}
	}

}
