package com.ubiqube.etsi.mano.service.vim.verify;

import org.openstack4j.api.OSClient.OSClientV3;

import com.ubiqube.etsi.mano.dao.mano.ai.KeystoneAuthV3;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.openstack.OsUtils;

public abstract class AbstractOsVerifier implements ResourceVerifier {
	@Override
	public final boolean verify(final VimConnectionInformation vimConnectionInformation, final String resourceId) {
		final OSClientV3 os = OsUtils.authenticate(vimConnectionInformation.getInterfaceInfo(), (KeystoneAuthV3) vimConnectionInformation.getAccessInfo());
		return verify(os, resourceId);
	}

	abstract boolean verify(OSClientV3 os, String resourceId);
}
