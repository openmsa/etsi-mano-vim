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

import java.util.List;
import java.util.function.Function;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.jspecify.annotations.NonNull;

public interface K8sExecutor {

	<R extends HasMetadata> R create(Config k8sCfg, Function<KubernetesClient, R> func);

	List<StatusDetails> delete(Config k8sConfig, final Function<KubernetesClient, List<StatusDetails>> func);

	void waitForClusterDelete(final Config k8sCfg, final HasMetadata obj);

	void waitForClusterCreate(final Config k8sCfg, final HasMetadata obj);

	<R extends HasMetadata> R get(Config k8sCfg, Function<KubernetesClient, R> func);

	@NonNull
	List<HasMetadata> apply(Config k8sCfg, String x);

	HasMetadata createOrPatch(final Config k8sCfg, final HasMetadata hasmetadata);

	List<Object> apply(Config cfg, List<String> x);
}
