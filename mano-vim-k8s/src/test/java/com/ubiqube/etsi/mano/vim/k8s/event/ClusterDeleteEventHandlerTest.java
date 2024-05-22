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
package com.ubiqube.etsi.mano.vim.k8s.event;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.x_k8s.cluster.v1beta1.Cluster;

class ClusterDeleteEventHandlerTest {

	@Test
	void test() throws InterruptedException {
		final ClusterDeleteEventHandler srv = new ClusterDeleteEventHandler();
		srv.onAdd(null);
		srv.onUpdate(null, null);

		final Cluster obj = new Cluster();
		srv.onDelete(obj, false);
		srv.await(10, TimeUnit.SECONDS);

	}

}
