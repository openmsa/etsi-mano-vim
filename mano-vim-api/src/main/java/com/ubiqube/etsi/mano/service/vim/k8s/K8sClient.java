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
package com.ubiqube.etsi.mano.service.vim.k8s;

import java.io.File;

import com.ubiqube.etsi.mano.dao.mano.config.Servers;
import com.ubiqube.etsi.mano.dao.mano.k8s.K8sServers;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
public interface K8sClient {

	String deploy(Servers server, K8sServers k8s, String userKey, File file, String name);

	void undeploy(final Servers server, final K8sServers k8s, final String userKey, final String vimResourceId);

}
