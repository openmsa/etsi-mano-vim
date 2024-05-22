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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.vim.k8s.factory.ClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmConfigTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmControlPlaneFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.MachineDeploymentFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackMachineTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.SecretFactory;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api.APIlistPodForAllNamespacesRequest;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.credentials.Authentication;
import io.kubernetes.client.util.credentials.ClientCertificateAuthentication;
import io.x_k8s.cluster.bootstrap.v1beta1.KubeadmConfigTemplate;
import io.x_k8s.cluster.controlplane.v1beta1.KubeadmControlPlane;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackCluster;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackMachineTemplate;
import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.MachineDeployment;

class JavaClientTest {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(JavaClientTest.class);

	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

	private final io.fabric8.kubernetes.client.Config config = new ConfigBuilder()
			.withMasterUrl("https://10.255.3.60:6443")
			.withCaCertData(CERT_AUTH)
			.withClientCertData(CLIENT_CERTIFICATE)
			.withClientKeyData(CLIENT_KEY)
			.withClientKeyAlgo("RSA")
			.build();

	void testName() throws Exception {
		final ApiClient client = Config.defaultClient();
		getAllPods(client);
	}

	private void getAllPods(final ApiClient client) throws ApiException {
		final CoreV1Api api = new CoreV1Api(client);
		final APIlistPodForAllNamespacesRequest pods = api.listPodForAllNamespaces();
		final V1PodList podList = pods.execute();
		podList.getItems().forEach(x -> {
			System.out.println("status: " + x.getStatus());
			System.out.println("spec: " + x.getSpec());
		});
	}

