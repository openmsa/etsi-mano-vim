package com.ubiqube.etsi.mano.service.vim.verify;

import com.ubiqube.etsi.mano.dao.mano.vim.ResourceTypeEnum;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;

public interface ResourceVerifier {

	boolean verify(final VimConnectionInformation vimConnectionInformation, final String resourceId);

	ResourceTypeEnum getResourceType();
}
