package com.ubiqube.etsi.mano.service.vim.verify;

import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.network.Router;
import org.springframework.stereotype.Service;

import com.ubiqube.etsi.mano.dao.mano.vim.ResourceTypeEnum;

@Service
public class VnfExtCpVerifier extends AbstractOsVerifier {

	@Override
	boolean verify(final OSClientV3 os, final String resourceId) {
		Router res = os.networking().router().get(resourceId);
		return res != null;
	}

	@Override
	public ResourceTypeEnum getResourceType() {
		return ResourceTypeEnum.VNF_EXTCP;
	}

}