	private static final String CLIENT_CERTIFICATE = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJYmMzK0ZKT2txeWt3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHlOVEExTVRFeE9UUTRNVEphTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXVhWG5KNktkbkhZQWwweFAKYW5iYVVVbDJQc3hxYm9BZHpWcy9JK2ZiL2djK2I3dGp3ZHBXclF0bmoyNWJMenphaW5rMDE1Z2MyNElDVDV5Sgp0MFBpd0lYSUNiVHBJY1lUY3RRZ3IrSElRV1JwbmwvRjI3NERiOEkvcHlyVzI1RzVMQzRIY3BUbFR2dno4TDJSCkpXZ2I0SFJ1UGpuQTFlMGc3RTVMQ0I5STBmd2lHbjg4Tk9JRGViam0yWC90L1lrQTd0amhtM0RLek5NZzNSVSsKUTJ4UXVvVkxoczdUR0NyVEIwZ3FQMXlzNjA3QlNoZjVRVE1HUE11QVdsdjJmZVVRUE1BNkoxQmNIY0pHL0hWOAovWUxobnVaT1hjUkx3akVTc2p0MVBUN2VKSnN6TzllTXF3am9UaXRHZ0ptQmtZdEl0VGk3T0lXRS9SeVo2cFQxCnVORDh5UUlEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95Mwo1akFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBU1M5UTZ5a0VEQjlJcnVMbFVqeGozUURyeWVRd2FOTlVxMy9SCm5qakJ3RERIWnhvNkk4cUs5WjVDbElJZEJCUk9ZNkhCaWpaK1BmQXlySFYzamhOQ2pxQ2pjMGpoM0NNSm01UkYKVDNKZ3ZtTlJVbHQrdnZGZFZWNjBDelhNeHp1VjRjVVlxU2U3OVBNZ3Jlcmh3cTNsSFFxakV1V0Q5VGh5M0tMTgpjVWZ4UVBUYXExaG0zaS9WcEU3b2UwbVRuK05MRVNYQXZoMnhMNGQ5cndmaGtzZU13L0dTSWlvK3pucURxd3poCnVubW43TVZjZDUwKzFpZGM1c0hIaW1jNW9xZkFmUnMrSzFIQ2wxeDkyUVhnWDFSME1nd0w5S2xKdVQ2a1lSeU8KTTlOSDc2d3ljbDd6TFlzcThSZTc1UlBnUkErYUNQaU5VZS8rYmtRZVRGNFpGMEY5RWc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
	private static final String CLIENT_KEY = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcEFJQkFBS0NBUUVBdWFYbko2S2RuSFlBbDB4UGFuYmFVVWwyUHN4cWJvQWR6VnMvSStmYi9nYytiN3RqCndkcFdyUXRuajI1Ykx6emFpbmswMTVnYzI0SUNUNXlKdDBQaXdJWElDYlRwSWNZVGN0UWdyK0hJUVdScG5sL0YKMjc0RGI4SS9weXJXMjVHNUxDNEhjcFRsVHZ2ejhMMlJKV2diNEhSdVBqbkExZTBnN0U1TENCOUkwZndpR244OApOT0lEZWJqbTJYL3QvWWtBN3RqaG0zREt6Tk1nM1JVK1EyeFF1b1ZMaHM3VEdDclRCMGdxUDF5czYwN0JTaGY1ClFUTUdQTXVBV2x2MmZlVVFQTUE2SjFCY0hjSkcvSFY4L1lMaG51Wk9YY1JMd2pFU3NqdDFQVDdlSkpzek85ZU0KcXdqb1RpdEdnSm1Ca1l0SXRUaTdPSVdFL1J5WjZwVDF1TkQ4eVFJREFRQUJBb0lCQVFDd0ZtNkVwN2U3ZWluaApXRjVUYTF4MGthdDRzVWVhZkNiSWErNGR6b1JhRk9EaDllb1BLMXZmVXR2dHJnMExaenNhOFJCd0ZqWDQ5djNlCktXcHV6Qk05N1NicUhoQ3JHa2ZFT1BTd0h3WmxFQ3NtTDAxcXVuWmtBT21EVkJ6cnN2TGl0dDRWanFsYUc1MHMKRVlLazZuVFdsMWtoNklIQjUrTzhnQjZuRzVWa3BaWk5nRWxaOVlkREF1Qnc2aGUyYUtXa3lMN0NKNzJhUFQxRwpZVDIzaEdYY0MycGNPSmRoVmhzNlZIOWpSK3BrcldRR1dYb095K1JPWFJxb3hySmd6U0VpWHFMbmlJVUV3UzQ4Cm9Vak93NWF1VVdyd0Y4OElEYmN2VzEvZjJ5RTllNSt4RjZCSGgxZ1Zxczl6cSttZlBqRVg0dVg3MkFYZ0xyZnUKRTcxOFQ0U2hBb0dCQU9XWkovVGpvQ01xRGZucmYrU2ZEYll0d3pBUGJaOUNlOWVRZXRJOXphQUc4WndxMVpsZApwV0dRRzVESmdqcXh1bGRhMHVrdEtPajkrb2taWFlxczhObUc2QkNhS0hpNlo5NGZscG1TT21maGdReHhZVllLCkZNb1MxcTN6cVdVdlcyaVY2cFdUelhqelZDVDZKeTB3eFlORnRmczJMWTBSV1BqUnA2VElFeTRsQW9HQkFNNysKOURzTFFGYlZOTWhrcTRrS3FrR3VEb1Y2U3lSWmwvaExYWmc5cHhIQ1VzTk5LeVlaNlNrbG1jSndKZk94Vk94egp0UXVRUTJVVlhUcmVQNklqWjE1OGxicTJKc1ltdjNPU01SL1RYL29JV1FFdGpROTNtVXIzTTM2QkdCTW8rSnRHCjN0bXdsT2ovQmgvQ3RUQkRwaDNuYXhwOENkeko5NW4yRkY0UG5ialZBb0dCQUlEcldhdEJNUWJ0czBGVXdkQmUKbXZWamhzanlzTXdQS2JpbDBNRkNvTFl0ai9idmVsYU9aYkY5bUd6WktUM0owK2IvQVpTYW5mRU53MksrRW04dQpvbUtTUkxZbHlYbll1VC9aSHRnR2FyTXZ4U1YyZUlBMlV1d1FYZWo0KzNIc2dIZ2VUY1ZGWThkNVloTU5QM2c1CmZYNHpZM2EyT2llT3lSV1ZxM3QwT0lqZEFvR0FYVjdQMkJwWktWSCsveTI1MnBVKysyV0NFMTk0cWFyb1RBM2kKNkJ0WFUwRnVoNzQxYW5oaG5qZmh4YjFFd1VvS1hxWkswaTUrMkFmdEIvTGNqVXlmQWhYOGpENUIrenZiUEV5RApjREd1cDJCQWxiRndZYnUzSUFPbDdMaGFuYm5yZWs5YmtxSithU013SmY5MEw1TTBTV1RzQVFSV2hvdHI3cno3CnJ2b29VUWtDZ1lCa2p5T0N4bFJEVThuS3M0Y2ZiaDMyQlk0Zjd3bEM2ckh5bFdGSlVPTmdRNGp3ODkvcXhuRTUKcXhqUkYrU0NaL3FCSDhYdzVoY3lyWldQYkdSdGFEakJJaGtuTGlCRGNaWXo0K2pKTjZ1VzRBTUVMVWQwR0N4cApZdGtnd3dpcmo4cTV0ZzBEa3ZWUHJSMkNGdFZDNkV5RFpNbUgzRHVHQ2NkZHR3L0R5UDdLamc9PQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=";
	private static final String CERT_AUTH = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJZUFLMEVxQ29DdHN3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHpOREExTURreE9UUTRNVEZhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUUN3cXpBNHBuWWozSWhZTGxnZkNIbXVKSUpMQzZjTXNjVmNnY004WmFacG44Q09SZXFvS0pTNUswVkUKOFFxRE9KdHVBY2d5RlI3VWx1MzArRDBKV05NM0FyYVk1R3lhcVVWM1JmZEhCLytTT1FPL21PSCtOZTlyd2VUaQpJcmFST01RTTd1NUxLWnhoV2x2YjMxR3duYlRoR1FDdmxrNEdjeEVyb0grdUphajJpa3VycnRvdVM4YnkzMGdQCjVVYTBDQVI0d0MvYStzMGExc3NCVWVBSlhlY0FIaFQvdGpyaFlkTHdlQlc0ZFMwOUhoRyt3RnBoL213WjRoWlQKbVN2dXVkTmorSFQ5RjJudWJwWmV5TGZGcVIzaDNvRXlsSnZaczBydHNPMFdBdzZTa0R2bkFDRmcxaURteGw0dAo1NnJUZE9ZUFllWUsreDhKUE1KUU1NczJGZHlOQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95MzVqQVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ3BZYmw0OVFPVwpnWWdlQUQrR3h3L2JERkhUQitOTzAvK1JVT2kzVTVOZ0Y5d3ZNWGIyMENhNFhBb1haQVdjZ3ByVlJ6QWFQQWZpCmJWNWNSYVVKZTlrbTlGTWdMdWlUaWw4U1RGRFA4N3RrajB1c3JYV2FyOWJWdjAxcFZ0bFRsc0lYM1locjZKRFIKdk1oMkN2WnVMd09TbXh4TGx1YUlaSWFRY0Q0WU13RFU5amxlS05sMHhTbzNreDRxMHBzTzVmcGhsc1VGdmNaTgpIVzNyZDE5TVBDWEdrbzZsSDd4UWRBVDE5SzlHcmkxUW1Md1NkemFnLzczV00zTzZ1U3lPWEV3Um1ZRkNMNS9NClUyZG9FM2g4QlcvM2J2RmIwbVBwUm1tL0ZPWlBlWTdCZ1FkeXYzcEMvYlhOVTExREg3TzFrbHV4ajhXV0RHci8KVTB3RUZIQlFGUVUzCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";

