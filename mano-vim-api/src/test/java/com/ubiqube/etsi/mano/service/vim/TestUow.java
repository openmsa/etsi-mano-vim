package com.ubiqube.etsi.mano.service.vim;

import org.jspecify.annotations.Nullable;

import com.ubiqube.etsi.mano.orchestrator.Context3d;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

public class TestUow extends AbstractUnitOfWork<String> {

	protected TestUow(final VirtualTaskV3<String> task, final Class<? extends Node> node) {
		super(task, node);
	}

	@Override
	public @Nullable String execute(final Context3d context) {
		return null;
	}

	@Override
	public @Nullable String rollback(final Context3d context) {
		return null;
	}

}
