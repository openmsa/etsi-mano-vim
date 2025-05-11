package com.ubiqube.etsi.mano.service.vim.verify;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.network.Port;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vim.ResourceTypeEnum;

@Service
public class PortVerify extends AbstractOsVerifier {

	@Override
	public ResourceTypeEnum getResourceType() {
		return ResourceTypeEnum.LINKPORT;
	}

	@Override
	boolean verify(final OSClientV3 os, final String resourceId) {
		Port res = os.networking().port().get(resourceId);
		return res != null;
	}
}
