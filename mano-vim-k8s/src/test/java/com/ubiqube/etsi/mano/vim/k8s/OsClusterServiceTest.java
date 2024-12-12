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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ubiqube.etsi.mano.dao.mano.InterfaceInfo;
import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.service.vim.VimGenericException;
import com.ubiqube.etsi.mano.vim.k8s.conn.CertificateAuthInfo;
import com.ubiqube.etsi.mano.vim.k8s.conn.K8s;
import com.ubiqube.etsi.mano.vim.k8s.mapping.K8sMapping;
import com.ubiqube.etsi.mano.vim.k8s.model.K8sParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsMachineParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsParams;
import com.ubiqube.etsi.mano.vim.k8sexecutor.K8sExecutorFabic8Impl;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.ClusterStatus;

@ExtendWith(MockitoExtension.class)
class OsClusterServiceTest {
	private static final String CLIENT_CERTIFICATE = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJYmMzK0ZKT2txeWt3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHlOVEExTVRFeE9UUTRNVEphTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXVhWG5KNktkbkhZQWwweFAKYW5iYVVVbDJQc3hxYm9BZHpWcy9JK2ZiL2djK2I3dGp3ZHBXclF0bmoyNWJMenphaW5rMDE1Z2MyNElDVDV5Sgp0MFBpd0lYSUNiVHBJY1lUY3RRZ3IrSElRV1JwbmwvRjI3NERiOEkvcHlyVzI1RzVMQzRIY3BUbFR2dno4TDJSCkpXZ2I0SFJ1UGpuQTFlMGc3RTVMQ0I5STBmd2lHbjg4Tk9JRGViam0yWC90L1lrQTd0amhtM0RLek5NZzNSVSsKUTJ4UXVvVkxoczdUR0NyVEIwZ3FQMXlzNjA3QlNoZjVRVE1HUE11QVdsdjJmZVVRUE1BNkoxQmNIY0pHL0hWOAovWUxobnVaT1hjUkx3akVTc2p0MVBUN2VKSnN6TzllTXF3am9UaXRHZ0ptQmtZdEl0VGk3T0lXRS9SeVo2cFQxCnVORDh5UUlEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95Mwo1akFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBU1M5UTZ5a0VEQjlJcnVMbFVqeGozUURyeWVRd2FOTlVxMy9SCm5qakJ3RERIWnhvNkk4cUs5WjVDbElJZEJCUk9ZNkhCaWpaK1BmQXlySFYzamhOQ2pxQ2pjMGpoM0NNSm01UkYKVDNKZ3ZtTlJVbHQrdnZGZFZWNjBDelhNeHp1VjRjVVlxU2U3OVBNZ3Jlcmh3cTNsSFFxakV1V0Q5VGh5M0tMTgpjVWZ4UVBUYXExaG0zaS9WcEU3b2UwbVRuK05MRVNYQXZoMnhMNGQ5cndmaGtzZU13L0dTSWlvK3pucURxd3poCnVubW43TVZjZDUwKzFpZGM1c0hIaW1jNW9xZkFmUnMrSzFIQ2wxeDkyUVhnWDFSME1nd0w5S2xKdVQ2a1lSeU8KTTlOSDc2d3ljbDd6TFlzcThSZTc1UlBnUkErYUNQaU5VZS8rYmtRZVRGNFpGMEY5RWc9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
	private static final String CLIENT_KEY = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFcEFJQkFBS0NBUUVBdWFYbko2S2RuSFlBbDB4UGFuYmFVVWwyUHN4cWJvQWR6VnMvSStmYi9nYytiN3RqCndkcFdyUXRuajI1Ykx6emFpbmswMTVnYzI0SUNUNXlKdDBQaXdJWElDYlRwSWNZVGN0UWdyK0hJUVdScG5sL0YKMjc0RGI4SS9weXJXMjVHNUxDNEhjcFRsVHZ2ejhMMlJKV2diNEhSdVBqbkExZTBnN0U1TENCOUkwZndpR244OApOT0lEZWJqbTJYL3QvWWtBN3RqaG0zREt6Tk1nM1JVK1EyeFF1b1ZMaHM3VEdDclRCMGdxUDF5czYwN0JTaGY1ClFUTUdQTXVBV2x2MmZlVVFQTUE2SjFCY0hjSkcvSFY4L1lMaG51Wk9YY1JMd2pFU3NqdDFQVDdlSkpzek85ZU0KcXdqb1RpdEdnSm1Ca1l0SXRUaTdPSVdFL1J5WjZwVDF1TkQ4eVFJREFRQUJBb0lCQVFDd0ZtNkVwN2U3ZWluaApXRjVUYTF4MGthdDRzVWVhZkNiSWErNGR6b1JhRk9EaDllb1BLMXZmVXR2dHJnMExaenNhOFJCd0ZqWDQ5djNlCktXcHV6Qk05N1NicUhoQ3JHa2ZFT1BTd0h3WmxFQ3NtTDAxcXVuWmtBT21EVkJ6cnN2TGl0dDRWanFsYUc1MHMKRVlLazZuVFdsMWtoNklIQjUrTzhnQjZuRzVWa3BaWk5nRWxaOVlkREF1Qnc2aGUyYUtXa3lMN0NKNzJhUFQxRwpZVDIzaEdYY0MycGNPSmRoVmhzNlZIOWpSK3BrcldRR1dYb095K1JPWFJxb3hySmd6U0VpWHFMbmlJVUV3UzQ4Cm9Vak93NWF1VVdyd0Y4OElEYmN2VzEvZjJ5RTllNSt4RjZCSGgxZ1Zxczl6cSttZlBqRVg0dVg3MkFYZ0xyZnUKRTcxOFQ0U2hBb0dCQU9XWkovVGpvQ01xRGZucmYrU2ZEYll0d3pBUGJaOUNlOWVRZXRJOXphQUc4WndxMVpsZApwV0dRRzVESmdqcXh1bGRhMHVrdEtPajkrb2taWFlxczhObUc2QkNhS0hpNlo5NGZscG1TT21maGdReHhZVllLCkZNb1MxcTN6cVdVdlcyaVY2cFdUelhqelZDVDZKeTB3eFlORnRmczJMWTBSV1BqUnA2VElFeTRsQW9HQkFNNysKOURzTFFGYlZOTWhrcTRrS3FrR3VEb1Y2U3lSWmwvaExYWmc5cHhIQ1VzTk5LeVlaNlNrbG1jSndKZk94Vk94egp0UXVRUTJVVlhUcmVQNklqWjE1OGxicTJKc1ltdjNPU01SL1RYL29JV1FFdGpROTNtVXIzTTM2QkdCTW8rSnRHCjN0bXdsT2ovQmgvQ3RUQkRwaDNuYXhwOENkeko5NW4yRkY0UG5ialZBb0dCQUlEcldhdEJNUWJ0czBGVXdkQmUKbXZWamhzanlzTXdQS2JpbDBNRkNvTFl0ai9idmVsYU9aYkY5bUd6WktUM0owK2IvQVpTYW5mRU53MksrRW04dQpvbUtTUkxZbHlYbll1VC9aSHRnR2FyTXZ4U1YyZUlBMlV1d1FYZWo0KzNIc2dIZ2VUY1ZGWThkNVloTU5QM2c1CmZYNHpZM2EyT2llT3lSV1ZxM3QwT0lqZEFvR0FYVjdQMkJwWktWSCsveTI1MnBVKysyV0NFMTk0cWFyb1RBM2kKNkJ0WFUwRnVoNzQxYW5oaG5qZmh4YjFFd1VvS1hxWkswaTUrMkFmdEIvTGNqVXlmQWhYOGpENUIrenZiUEV5RApjREd1cDJCQWxiRndZYnUzSUFPbDdMaGFuYm5yZWs5YmtxSithU013SmY5MEw1TTBTV1RzQVFSV2hvdHI3cno3CnJ2b29VUWtDZ1lCa2p5T0N4bFJEVThuS3M0Y2ZiaDMyQlk0Zjd3bEM2ckh5bFdGSlVPTmdRNGp3ODkvcXhuRTUKcXhqUkYrU0NaL3FCSDhYdzVoY3lyWldQYkdSdGFEakJJaGtuTGlCRGNaWXo0K2pKTjZ1VzRBTUVMVWQwR0N4cApZdGtnd3dpcmo4cTV0ZzBEa3ZWUHJSMkNGdFZDNkV5RFpNbUgzRHVHQ2NkZHR3L0R5UDdLamc9PQotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=";
	private static final String CERT_AUTH = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCVENDQWUyZ0F3SUJBZ0lJZUFLMEVxQ29DdHN3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TkRBMU1URXhPVFF6TVRGYUZ3MHpOREExTURreE9UUTRNVEZhTUJVeApFekFSQmdOVkJBTVRDbXQxWW1WeWJtVjBaWE13Z2dFaU1BMEdDU3FHU0liM0RRRUJBUVVBQTRJQkR3QXdnZ0VLCkFvSUJBUUN3cXpBNHBuWWozSWhZTGxnZkNIbXVKSUpMQzZjTXNjVmNnY004WmFacG44Q09SZXFvS0pTNUswVkUKOFFxRE9KdHVBY2d5RlI3VWx1MzArRDBKV05NM0FyYVk1R3lhcVVWM1JmZEhCLytTT1FPL21PSCtOZTlyd2VUaQpJcmFST01RTTd1NUxLWnhoV2x2YjMxR3duYlRoR1FDdmxrNEdjeEVyb0grdUphajJpa3VycnRvdVM4YnkzMGdQCjVVYTBDQVI0d0MvYStzMGExc3NCVWVBSlhlY0FIaFQvdGpyaFlkTHdlQlc0ZFMwOUhoRyt3RnBoL213WjRoWlQKbVN2dXVkTmorSFQ5RjJudWJwWmV5TGZGcVIzaDNvRXlsSnZaczBydHNPMFdBdzZTa0R2bkFDRmcxaURteGw0dAo1NnJUZE9ZUFllWUsreDhKUE1KUU1NczJGZHlOQWdNQkFBR2pXVEJYTUE0R0ExVWREd0VCL3dRRUF3SUNwREFQCkJnTlZIUk1CQWY4RUJUQURBUUgvTUIwR0ExVWREZ1FXQkJTR3lkazdyRkd5ZDc3cjNTL1N1a3FGdy95MzVqQVYKQmdOVkhSRUVEakFNZ2dwcmRXSmxjbTVsZEdWek1BMEdDU3FHU0liM0RRRUJDd1VBQTRJQkFRQ3BZYmw0OVFPVwpnWWdlQUQrR3h3L2JERkhUQitOTzAvK1JVT2kzVTVOZ0Y5d3ZNWGIyMENhNFhBb1haQVdjZ3ByVlJ6QWFQQWZpCmJWNWNSYVVKZTlrbTlGTWdMdWlUaWw4U1RGRFA4N3RrajB1c3JYV2FyOWJWdjAxcFZ0bFRsc0lYM1locjZKRFIKdk1oMkN2WnVMd09TbXh4TGx1YUlaSWFRY0Q0WU13RFU5amxlS05sMHhTbzNreDRxMHBzTzVmcGhsc1VGdmNaTgpIVzNyZDE5TVBDWEdrbzZsSDd4UWRBVDE5SzlHcmkxUW1Md1NkemFnLzczV00zTzZ1U3lPWEV3Um1ZRkNMNS9NClUyZG9FM2g4QlcvM2J2RmIwbVBwUm1tL0ZPWlBlWTdCZ1FkeXYzcEMvYlhOVTExREg3TzFrbHV4ajhXV0RHci8KVTB3RUZIQlFGUVUzCi0tLS0tRU5EIENFUlRJRklDQVRFLS0tLS0K";
	@Mock
	private KubernetesClient client;
	private final K8sMapping mapper = Mappers.getMapper(K8sMapping.class);
	private final K8s k8sConfigMock = K8s.builder()
			.namespace("default")
			.apiUrl("https://10.255.3.60:6443")
			.caData(CERT_AUTH)
			.certificateAuthInfo(CertificateAuthInfo.builder()
					.clientCertificate(CLIENT_CERTIFICATE)
					.clientCertificateKey(CLIENT_KEY)
					.build())
			.build();
	@Mock
	private NamespaceableResource<HasMetadata> namespaceRes;

