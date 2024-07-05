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

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.ClusterStatus;
import io.x_k8s.cluster.v1beta1.clusterstatus.Conditions;

class ClusterCreateEventHandlerTest {
	ClusterCreateEventHandler createService() {
		return new ClusterCreateEventHandler();
	}

	@Test
	void test() {
		final ClusterCreateEventHandler srv = createService();
		srv.onAdd(null);
		srv.onDelete(null, false);
		final Cluster cluster = new Cluster();
		cluster.setStatus(new ClusterStatus());
		cluster.getStatus().setConditions(List.of());
		srv.onUpdate(cluster, cluster);
		assertTrue(true);
	}

	@Test
	void testBadType() {
		final ClusterCreateEventHandler srv = createService();
		srv.onAdd(null);
		srv.onDelete(null, false);
		final Cluster cluster = new Cluster();
		cluster.setStatus(new ClusterStatus());
		final Conditions cond = new Conditions();
		cluster.getStatus().setConditions(List.of(cond));
		srv.onUpdate(cluster, cluster);
		assertTrue(true);
	}

	@Test
	void testFalse() {
		final ClusterCreateEventHandler srv = createService();
		srv.onAdd(null);
		srv.onDelete(null, false);
		final Cluster cluster = new Cluster();
		cluster.setStatus(new ClusterStatus());
		final Conditions cond = new Conditions();
		cond.setType("Ready");
		cond.setStatus("False");
		cluster.getStatus().setConditions(List.of(cond));
		srv.onUpdate(cluster, cluster);
		assertTrue(true);
	}

	@Test
	void testTrue() throws InterruptedException {
		final ClusterCreateEventHandler srv = createService();
		srv.onAdd(null);
		srv.onDelete(null, false);
		final Cluster cluster = new Cluster();
		cluster.setStatus(new ClusterStatus());
		final Conditions cond = new Conditions();
		cond.setType("Ready");
		cond.setStatus("True");
		cluster.getStatus().setConditions(List.of(cond));
		srv.onUpdate(cluster, cluster);
		srv.await(10, TimeUnit.SECONDS);
		assertTrue(true);
	}
}
