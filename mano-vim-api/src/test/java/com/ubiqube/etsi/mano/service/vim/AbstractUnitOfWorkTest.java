package com.ubiqube.etsi.mano.service.vim;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.ubiqube.etsi.mano.orchestrator.ResultType;
import com.ubiqube.etsi.mano.orchestrator.SystemBuilder;
import com.ubiqube.etsi.mano.orchestrator.nodes.Node;
import com.ubiqube.etsi.mano.orchestrator.vt.VirtualTaskV3;

class AbstractUnitOfWorkTest {

	@Test
	void test() {
		TestUow testUow = new TestUow(new TestVirtualTaskV3(), null);
		assertNull(testUow.execute(null));
		assertNull(testUow.rollback(null));
		assertNotNull(testUow.getVirtualTask());
		assertNull(testUow.getType());
		testUow.setResource(null);
	}

	class TestVirtualTaskV3 implements VirtualTaskV3<String> {

		@Override
		public boolean isDeleteTask() {
			return false;
		}

		@Override
		public @Nullable String getVimConnectionId() {
			return null;
		}

		@Override
		public void setVimConnectionId(final String id) {
			//
		}

		@Override
		public void setName(final String name) {
			//
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Class<? extends Node> getType() {
			return null;
		}

		@Override
		public void setAlias(final String alias) {
			//
		}

		@Override
		public String getAlias() {
			return null;
		}

		@Override
		public int getRank() {
			return 0;
		}

		@Override
		public void setRank(final int rank) {
			//
		}

		@Override
		public String getTemplateParameters() {
			return null;
		}

		@Override
		public void setTemplateParameters(final String u) {
			//
		}

		@Override
		public void setDelete(final boolean del) {
			//
		}

		@Override
		public void setSystemBuilder(final SystemBuilder<String> db) {
			//
		}

		@Override
		public SystemBuilder<String> getSystemBuilder() {
			return null;
		}

		@Override
		public @Nullable String getVimResourceId() {
			return null;
		}

		@Override
		public void setVimResourceId(final String res) {
			//
		}

		@Override
		public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
			//
		}

		@Override
		public String getToscaName() {
			return null;
		}

		@Override
		public ResultType getStatus() {
			return null;
		}
	}
}
