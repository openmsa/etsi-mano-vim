package com.ubiqube.etsi.mano.service.vim.verify;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.network.Subnet;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vim.ResourceTypeEnum;

@Service
public class SubNetworkVerifier extends AbstractOsVerifier {

	@Override
	boolean verify(final OSClientV3 os, final String resourceId) {
		Subnet res = os.networking().subnet().get(resourceId);
		return res != null;
	}

	@Override
	public ResourceTypeEnum getResourceType() {
		return ResourceTypeEnum.SUBNETWORK;
	}

}
