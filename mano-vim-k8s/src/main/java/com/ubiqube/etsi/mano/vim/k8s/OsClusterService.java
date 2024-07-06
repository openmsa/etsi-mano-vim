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
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.service.vim.VimGenericException;
import com.ubiqube.etsi.mano.vim.k8s.factory.ClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.CommonFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmConfigTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.KubeadmControlPlaneFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.MachineDeploymentFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackClusterFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.OpenStackMachineTemplateFactory;
import com.ubiqube.etsi.mano.vim.k8s.factory.SecretFactory;
import com.ubiqube.etsi.mano.vim.k8s.mapping.K8sMapping;
import com.ubiqube.etsi.mano.vim.k8s.model.K8sParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsMachineParams;
import com.ubiqube.etsi.mano.vim.k8s.model.OsParams;
import com.ubiqube.etsi.mano.vim.k8sexecutor.K8sExecutor;

import io.fabric8.kubernetes.api.model.Context;
import io.fabric8.kubernetes.api.model.NamedAuthInfo;
import io.fabric8.kubernetes.api.model.NamedCluster;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
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
import jakarta.annotation.Nullable;

@Service
public class OsClusterService {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(OsClusterService.class);

	private final CloudsManager cm;

	private final K8sExecutor kexec;

	private final K8sMapping mapper;

	public OsClusterService(final CloudsManager cm, final K8sExecutor kexec, final K8sMapping mapper) {
		this.cm = cm;
		this.kexec = kexec;
		this.mapper = mapper;
	}

