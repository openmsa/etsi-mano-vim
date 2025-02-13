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
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public @Nullable String getVimConnectionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setVimConnectionId(final String id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setName(final String name) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Node> getType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setAlias(final String alias) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getAlias() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRank() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setRank(final int rank) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getTemplateParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setTemplateParameters(final String u) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setDelete(final boolean del) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setSystemBuilder(final SystemBuilder<String> db) {
			// TODO Auto-generated method stub

		}

		@Override
		public SystemBuilder<String> getSystemBuilder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public @Nullable String getVimResourceId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setVimResourceId(final String res) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setRemovedLiveInstanceId(final UUID liveInstanceId) {
			// TODO Auto-generated method stub

		}

		@Override
		public String getToscaName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ResultType getStatus() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
