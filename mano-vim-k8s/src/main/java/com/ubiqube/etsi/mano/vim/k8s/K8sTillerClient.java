/**
 *     Copyright (C) 2019-2020 Ubiqube.
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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.service.vim.k8s.K8sClient;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public class K8sTillerClient implements K8sClient {

	@Override
	public String deploy(final Servers srv, final K8sServers server, final String userKey, final File file, final String name) {
		try {
			final TillerClient cli = TillerClient.ofCerts(new URL(server.getApiAddress()), server.getCaPem().getBytes(), server.getUserCrt().getBytes(), userKey, name);
			return cli.deploy(file);
		} catch (final MalformedURLException e) {
			throw new VimException(e);
		}
	}

	@Override
	public void undeploy(final Servers server, final K8sServers k8s, final String userKey, final String vimResourceId) {
		// To do.
	}

}
