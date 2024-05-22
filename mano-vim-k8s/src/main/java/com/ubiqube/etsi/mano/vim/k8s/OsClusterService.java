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

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.service.vim.VimGenericException;
import com.ubiqube.etsi.mano.vim.k8s.factory.ClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.CommonFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmConfigTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmControlPlaneFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.MachineDeploymentFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackMachineTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.SecretFactory;
import com.ubiqube.etsi.mano.vim.k8s.model.K8sParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsMachineParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsParams;
import com.ubiqube.etsi.mano.vim.k8sexecutor.K8sExecutor;

import io.fabric8.kubernetes.api.model.NamedAuthInfo;
import io.fabric8.kubernetes.api.model.NamedCluster;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.NonDeletingOperation;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.x_k8s.cluster.bootstrap.v1beta1.KubeadmConfigTemplate;
import io.x_k8s.cluster.controlplane.v1beta1.KubeadmControlPlane;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackCluster;
import io.x_k8s.cluster.infrastructure.v1beta1.OpenStackMachineTemplate;
import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.MachineDeployment;

@Service
public class OsClusterService {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(OsClusterService.class);

	private final CloudsManager cm;

	private final K8sExecutor kexec;

	public OsClusterService(final CloudsManager cm, final K8sExecutor kexec) {
		this.cm = cm;
		this.kexec = kexec;
	}

	public void createCluster(final VimConnectionInformation vci, final K8s k8sConfig, final K8sParams params) {
		final Config k8sCfg = toConfig(k8sConfig);
		final String vimConn = cm.vimConnectionToYaml(vci);
		final String vciB64 = Base64.getEncoder().encodeToString(vimConn.getBytes());
		LOG.debug("vci :\n{}", vciB64);
		final Secret secret = SecretFactory.create(params.getClusterName(), vciB64);
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			final Secret res = client.secrets().resource(secret).createOr(NonDeletingOperation::update);
			LOG.info("{}", res.getMetadata().getUid());
		} catch (final KubernetesClientException e) {
			LOG.error("error code: {}", e.getCode());
		}
		//
		final KubeadmConfigTemplate kct = KubeadmConfigTemplateFactory.create(params.getClusterName(), 0);
		kexec.create(k8sCfg, x -> x.resource(kct).createOr(NonDeletingOperation::update));
		//
		final Cluster cluster = ClusterFactory.create(params.getClusterName(), params.getClusterNetworkCidr(), params.getServiceDomain());
		kexec.create(k8sCfg, x -> x.resource(cluster).create());
		//
		final OsParams osParams = params.getOpenStackParams();
		final OsMachineParams osCp = osParams.getControlPlane();
		final OsMachineParams osWork = osParams.getWorker();
		final MachineDeployment md = MachineDeploymentFactory.create(params.getClusterName(), osWork.getReplica(), 0, params.getK8sVersion(), osParams.getDomainZone());
		kexec.create(k8sCfg, x -> x.resource(md).create());
		//
		final KubeadmControlPlane kcp = KubeadmControlPlaneFactory.create(params.getClusterName(), osCp.getReplica(), params.getK8sVersion(), osCp.getIgnition());
		kexec.create(k8sCfg, x -> x.resource(kcp).create());
		//
		final OpenStackCluster oc = OpenStackClusterFactory.create(params.getClusterName(), osParams.getExtNetId(), osParams.getCidr(), osParams.getDns());
		kexec.create(k8sCfg, x -> x.resource(oc).create());
		//
		final OpenStackMachineTemplate osmtCP = OpenStackMachineTemplateFactory.createControlPlane(params.getClusterName(), osCp.getFlavor(), osCp.getImage(), osParams.getSshKey());
		kexec.create(k8sCfg, x -> x.resource(osmtCP).create());
		//
		final OpenStackMachineTemplate osmtMd = OpenStackMachineTemplateFactory.createMd(params.getClusterName(), osWork.getFlavor(), osWork.getImage(), osParams.getSshKey());
		kexec.create(k8sCfg, x -> x.resource(osmtMd).create());
		kexec.waitForClusterCreate(k8sCfg, cluster);
	}

	public void deleteCluster(final K8s k8sConfig, final String namespace, final String clusterName) {
		final Cluster cluster = new Cluster();
		CommonFactory.setNameNamespace(cluster, namespace, clusterName);
		final Config k8sCfg = toConfig(k8sConfig);
		kexec.delete(k8sCfg, x -> x.resource(cluster).delete());
		kexec.waitForClusterDelete(k8sCfg, cluster);
	}

	private static Config toConfig(final K8s k8sConfig) {
		return new ConfigBuilder()
				.withMasterUrl(k8sConfig.getApiUrl())
				.withCaCertData(k8sConfig.getCaData())
				.withClientCertData(k8sConfig.getClientCrt())
				.withClientKeyData(k8sConfig.getClientKey())
				.withClientKeyAlgo("RSA")
				.build();
	}

	public K8s getKubeConfig(final K8s k8sConfig, final String namespace, final String clusterName) {
		final Config k8sCfg = toConfig(k8sConfig);
		final Secret secret = kexec.get(k8sCfg, x -> x.secrets().inNamespace(namespace).withName(clusterName + "-kubeconfig").get());
		if (!"cluster.x-k8s.io/secret".equals(secret.getType())) {
			throw new VimGenericException("Type of expected secret is not correct: " + secret.getType());
		}
		final String rawSecret = secret.getData().get("value");
		final byte[] raw = Base64.getDecoder().decode(rawSecret);
		return rawToK8s(new String(raw));
	}

	@SuppressWarnings("static-method")
	public K8s rawToK8s(final String string) {
		final io.fabric8.kubernetes.api.model.Config cfg = Serialization.unmarshal(string, io.fabric8.kubernetes.api.model.Config.class);
		final NamedCluster cluster = cfg.getClusters().get(0);
		final NamedAuthInfo user = cfg.getUsers().get(0);
		return K8s.builder()
				.apiUrl(cluster.getCluster().getServer())
				.caData(cluster.getCluster().getCertificateAuthorityData())
				.clientCrt(user.getUser().getClientCertificateData())
				.clientKey(user.getUser().getClientKeyData())
				.namespace("default")
				.build();
	}

}
