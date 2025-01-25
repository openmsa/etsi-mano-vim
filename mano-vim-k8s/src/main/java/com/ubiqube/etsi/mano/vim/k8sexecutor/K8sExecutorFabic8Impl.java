/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.vim.k8sexecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.vim.k8s.event.ClusterCreateEventHandler;
import com.ubiqube.etsi.mano.vim.k8s.event.ClusterDeleteEventHandler;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonDeletingOperation;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.utils.KubernetesSerialization;

@Service
public class K8sExecutorFabic8Impl implements K8sExecutor {
	private static final String ERROR_CODE = "Error code: {}";
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(K8sExecutorFabic8Impl.class);
	private final KubernetesSerialization kubernetesSerialization = new KubernetesSerialization();

	@Override
	public <R extends HasMetadata> R create(final Config k8sCfg, final Function<KubernetesClient, R> func) {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			final R res = func.apply(client);
			LOG.info("Created: {}", res.getMetadata().getUid());
			return res;
		} catch (final KubernetesClientException e) {
			LOG.warn(ERROR_CODE, e.getCode(), e);
		}
		return null;
	}

	@Override
	public HasMetadata createOrPatch(final Config k8sCfg, final HasMetadata hasmetadata1) {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			final HasMetadata res = client.resource(hasmetadata1).createOr(NonDeletingOperation::update);
			LOG.debug("Done creating/update: {}", res.getMetadata().getName());
			return res;
		} catch (final KubernetesClientException e) {
			LOG.error(ERROR_CODE, e.getCode(), e);
		}
		return null;
	}

	@Override
	public List<StatusDetails> delete(final Config k8sConfig, final Function<KubernetesClient, List<StatusDetails>> func) {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
			final List<StatusDetails> res = func.apply(client);
			LOG.info("Deleted: {}", res.size());
			return res;
		} catch (final KubernetesClientException e) {
			LOG.warn(ERROR_CODE, e.getCode(), e);
		}
		return List.of();
	}

	@Override
	public void waitForClusterDelete(final Config k8sCfg, final HasMetadata obj) {
		try (final KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			LOG.info("Setting informer");
			final ClusterDeleteEventHandler event = new ClusterDeleteEventHandler();
			try (final SharedIndexInformer<HasMetadata> inf = client.resource(obj).inform((ResourceEventHandler) event)) {
				LOG.info("Cluster delete, Wating for 5 minutes");
				final boolean isTerminatedSuccessfully = event.await(5, TimeUnit.MINUTES);
				if (!isTerminatedSuccessfully) {
					LOG.error("Time out");
				}
			}
		} catch (final InterruptedException e) {
			LOG.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void waitForClusterCreate(final Config k8sCfg, final HasMetadata obj) {
		try (final KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			LOG.info("Setting informer");
			final ClusterCreateEventHandler event = new ClusterCreateEventHandler();
			try (final SharedIndexInformer<HasMetadata> inf = client.resource(obj).inform((ResourceEventHandler) event)) {
				LOG.info("Cluster create, Wating for 30 minutes");
				final boolean isTerminatedSuccessfully = event.await(30, TimeUnit.MINUTES);
				if (!isTerminatedSuccessfully) {
					LOG.error("Time out");
				}
			}
		} catch (final InterruptedException e) {
			LOG.warn("Interrupted!", e);
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public <R extends HasMetadata> R get(final Config k8sCfg, final Function<KubernetesClient, R> func) {
		try (final KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			return func.apply(client);
		} catch (final KubernetesClientException e) {
			LOG.warn(ERROR_CODE, e.getCode(), e);
		}
		return null;
	}

	@Override
	public List<HasMetadata> apply(final Config k8sCfg, final String str) {
		final Object unmarshalled = kubernetesSerialization.unmarshal(str);
		final Collection<HasMetadata> entities;
		switch (unmarshalled) {
		case final Collection<?> c -> entities = (Collection<HasMetadata>) c;
		case final KubernetesResourceList krl -> entities = krl.getItems();
		default -> entities = List.of((HasMetadata) unmarshalled);
		}
		return entities.stream()
				.map(x -> createOrPatch(k8sCfg, x))
				.toList();
	}

	@Override
	public List<Object> apply(final Config k8sCfg, final List<String> str) {
		final List<Object> ret = new ArrayList<>();
		try (final KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			for (final String string : str) {
				final Object unmarshalled = kubernetesSerialization.unmarshal(string);
				final Collection<HasMetadata> entities;
				switch (unmarshalled) {
				case final Collection<?> c -> entities = (Collection<HasMetadata>) c;
				case final KubernetesResourceList krl -> entities = krl.getItems();
				default -> entities = List.of((HasMetadata) unmarshalled);
				}
				final List<HasMetadata> lst = entities.stream().map((final HasMetadata x) -> {
					final HasMetadata res = client.resource(x).createOr(NonDeletingOperation::update);
					LOG.debug("Done creating/update: {}", res.getMetadata().getName());
					return res;
				}).toList();
				ret.addAll(lst);
			}
		} catch (final KubernetesClientException e) {
			LOG.warn(ERROR_CODE, e.getCode(), e);
		}
		return ret;
	}

	public <T extends HasMetadata> KubernetesResourceList<T> list(final Config k8sCfg, final Function<KubernetesClient, KubernetesResourceList<T>> func) {
		try (final KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			return func.apply(client);
		} catch (final KubernetesClientException e) {
			LOG.warn(ERROR_CODE, e.getCode(), e);
		}
		return null;
	}
}