	OsClusterService createService() {
		return new OsClusterService(new CloudsManager(), new TestExecutor(client), mapper);
	}

	static VimConnectionInformation createVci() {
		final VimConnectionInformation vci = new VimConnectionInformation();
		vci.setId(UUID.randomUUID());
		vci.setVimId(vci.getId().toString());
		final InterfaceInfo ii = new InterfaceInfo();
		ii.setEndpoint("http://10.31.1.108:5000");
		vci.setInterfaceInfo(ii);
		final KeystoneAuthV3 ai = KeystoneAuthV3.builder()
				.username("admin")
				.password("cc4ace4cf4844540")
				.projectId("18b7739d3f294ba5adacb41b42e14e80")
				.projectDomain("Default")
				.userDomain("Default")
				.build();
		vci.setAccessInfo(ai);
		return vci;
	}

	@Test
	void testCreateCluster() {
		final OsClusterService srv = createService();
		final VimConnectionInformation vci = createVci();
		final K8sParams params = K8sParams.builder()
				.clusterName("capi-quickstart")
				.clusterNetworkCidr(List.of("192.168.0.0/16"))
				.k8sVersion("v1.29.2")
				.serviceDomain("cluster.local")
				.openStackParams(OsParams.builder()
						.cidr("10.6.0.0/24")
						.dns(List.of("8.8.4.4"))
						.domainZone("nova")
						.extNetId("2b9c9c46-62e6-4903-a1ee-b0aca3b6f834")
						.sshKey("ovi")
						.controlPlane(OsMachineParams.builder()
								.flavor("k8s")
								.image("rockylinux-9-kube-v1.28.9")
								.replica(3)
								.build())
						.worker(OsMachineParams.builder()
								.flavor("k8s")
								.image("rockylinux-9-kube-v1.28.9")
								.replica(2)
								.build())
						.build())
				.build();
		when(client.resource((HasMetadata) any())).thenReturn(namespaceRes);
		final Cluster cluster = Mockito.mock(Cluster.class);
		when(namespaceRes.get()).thenReturn(cluster);
		final ClusterStatus clusterStatus = Mockito.mock(ClusterStatus.class);
		when(cluster.getStatus()).thenReturn(clusterStatus);
		srv.createCluster(vci, k8sConfigMock, params);
		assertTrue(true);
	}

