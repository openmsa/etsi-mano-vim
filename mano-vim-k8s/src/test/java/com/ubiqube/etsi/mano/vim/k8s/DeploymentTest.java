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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.DaemonSetBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.MetricAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.Resource;

@SuppressWarnings("static-method")
class DeploymentTest {
	private static final String CLIENT_CERTIFICATE = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJrVENDQVRlZ0F3SUJBZ0lJZUNyTzBuaW9VbFl3Q2dZSUtvWkl6ajBFQXdJd0l6RWhNQjhHQTFVRUF3d1kKYXpOekxXTnNhV1Z1ZEMxallVQXhOelE1TmpJNE5ERXhNQjRYRFRJMU1EWXhNVEEzTlRNek1Wb1hEVEkyTURZeApNVEEzTlRNek1Wb3dNREVYTUJVR0ExVUVDaE1PYzNsemRHVnRPbTFoYzNSbGNuTXhGVEFUQmdOVkJBTVRESE41CmMzUmxiVHBoWkcxcGJqQlpNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQTBJQUJJa1RaYUg5WWxtdlU0cXgKdFVhL21ybXFFM2VUVmF6K3EwV1ZSY1pHM1lyQzJBUVczV3plR3BLN1JwZ204Ull6S0h2RmRPc2lySXZsVFBYbwp0SGZTbFRTalNEQkdNQTRHQTFVZER3RUIvd1FFQXdJRm9EQVRCZ05WSFNVRUREQUtCZ2dyQmdFRkJRY0RBakFmCkJnTlZIU01FR0RBV2dCU1hDUmJobmtLbGwyQTNtWjJvSTk3UnVVWnFRVEFLQmdncWhrak9QUVFEQWdOSUFEQkYKQWlFQTB1dEd4SW9pSWtiTDNFMXFXY214bVFDdEo5WngvRW9OM3dOdng3dktiRjRDSUNFN0RKUXRsSmxYaWdaRQpxTWdzU29HZm9MNmQ5Ny9OMjF4bHZQdFRoK05XCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0KLS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJkekNDQVIyZ0F3SUJBZ0lCQURBS0JnZ3Foa2pPUFFRREFqQWpNU0V3SHdZRFZRUUREQmhyTTNNdFkyeHAKWlc1MExXTmhRREUzTkRrMk1qZzBNVEV3SGhjTk1qVXdOakV4TURjMU16TXhXaGNOTXpVd05qQTVNRGMxTXpNeApXakFqTVNFd0h3WURWUVFEREJock0zTXRZMnhwWlc1MExXTmhRREUzTkRrMk1qZzBNVEV3V1RBVEJnY3Foa2pPClBRSUJCZ2dxaGtqT1BRTUJCd05DQUFRS3c4bnBPSzlYOWJQMExsMEVyOUlqemNKbGJibnlsR3lzeFBLZjhiMkkKTTd1RExEdXoyZG5lQXBqU2Q1ck9Va2RxelJjamZkN0ZPK1Avd25LZXBja1hvMEl3UURBT0JnTlZIUThCQWY4RQpCQU1DQXFRd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFRmdRVWx3a1c0WjVDcFpkZ041bWRxQ1BlCjBibEdha0V3Q2dZSUtvWkl6ajBFQXdJRFNBQXdSUUlnTEVNKzFNeTlCd2ZFNmtMR1pkTDBwemN3MkRYUjFSSGQKcHkyNWFubkVtcUlDSVFEZndrYmNoUzU0Wkd5b2J6K1hPSkJYVi9RbnZ3MFdYakhTcWV5YWdqWW9aUT09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";
	private static final String CLIENT_KEY = "LS0tLS1CRUdJTiBFQyBQUklWQVRFIEtFWS0tLS0tCk1IY0NBUUVFSUtGdXN4SGgxUnJDcGVzZDJEalhVcUJwUFNYVlFhWGZQRk53bXpOR0Q0OTZvQW9HQ0NxR1NNNDkKQXdFSG9VUURRZ0FFaVJObG9mMWlXYTlUaXJHMVJyK2F1YW9UZDVOVnJQNnJSWlZGeGtiZGlzTFlCQmJkYk40YQprcnRHbUNieEZqTW9lOFYwNnlLc2krVk05ZWkwZDlLVk5BPT0KLS0tLS1FTkQgRUMgUFJJVkFURSBLRVktLS0tLQo=";
	private static final String CERT_AUTH = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJkekNDQVIyZ0F3SUJBZ0lCQURBS0JnZ3Foa2pPUFFRREFqQWpNU0V3SHdZRFZRUUREQmhyTTNNdGMyVnkKZG1WeUxXTmhRREUzTkRrMk1qZzBNVEV3SGhjTk1qVXdOakV4TURjMU16TXhXaGNOTXpVd05qQTVNRGMxTXpNeApXakFqTVNFd0h3WURWUVFEREJock0zTXRjMlZ5ZG1WeUxXTmhRREUzTkRrMk1qZzBNVEV3V1RBVEJnY3Foa2pPClBRSUJCZ2dxaGtqT1BRTUJCd05DQUFSdWY1WEJvZXFXa2ZBalJiYUVPTGJMSGttOWFOaUtCT1RnL3NqSWh2VmYKdUhES2l6WTQzQnRnK1JCTWFiRTVpVm9kL3QzeUdjYXJjM3dKQ0t1bTlqUitvMEl3UURBT0JnTlZIUThCQWY4RQpCQU1DQXFRd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFRmdRVXVvVllKd0RYWnF1dno0d2NodE9QCkUwWm43MjR3Q2dZSUtvWkl6ajBFQXdJRFNBQXdSUUlnRlhqRU5oV2JFS2w5RHAwcmQ4NFBMbnRzYlhNaXNrTkUKaUlWYlVSZ203YWNDSVFDT01TaUMySnJwVFk1QUlFYnE0UXNqcmpremZiVXBxQUlORml0eFRtUFBwZz09Ci0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";

