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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.x_k8s.cluster.v1beta1.Cluster;
import io.x_k8s.cluster.v1beta1.clusterstatus.Conditions;

public class ClusterCreateEventHandler implements ResourceEventHandler<Cluster>, WaitableEvent {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(ClusterCreateEventHandler.class);

	private final CountDownLatch latch;

	public ClusterCreateEventHandler() {
		latch = new CountDownLatch(1);
	}

	@Override
	public void onAdd(final Cluster obj) {
		//
	}

	@Override
	public void onUpdate(final Cluster oldObj, final Cluster newObj) {
		final List<Conditions> conds = newObj.getStatus().getConditions();
		if (conds.stream()
				.filter(x -> "Ready".equals(x.getType()))
				.anyMatch(x -> "True".equals(x.getStatus()))) {
			latch.countDown();
			LOG.info("Cluster: {} created.", newObj.getMetadata().getName());
		} else {
			LOG.debug("Not correct state: {}", dumpConds(conds));
		}
	}

	private static Object dumpConds(final List<Conditions> conds) {
		return conds.stream().map(Conditions::getStatus).toList();
	}

	@Override
	public void onDelete(final Cluster obj, final boolean deletedFinalStateUnknown) {
		//
	}

	@Override
	public boolean await(final long timeout, final TimeUnit tu) throws InterruptedException {
		return latch.await(timeout, tu);
	}

}