	@Test
	void deleteCluster() {
		final OsClusterService srv = createService();
		srv.deleteCluster(k8sConfigMock, "default", "capi-quickstart");
		assertTrue(true);
	}

	@Test
	void getKubeConfigBadType() {
		final OsClusterService ocs = createService();
		final MixedOperation<Secret, SecretList, Resource<Secret>> sec = Mockito.mock(MixedOperation.class);
		when(client.secrets()).thenReturn(sec);
		when(sec.inNamespace("default")).thenReturn(sec);
		final Resource<Secret> value = Mockito.mock(Resource.class);
		when(sec.withName("capi-quickstart-kubeconfig")).thenReturn(value);
		final Secret secret = new Secret();
		when(value.get()).thenReturn(secret);
		assertThrows(VimGenericException.class, () -> ocs.getKubeConfig(k8sConfigMock, "default", "capi-quickstart"));
	}

	@Test
	void getKubeConfig() {
		final OsClusterService ocs = createService();
		final MixedOperation<Secret, SecretList, Resource<Secret>> sec = Mockito.mock(MixedOperation.class);
		when(client.secrets()).thenReturn(sec);
		when(sec.inNamespace("default")).thenReturn(sec);
		final Resource<Secret> value = Mockito.mock(Resource.class);
		when(sec.withName("capi-quickstart-kubeconfig")).thenReturn(value);
		final Secret secret = new Secret();
		secret.setType("cluster.x-k8s.io/secret");
		final String r = load("/kubeConfig");
		final String r64 = toBase64(r);
		secret.getData().put("value", r64);
		when(value.get()).thenReturn(secret);
		final Optional<K8s> k = ocs.getKubeConfig(k8sConfigMock, "default", "capi-quickstart");
		assertTrue(k.isPresent());
		System.out.println(k.get());
		// assertThat(k.get()).hasNoNullFieldsOrProperties()
	}

