package com.ubiqube.etsi.mano.vim.k8sexecutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.Resource;

class K8sExecutorFabic8ImplTest {

	@Mock
	private KubernetesClient mockClient;
	@Mock
	private Config mockConfig;
	@Mock
	private HasMetadata mockHasMetadata;
	@Mock
	private Resource<HasMetadata> mockResource;

	private NamespaceableResource mockNamespace;
	@Mock
	private KubernetesResourceList<HasMetadata> mockList;
	private K8sExecutorFabic8Impl k8sExecutor;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		k8sExecutor = new K8sExecutorFabic8Impl();
	}

	@Test
	void testCreate() {
		when(mockClient.resource(mockHasMetadata)).thenReturn(mockNamespace);
		when(mockResource.createOr(any())).thenReturn(mockHasMetadata);

		HasMetadata result = k8sExecutor.create(mockConfig, client -> mockHasMetadata);
		assertNull(result);
	}

	@Test
	void testCreateOrPatch() {
		when(mockClient.resource(mockHasMetadata)).thenReturn(mockNamespace);
		when(mockResource.createOr(any())).thenReturn(mockHasMetadata);

		HasMetadata result = k8sExecutor.createOrPatch(mockConfig, mockHasMetadata);
		assertNull(result);
	}

	@Test
	void testDelete() {
		List<StatusDetails> mockStatusDetails = List.of();
		when(mockClient.resource(mockHasMetadata)).thenReturn(mockNamespace);
		when(mockResource.delete()).thenReturn(mockStatusDetails);

		List<StatusDetails> result = k8sExecutor.delete(mockConfig, client -> mockStatusDetails);
		assertNotNull(result);
	}

	@Test
	void testGet() {
		when(mockClient.resource(mockHasMetadata)).thenReturn(mockNamespace);
		when(mockResource.get()).thenReturn(mockHasMetadata);

		HasMetadata result = k8sExecutor.get(mockConfig, client -> mockHasMetadata);
		assertNull(result);
	}

	@Test
	void testApply() {
		Config k8sCfg = new Config(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		k8sCfg.setConnectionTimeout(1);
		k8sCfg.setRequestTimeout(1);
		String str = getFile("/k8s-get-pods.yaml");
		List<HasMetadata> r = k8sExecutor.apply(k8sCfg, str);
		assertNotNull(r);
	}

	@Test
	void testApplyList() {
		Config k8sCfg = new Config(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		k8sCfg.setConnectionTimeout(1);
		k8sCfg.setRequestTimeout(1);
		String str = getFile("/k8s-get-pods.yaml");
		List<Object> r = k8sExecutor.apply(k8sCfg, List.of(str));
		assertNotNull(r);
	}

	@Test
	void testList() {
		k8sExecutor.list(mockConfig, client -> mockList);
		assertTrue(true);
	}

	public String getFile(final String fileName) {
		try (InputStream is = this.getClass().getResourceAsStream(fileName);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			is.transferTo(baos);
			return new String(baos.toByteArray());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