	private final io.fabric8.kubernetes.client.Config config = new ConfigBuilder()
			.withMasterUrl("capi-k8s-000.mano.dynfi.ubiqube.com:6443")
			.withCaCertData(CERT_AUTH)
			.withClientCertData(CLIENT_CERTIFICATE)
			.withClientKeyData(CLIENT_KEY)
			.withClientKeyAlgo("EC")
			.build();

	@Test
	void testDeployment() {
		final Deployment deployment = new DeploymentBuilder()
				.withNewMetadata()
				.withName("nginx")
				.endMetadata()
				.withNewSpec()
				.withReplicas(1)
				.withNewTemplate()
				.withNewMetadata()
				.addToLabels("app", "nginx")
				.endMetadata()
				.withNewSpec()
				.addNewContainer()
				.withName("nginx")
				.withImage("nginx")
				.addNewPort()
				.withContainerPort(80)
				.endPort()
				.endContainer()
				.endSpec()
				.endTemplate()
				.withNewSelector()
				.addToMatchLabels("app", "nginx")
				.endSelector()
				.endSpec()
				.build();
		assertNotNull(deployment);
	}

	@Test
	void testDaemonSet() {
		final DaemonSet daemon = new DaemonSetBuilder()
				.withNewMetadata()
				.endMetadata()
				.withNewSpec()
				.withNewTemplate()
				.withNewSpec()
				.addNewContainer()
				.withImage("nginx")
				.withName("nginx")
				.addNewPort()
				.withHostPort(1234)
				.endPort()
				.endContainer()
				.endSpec()
				.endTemplate()
				.endSpec()
				.build();
		assertNotNull(daemon);
	}

	@Test
	void testStatefulSet() {
		final StatefulSet daemon = new StatefulSetBuilder()
				.withNewMetadata()
				.withName("repl1")
				.withNamespace("test")
				.endMetadata()
				.withNewSpec()
				.withReplicas(1)
				.withNewTemplate()
				.withNewMetadata()
				.withLabels(new HashMap<>())
				.endMetadata()
				.withNewSpec()
				.addNewContainer()
				.withImage("img1")
				.addNewPort()
				.withContainerPort(80)
				.endPort()
				.endContainer()
				.endSpec()
				.endTemplate()
				.endSpec()
				.withNewStatus()
				.withReplicas(1)
				.endStatus()
				.build();
		assertNotNull(daemon);
	}

	@Test
	void testNodeInfo() {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
			final List<Resource<Node>> lst = client.nodes().resources().toList();
			lst.forEach(x -> {
				final Node res = x.get();
				final Map<String, Quantity> alloc = res.getStatus().getAllocatable();
				System.out.println("> " + res.getMetadata().getName() + ":");
				System.out.println("allo: " + alloc);
			});
		}
		assertTrue(true);
	}

	@Test
	void testNodeInfo2() {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build();
				MetricAPIGroupDSL top = client.top()) {
			final NodeMetricsList lst = top.nodes().metrics();
			lst.getItems().forEach(x -> {
				final Map<String, Quantity> usage = x.getUsage();
				System.out.println(x.getMetadata().getName() + ": cpu=" + usage.get("cpu").getNumericalAmount() + ", mem=" + usage.get("memory").getNumericalAmount());
			});
		}
		assertTrue(true);
	}
}