	void testBadGetClusterInfo() {
		final OsClusterService ocs = new OsClusterService(new CloudsManager(), new K8sExecutorFabic8Impl(), mapper);
		final Optional<K8s> res = ocs.getKubeConfig(k8sConfigMock, "default", "bad");
		assertTrue(res.isEmpty());
	}

	@Test
	void testFromKubeConfigBad() {
		final OsClusterService srv = createService();
		final byte[] arr = load("/kubeConfig").getBytes();
		assertThrows(VimException.class, () -> srv.fromKubeConfig("badCtx", arr));
	}

	@Test
	void testFromKubeConfigGood() {
		final OsClusterService srv = createService();
		final byte[] arr = load("/kubeConfig").getBytes();
		final K8s res = srv.fromKubeConfig("capi-quickstart-admin@capi-quickstart", arr);
		assertNotNull(res);
	}

	@Test
	void testApplySingle() {
		final OsClusterService srv = createService();
		srv.apply(k8sConfigMock, load("/capi/quickstart.yaml"));
		assertTrue(true);
	}

	@Test
	void testApplyMulti() {
		final OsClusterService srv = createService();
		final String qs = load("/capi/quickstart.yaml");
		srv.apply(k8sConfigMock, List.of(qs, qs));
		assertTrue(true);
	}

	private static String toBase64(final String r) {
		return Base64.getEncoder().encodeToString(r.getBytes());
	}

	private String load(final String string) {
		try (InputStream r = this.getClass().getResourceAsStream(string)) {
			return new String(r.readAllBytes());
		} catch (final IOException e) {
			throw new VimException(e);
		}
	}
}