	public void createCluster(final VimConnectionInformation vci, final K8s k8sConfig, final K8sParams params) {
		final Config k8sCfg = toConfig(k8sConfig);
		final String vimConn = cm.vimConnectionToYaml(vci);
		final String vciB64 = Base64.getEncoder().encodeToString(vimConn.getBytes());
		LOG.trace("vci :{}", vciB64);
		final Secret secret = SecretFactory.create(params.getClusterName(), vciB64);
		kexec.createOrPatch(k8sCfg, secret);
		//
		final KubeadmConfigTemplate kct = KubeadmConfigTemplateFactory.create(params.getClusterName(), 0);
		kexec.createOrPatch(k8sCfg, kct);
		//
		final Cluster cluster = ClusterFactory.create(params.getClusterName(), params.getClusterNetworkCidr(), params.getServiceDomain());
		kexec.createOrPatch(k8sCfg, cluster);
		//
		final OsParams osParams = params.getOpenStackParams();
		final OsMachineParams osCp = osParams.getControlPlane();
		final OsMachineParams osWork = osParams.getWorker();
		final MachineDeployment md = MachineDeploymentFactory.create(params.getClusterName(), osWork.getReplica(), 0, params.getK8sVersion(), osParams.getDomainZone());
		kexec.createOrPatch(k8sCfg, md);
		//
		final KubeadmControlPlane kcp = KubeadmControlPlaneFactory.create(params.getClusterName(), osCp.getReplica(), params.getK8sVersion(), osCp.getIgnition());
		kexec.createOrPatch(k8sCfg, kcp);
		//
		final OpenStackCluster oc = OpenStackClusterFactory.create(params.getClusterName(), osParams.getExtNetId(), osParams.getCidr(), osParams.getDns());
		kexec.createOrPatch(k8sCfg, oc);
		//
		final OpenStackMachineTemplate osmtCP = OpenStackMachineTemplateFactory.createControlPlane(params.getClusterName(), osCp.getFlavor(), osCp.getImage(), osParams.getSshKey());
		kexec.createOrPatch(k8sCfg, osmtCP);
		//
		final OpenStackMachineTemplate osmtMd = OpenStackMachineTemplateFactory.createMd(params.getClusterName(), osWork.getFlavor(), osWork.getImage(), osParams.getSshKey());
		kexec.createOrPatch(k8sCfg, osmtMd);
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

	public Optional<K8s> getKubeConfig(final K8s k8sConfig, final String namespace, final String clusterName) {
		final Config k8sCfg = toConfig(k8sConfig);
		final Secret secret = kexec.get(k8sCfg, x -> x.secrets().inNamespace(namespace).withName(clusterName + "-kubeconfig").get());
		if (null == secret) {
			return Optional.empty();
		}
		if (!"cluster.x-k8s.io/secret".equals(secret.getType())) {
			throw new VimGenericException("Type of expected secret is not correct: " + secret.getType());
		}
		final String rawSecret = secret.getData().get("value");
		final byte[] raw = Base64.getDecoder().decode(rawSecret);
		return Optional.of(rawToK8s(raw));
	}

	public K8s rawToK8s(final byte[] raw) {
		final String string = new String(raw);
		final io.fabric8.kubernetes.api.model.Config cfg = Serialization.unmarshal(string, io.fabric8.kubernetes.api.model.Config.class);
		final List<NamedContext> ctx = cfg.getContexts();
		if (ctx.size() != 1) {
			throw new VimException("Context have " + ctx.size() + " element, only 1 is accepted.");
		}
		final NamedCluster cluster = cfg.getClusters().get(0);
		final NamedAuthInfo user = cfg.getUsers().get(0);
		return toK8s(cluster, user);
	}

	private K8s toK8s(final NamedCluster cluster, final NamedAuthInfo user) {
		final K8s ret = new K8s();
		mapper.map(user, ret);
		mapper.map(cluster, ret);
		ret.setNamespace("default");
		return ret;
	}

	public K8s fromKubeConfig(final String context, final byte[] bytes) {
		final String string = new String(bytes);
		final io.fabric8.kubernetes.api.model.Config cfg = Serialization.unmarshal(string, io.fabric8.kubernetes.api.model.Config.class);
		final NamedContext optCtx = findContext(cfg, context);
		final Context ctx = optCtx.getContext();
		final String clusName = ctx.getCluster();
		final String userName = ctx.getUser();
		final NamedCluster cluster = findCluster(clusName, cfg.getClusters());
		final NamedAuthInfo user = findUser(userName, cfg.getUsers());
		return toK8s(cluster, user);
	}

	private static NamedContext findContext(final io.fabric8.kubernetes.api.model.Config cfg, final String context) {
		return cfg.getContexts().stream()
				.filter(x -> x.getName().equals(context))
				.findFirst()
				.orElseThrow(() -> new VimException("Unable to find context: " + context));
	}

	private static NamedAuthInfo findUser(final String userName, final List<NamedAuthInfo> list) {
		return list.stream()
				.filter(x -> x.getName().equals(userName))
				.findFirst()
				.orElseThrow(() -> new VimException("Unable to find user: " + userName));
	}

	private static NamedCluster findCluster(final String clusName, final List<NamedCluster> list) {
		return list.stream()
				.filter(x -> x.getName().equals(clusName))
				.findFirst()
				.orElseThrow(() -> new VimException("Unable to find cluster: " + clusName));
	}

	public Object apply(final K8s cluster, final String x) {
		final Config cfg = toConfig(cluster);
		return kexec.apply(cfg, x);
	}

	@Nullable
	public Secret applySecret(final K8s k8sConfig, final Secret secret) {
		final Config k8sCfg = toConfig(k8sConfig);
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			final Secret res = client.secrets().resource(secret).createOr(NonDeletingOperation::update);
			LOG.info("{}", res.getMetadata().getUid());
			return res;
		} catch (final KubernetesClientException e) {
			LOG.error("error code: {}", e.getCode(), e);
		}
		return null;
	}

	@Nullable
	public StorageClass applyStorageClass(final K8s k8sConfig, final StorageClass sc) {
		final Config k8sCfg = toConfig(k8sConfig);
		try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sCfg).build()) {
			final StorageClass res = client.storage().v1().storageClasses().resource(sc).createOr(NonDeletingOperation::update);
			LOG.info("{}", res.getMetadata().getUid());
			return res;
		} catch (final KubernetesClientException e) {
			LOG.error("error code: {}", e.getCode(), e);
		}
		return null;
	}

}