	void testConnection() throws IOException, ApiException {
		final ApiClient client = createClient();
		getAllPods(client);
	}

	private static ApiClient createClient() throws IOException {
		final byte[] cert = Base64.getDecoder().decode(CLIENT_CERTIFICATE);
		final byte[] key = Base64.getDecoder().decode(CLIENT_KEY);
		final byte[] certAuth = Base64.getDecoder().decode(CERT_AUTH);
		final Authentication auth = new ClientCertificateAuthentication(cert, key);
		return ClientBuilder
				.standard(false)
				.setBasePath("https://10.255.3.60:6443")
				.setAuthentication(auth)
				.setCertificateAuthority(certAuth)
				.build();
	}

	void testCreate() throws IOException {
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
			final PodList podList = client.pods().list();
			podList.getItems().forEach(x -> {
				System.out.println("status: " + x.getStatus());
				System.out.println("spec: " + x.getSpec());
			});
		}
	}

	@Test
	void testOpenStackMachineTemplate() throws Exception {
		final OpenStackMachineTemplate o = OpenStackMachineTemplateFactory.createControlPlane("capi-quickstart", "k8s", "rockylinux-9-kube-v1.28.9", "ovi");
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("OpenStackMachineTemplate");
		LOG.info("{}", doc);
		LOG.info("{}", str);
		assertTrue(doc.equals(str));
	}

	@Test
	void testOpenStackCluster() throws Exception {
		final OpenStackCluster o = OpenStackClusterFactory.create("capi-quickstart", "2b9c9c46-62e6-4903-a1ee-b0aca3b6f834", "10.6.0.0/24", List.of("8.8.4.4"));
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("OpenStackCluster");
		LOG.info("{}", doc);
		LOG.info("{}", str);
		assertTrue(doc.equals(str));
	}

	@Test
	void testKubeadmControlPlane() throws Exception {
		final KubeadmControlPlane o = KubeadmControlPlaneFactory.create("capi-quickstart", 3, "v1.29.2", null);
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("KubeadmControlPlane");
		LOG.info("{}", doc);
		LOG.info("{}", str);
		assertTrue(doc.equals(str));
	}

	@Test
	void testMachineDeployment() throws IOException {
		final MachineDeployment o = MachineDeploymentFactory.create("capi-quickstart", 2, 0, "v1.29.2", "nova");
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("MachineDeployment");
		LOG.info("{}", doc);
		LOG.info("{}", str);
		assertTrue(doc.equals(str));
	}

	@Test
	void testCluster() throws IOException {
		final Cluster o = ClusterFactory.create("capi-quickstart", List.of("192.168.0.0/16"), "cluster.local");
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("Cluster");
		LOG.info("{}", doc);
		LOG.info("{}", str);
		assertTrue(doc.equals(str));
	}

	@Test
	void testKubeadmConfigTemplate() throws IOException {
		final KubeadmConfigTemplate o = KubeadmConfigTemplateFactory.create("capi-quickstart", 0);
		final JsonNode str = toObjectNode(o);
		final ObjectNode doc = findDocument("KubeadmConfigTemplate");
		assertTrue(doc.equals(str));
	}

	private JsonNode toObjectNode(final Object o) throws JsonMappingException, JsonProcessingException {
		final String ser = Serialization.asYaml(o);
		return mapper.readTree(ser);
	}

	ObjectNode findDocument(final String name) throws IOException {
		final YAMLFactory yamlFactory = new YAMLFactory();
		final YAMLParser yamlParser = yamlFactory.createParser(new File("src/test/resources/capi/quickstart.yaml"));
		final List<ObjectNode> res = mapper.readValues(yamlParser, new TypeReference<ObjectNode>() {
		}).readAll();
		for (final ObjectNode objectNode : res) {
			final String type = objectNode.get("kind").asText();
			if (name.equals(type)) {
				return objectNode;
			}
		}
		throw new VimException("Could not find: " + name);
	}

	void createSecret() {
		final Secret secret = SecretFactory.create("capi-quickstart", "Y2xvdWRzOgogIHByb2plY3Q6CiAgICBhdXRoOgogICAgICBhdXRoX3VybDogaHR0cDovLzEwLjMxLjEuMTA4OjUwMDAKICAgICAgcGFzc3dvcmQ6IGNjNGFjZTRjZjQ4NDQ1NDAKICAgICAgdXNlcm5hbWU6IGFkbWluCiAgICAgIHVzZXJfZG9tYWluX25hbWU6IERlZmF1bHQKICAgICAgcHJvamVjdF9uYW1lOiBhZG1pbgogICAgICBwcm9qZWN0X2RvbWFpbl9uYW1lOiBEZWZhdWx0CiAgICBpbnRlcmZhY2U6IHB1YmxpYwogICAgcmVnaW9uX25hbWU6IFJlZ2lvbk9uZQo=");
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build()) {
			final Secret res = client.secrets().resource(secret).create();
			LOG.info("{}", res);
		} catch (final KubernetesClientException e) {
			System.out.println("error code: " + e.getCode());
		}
		assertTrue(true);
	}

	@SuppressWarnings("static-method")
	void testYamlQuote() throws JsonMappingException, JsonProcessingException {
		final String a = """
				root:
				  a: openstack:///'{{ instance_id }}'
				  b: '{{ local_hostname }}'
				""";
		final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		final JsonNode res = mapper.readTree(a);
		System.out.println(res.toString());

	}
}
