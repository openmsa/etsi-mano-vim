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
	private static final String CLIENT_CERTIFICATE = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJSGE5MUNUN3BCVTR3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHlOakExTVRJeE5ESXdOREZhTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQWx5Ni9NUXBUU0cxOXBNVGYKYXNTa0NOLzgwM2o3WWd3WGY5bnZmL0IzRVJRSWZ4L29LWVdTMUFnVWJyNExSb0tJWFNGNFRVNG1ZVUlOU2xBdwpncVhocEdEWUZQRzBvb2RJT3ozM3J4S0x1NU9NVHRUbEFDR1lCSkpRMUJSeStqd1lVb0tabFlIbjRIVGg2SVgzCm5NeXBobDNZWGpkeWJZSUJkQjNWOUxwVk1YN0FnT3pINGsxS015UmtSUlVHdlpSSE90WFF2aVZrSzhheHY4WCsKQW9DV2ZVSGxLWERHRXRTK3Boc1ZHUTdXTG9ERVNzdGpSbDZWV1lmSk51YlEvR1lnV0YweHJCc08zNEJJK1k0MQozbWRDeHQwOVkrWFRROWhGK3lmMUg4Y3VqOGFmVjBHcjVGcklMTXFtQkJVczBaeGN0dHJneDlyNGVCaUlreW5wCit1ckg0d0lEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95Mwo1akFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBbitWUjQxUnBuYjlJREthWXlzdk1XbGJvVGNLTW5Lbm9VZ1JVClJURW5NVEp5RENpcXBwU2N6UnMrZWZHVlJqTnIvQmRUMjlGZDByOUVtbHlDa25JNHZQSnZ2alY1NEZqSFR1THgKSUp2cDFEc1NiNjg2OTNnSDZzSTNmOUpVNkF2MjZ4MUlCT1BUVzVuY3R4NFBLd3VBOUZWSytjNlZsTHFxaHBWeQplWm54NGpjN2E1YkNBWFBWNUtXSzRQRzZoYi9FS1pjRjRtb2RhSy81a3orOUlmdi9uLzFPWFcreWg3WWtWYlNMCjd3TENWK0luczIrOFVtNzdTeHpGOTBRcUFiYmdnaDE2eXQxMmQ4UDdVb2Zqa1dkVUdWays0OWtNUHBUL01TQUEKZ0MrTkNwTW93czNJdVJqRzNZeEZTU3o2QnEweVhmd2pDOFlnOXdOWkJ3N2drQ1BodXc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
	private static final String CLIENT_KEY = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb2dJQkFBS0NBUUVBbHk2L01RcFRTRzE5cE1UZmFzU2tDTi84MDNqN1lnd1hmOW52Zi9CM0VSUUlmeC9vCktZV1MxQWdVYnI0TFJvS0lYU0Y0VFU0bVlVSU5TbEF3Z3FYaHBHRFlGUEcwb29kSU96MzNyeEtMdTVPTVR0VGwKQUNHWUJKSlExQlJ5K2p3WVVvS1psWUhuNEhUaDZJWDNuTXlwaGwzWVhqZHliWUlCZEIzVjlMcFZNWDdBZ096SAo0azFLTXlSa1JSVUd2WlJIT3RYUXZpVmtLOGF4djhYK0FvQ1dmVUhsS1hER0V0UytwaHNWR1E3V0xvREVTc3RqClJsNlZXWWZKTnViUS9HWWdXRjB4ckJzTzM0QkkrWTQxM21kQ3h0MDlZK1hUUTloRit5ZjFIOGN1ajhhZlYwR3IKNUZySUxNcW1CQlVzMFp4Y3R0cmd4OXI0ZUJpSWt5bnArdXJINHdJREFRQUJBb0lCQUV2ZWlOSmFoU3NPejRQMQp6TTB1KzhiOGFVdlRKZ3l4VnNXeG9CQ0xvNlV0Y1lvaWdBYitZbzRIdEFGVFhDTTVkS2VRR1docU5MYVFTRTBuCjZ3QlZzT1FtT2FwaEZpNk9SNEI3ZTZZZlg0SjE3RzdQUmxFSGl0dVlkZm5JZlY3NDg1Zk10TGlqNWJMdDFVVTAKbmV2UmhhM0p3TUNmZ014Vjl0Ykg5a3lIaWlONDNDNGREMVkrWnVRZmhhZnNLQlFpaHVyTHB6QWhNM3lURXN6cwpBLzh1RUNtY3NEUEhwR1NINWVLOGNWQ1B6U1hZRGE0bjI4dGwyRDhlUmhjbE5Xc2hNVHZsL0lQa01WNllXNWhQCnMvWS9HQ010Z3pkcWdGdGU5aUJubmF1UjJNbEUyWWt4b25JQ1p1S1V1d3ZLWUk4Z3FQY0FnV3ZxbW1XYXNDRUEKNTlTdGRLa0NnWUVBd0RpSUQwZks4NkZxdys0aUUvdllJOW9hYnArMHR4YlU3QlBHcGkraXl0VTZvakF1L293dApVdjkyZ1VTYXYxOFNXMWtHbTdlek82VE1NV21EVUZlMnpWUjV4ZEEycHNCSFlSWUtLWDEyS1ZoWXdlT0loek5RCk9UWFAvb3dEaHRLT0JZb1RPeFlNS3JtVktwWE9qYnBpanZOdjVNRHBCYnBwMU1FVlNKTjJuNDhDZ1lFQXlWaGgKSVJrZkxBcmdSajg1djlOTVV2a1lObVkwRHlhdjRUWDVSN2V6V3hjV2t0N0xHMTBPS2JHNWNnQnNTc01IdXZYUgozMGU3SVgvd2RXa1B2ckZhSU5NbkRSbTJqUExWYVkrWXlEdDJhcU0xWDM3N0swZkhNQmM1RTUvdkhmRkFKUTl6CmRFbTZQK21xUmNYMDQ2ZG5Fc2pZNlVQV1ZqYzdvU0pjcTZLZHFHMENnWUFIZ1cyV295Z0NrVmY0NUpDQ0ZVQmcKVGdEZ051NDRnL1MyYnlNL2svYVlVdkVpS2gxN3BQK1VFUk9RZ3B4RVFyTVZ6QWtkQlhSRExQRW9NTVdHMnBYeAp1STIwc3FlZnBUeGtSeGNJZmJTaWNxZXNrblRmU3Bxa1VUeVk1T0N5WUFCWnFRV05OaFF0aXVlUExTQk9tbXVFCngyNmtFUUJJVC9vWDY1NG9JbEhzY3dLQmdINENBSytOSUZRWTdta29Nb1Vad1ArQWd5V3dhczQ4b1AvcUhudEMKdlUyNXNsZnlkTnhJTFkvc2VTbWVtQ2RselZXMWVFWXJoektjOWlrVXVsVFdlNWhsYldBbWduZk5sY0E5clJsTApGWDRCYkdxNmRvaU1vRjA1ZTNBTHZNTlpjNUorQy94d0U4N2g5cjA1K1Z2c0xYYTFHZ05LZ0Q1dXpMTzRaSlRKCjdhODFBb0dBVXFud3M3NWthZFVrZmx1K0FjYXJkRUp3d2NRa0JyOTVrUUx5WGtYUGkwY3Y2WVdwNmhiQWljeTMKTmtZL3dRVWFWOXhpVkVqTXFvK2pCbUM3ODYwU0U3em5vMzUySmI4YjBjWTRiWS9YV2YwV1cvVjVxRm5VZFF3cAp0bkdsTVlISmErbHlZYmIwQ2IxT3ZWM0M4V3Jta2ErdXA0M1N1cVFRTzhjSFlhaE1hQm89Ci0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tCg==";
	private static final String CERT_AUTH = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJZUFLMEVxQ29DdHN3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHpOREExTURreE9UUTRNVEZhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUUN3cXpBNHBuWWozSWhZTGxnZkNIbXVKSUpMQzZjTXNjVmNnY004WmFacG44Q09SZXFvS0pTNUswVkUKOFFxRE9KdHVBY2d5RlI3VWx1MzArRDBKV05NM0FyYVk1R3lhcVVWM1JmZEhCLytTT1FPL21PSCtOZTlyd2VUaQpJcmFST01RTTd1NUxLWnhoV2x2YjMxR3duYlRoR1FDdmxrNEdjeEVyb0grdUphajJpa3VycnRvdVM4YnkzMGdQCjVVYTBDQVI0d0MvYStzMGExc3NCVWVBSlhlY0FIaFQvdGpyaFlkTHdlQlc0ZFMwOUhoRyt3RnBoL213WjRoWlQKbVN2dXVkTmorSFQ5RjJudWJwWmV5TGZGcVIzaDNvRXlsSnZaczBydHNPMFdBdzZTa0R2bkFDRmcxaURteGw0dAo1NnJUZE9ZUFllWUsreDhKUE1KUU1NczJGZHlOQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95MzVqQVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ3BZYmw0OVFPVwpnWWdlQUQrR3h3L2JERkhUQitOTzAvK1JVT2kzVTVOZ0Y5d3ZNWGIyMENhNFhBb1haQVdjZ3ByVlJ6QWFQQWZpCmJWNWNSYVVKZTlrbTlGTWdMdWlUaWw4U1RGRFA4N3RrajB1c3JYV2FyOWJWdjAxcFZ0bFRsc0lYM1locjZKRFIKdk1oMkN2WnVMd09TbXh4TGx1YUlaSWFRY0Q0WU13RFU5amxlS05sMHhTbzNreDRxMHBzTzVmcGhsc1VGdmNaTgpIVzNyZDE5TVBDWEdrbzZsSDd4UWRBVDE5SzlHcmkxUW1Md1NkemFnLzczV00zTzZ1U3lPWEV3Um1ZRkNMNS9NClUyZG9FM2g4QlcvM2J2RmIwbVBwUm1tL0ZPWlBlWTdCZ1FkeXYzcEMvYlhOVTExREg3TzFrbHV4ajhXV0RHci8KVTB3RUZIQlFGUVUzCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";

	private final io.fabric8.kubernetes.client.Config config = new ConfigBuilder()
			.withMasterUrl("https://10.255.3.60:6443")
			.withCaCertData(CERT_AUTH)
			.withClientCertData(CLIENT_CERTIFICATE)
			.withClientKeyData(CLIENT_KEY)
			.withClientKeyAlgo("RSA")
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
