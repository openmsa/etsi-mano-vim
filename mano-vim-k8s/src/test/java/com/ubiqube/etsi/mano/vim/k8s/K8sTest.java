/**
 *     Copyright (C) 2019-2023 Ubiqube.
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

import java.util.Base64;

import org.junit.jupiter.api.Test;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.ClientCertificateAuthentication;

class K8sTest {
	private final String certData = "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJQ01DRWtQakZmZFF3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TVRBME1UUXhNVFExTkRCYUZ3MHlNakEwTVRReE1UUTFORE5hTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXNCZmIrQjN1cE0yTmxyejIKelhBUXFpTFF1dHR4OGdrSXI3UUd1aHJQblFoMzUrK3ZXTUZKMUp6dWtjQk55eFlYSXJWZlNuSkY4R3Y2bHk3RwpmMXBzRllaS2Vrc0ptd3ZyeEtING5WS09pdEk3SThXd1oxdmlDN0ZHU3pqNTh4OHpqcDkrSDdhT1ZJM05QZXZlCjd3VTF5YkZSdXc2eUU0eEF1MlZHVWtKMGZ6KzhoWVl3amNVRUhSNUo0RVY0UGFjQlhiUEtqeFEwdC9hOXRsRTYKK0t3VFNidkRZMVdpQUluc0RvQ080TDcxaE5rbG5LcEVPZ3dNYkhxNXora0dxQWMyc3ZKKzNYVTI3akRUcWM3Nwo3REc4cE9mbDNOSWdOc2xPUTJSZTZQb0ViK2tJUDRKaGVieG9WaUREMHZtLzNUZkNTRGEySW5RWUQvSG4xakd4CnBQNHRiUUlEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JUZHZ3UmR4elNENlRSMExjdnI2QysxdjhNbgpjVEFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBa2Z6T1F5WkthV0FrRTRuVDZoL3VkSDJ1d21IcS9OYkpHUFVICnNidmp3QVRPeHE2RHFjZzNwSWExVit2Zk9CVy9GNy9WR3BiMXVyMEZkMGdCZnVaeFlFeldQR3hMMXczZXJrU3cKME9Ed09IblBuaGVxaTBscU9FVFZOdm5QVmJPeDFWd0Jka0svcEJPUlRLMnNvenFWVkt6S3pWRnV4OXZ1KzNLVwpaSVpIQlhYdVJFOGZUNzZxRmxQSVY1NDd2WXcwNzJWd2lhZVAydHFVNHpiV3BtUUI1TU1abWhwcU03dnZ5L0hiCkxlRE5udFdxTGNGV3MrQkxtYzl2Q3RRa2Q0ZWF6MFc0UjR3Ui9TSXI0ZE5zdFpXMFczYTViaTNwYXB6dzNxeE8KTnBIbitVMHpYclh6Z3lSNytKWGcyQ2VyYTZpYTY3R2ZLMlgwTVIvMXMwLzdwN2Z6QVE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==";
	private final String keyData = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb3dJQkFBS0NBUUVBc0JmYitCM3VwTTJObHJ6MnpYQVFxaUxRdXR0eDhna0lyN1FHdWhyUG5RaDM1Kyt2CldNRkoxSnp1a2NCTnl4WVhJclZmU25KRjhHdjZseTdHZjFwc0ZZWktla3NKbXd2cnhLSDRuVktPaXRJN0k4V3cKWjF2aUM3RkdTemo1OHg4empwOStIN2FPVkkzTlBldmU3d1UxeWJGUnV3NnlFNHhBdTJWR1VrSjBmeis4aFlZdwpqY1VFSFI1SjRFVjRQYWNCWGJQS2p4UTB0L2E5dGxFNitLd1RTYnZEWTFXaUFJbnNEb0NPNEw3MWhOa2xuS3BFCk9nd01iSHE1eitrR3FBYzJzdkorM1hVMjdqRFRxYzc3N0RHOHBPZmwzTklnTnNsT1EyUmU2UG9FYitrSVA0SmgKZWJ4b1ZpREQwdm0vM1RmQ1NEYTJJblFZRC9IbjFqR3hwUDR0YlFJREFRQUJBb0lCQUNKY1JYdmd1MVJGRjcrMgpGNkoybjRuVFNVOXN5b3NqNENJSXl0YzlyQTFFUGNGd0w1THVCRzRNUVRFSGlTWjdwUUFNekE5R3hRYm95dmo5CkU1WitHbUVJMXplTnRNNGdRVjBaRGQ0RTRmT01taVdjeVpNaGhyWXBYVUlJV0Ixc2xTN2JTRXovM2ozNWZYYjcKczRDSVQ3cVBjeGRCQXRaMW81dnRZdzlpRUVGVDUyMmNSbzN3SGF1bnorL3FNRnpNcm5lUGRBSUgrcVJheGhoRgo2V0FJcys2LzlNclhvdjkxZzhxSU92czliYkdjVnB0QXZvUGwxQTI3Nk11bWJZZnJzRXdBYTM4bnkxTlllVlJ0CkVLWG1qNlV6N0lJUTVrbmsvcHZVUUJsQUpPYWZVTllRZElhemw1dGdSVjhxVUVTNzhWK0dqSXBJRHg2cXVOQzIKbkowNGk0RUNnWUVBeFgzMUk1eS85UHVNRi9wU0R5eDNYUUxqWHdjKzhIY3Q2V2l3aXF1R1FWTEl6RXpOSnJMdAppMWpvbFBROEl5V3plcFd1a3BHZWVSYUlPVUxac29LbFpCVVB5Mm05amVaeGxCOXNjeTRYV3IrKzN0Q0dzemttCkZ0MXYyT05CQnBGR0M2bWtpR05rUllYeW1ETFlIWVNyS2tvQ3h1bU1mQmZsQTE1Y1FWWFJQcmtDZ1lFQTVFTDgKL1FyVW54eFdKWko0VkZrU2x1VzB1UU40SURadExLVW1UUXlsVE9RTnVDeE9FZkZHSnVUcTUxdG9JMWpIME5TMQpnMGdYUVcxOWRqcnJISEpqMGZ0Nm5Rb2MwMU8rQ0pkUEltMW5PVGpFYTBNZUxSMXhGRThUbUJiYWxJUDh3dnZiCk85WXpwOWFyTzVKZFlhb1B0dW1nQ05SbUJRMk52bDBab3RjS0tsVUNnWUVBbTEzNlNxZjRnOXlaWEF1OEh0L1cKajcvRDdIQ2czVUNGU0FqcVNrMmljeVdUZnRZMDF1c1E0ZjF5REMwUVduNUFjb0hyTXJlSXVxNDFRUWNNaU5rcgpFRTlaZTZlV1Rsb3FwR01NU0pqTUdzS2FnR1FBZEdMVFNrMnV2aXdhYmZZLzZ0RHM5YmRyblI1QnVlYldDbkxpCnpZUW9KeUI1T3U3NVplc0lIUVpNZFZFQ2dZQVZBSXppZzNLWXlUU3I5RlFaL3JBVkxrRjRuSGNiRDZidjZxb2gKZ0w1YzJzYmdZVU0xcDJ0Ni9La1liSEtpZmZLMHBqMmxQS2JYSUVuZzdQN2crUzF5RHA5emY1Q3phUXNkSXo3bQp2Z1kzWDUvQzlzUDZCckYyMnVmRW9LV3Y4SXo3SzgwNkxqVkdoaHZ2T1VKYVVCbHVEUVRxaXhCditwVkdKSzdOCkt0Z0dtUUtCZ0NRcnJGYVBWYmExT0ZJK09QcTMzOWpESHNjS1gyWWt2RGxtYkdzMkpCWllwMHdPdW9GMmdnQ04KekVEd2wwTjFDM1NhVXorc0hqZjIwdXZkV2F0MG1qQjhhV1VKNS9iRktFb2dNbURoNjVJalNWU3Zqc0d4R3FQdgp3UHZXdUxRcC9JYnpTZWRNWWcySVdSaHg1T0h4QU9qQ1JjUG41RmtMQkVKQVZQeG9EbjBrCi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tCg==";

	@Test
	void dummy() {
		assertTrue(true);
	}

	void testName() throws Exception {
		final byte[] certificate = Base64.getDecoder().decode(certData);
		final byte[] key = Base64.getDecoder().decode(keyData);
		final ApiClient client = new ClientBuilder()
				.setBasePath("https://10.31.1.184:6443")
				// .setAuthentication(new AccessTokenAuthentication(TOKEN2))
				.setAuthentication(new ClientCertificateAuthentication(certificate, key))
				.setVerifyingSsl(false)
				.build();
		// final ApiClient client =
		// Config.fromUrl("http://10.31.1.184").setBasePath("https://10.31.1.184:6443");
		// final ApiKeyAuth BearerToken = (ApiKeyAuth)
		// client.getAuthentication("BearerToken");
		// BearerToken.setApiKey(TOKEN);
		Configuration.setDefaultApiClient(client);
		final CoreV1Api api = new CoreV1Api(client);
		final V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
		for (final V1Pod item : list.getItems()) {
			System.out.println(item.getMetadata());
		}
	}
}
