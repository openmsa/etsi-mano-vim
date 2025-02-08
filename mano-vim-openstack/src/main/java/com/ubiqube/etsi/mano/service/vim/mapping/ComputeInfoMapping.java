package com.ubiqube.etsi.mano.service.vim.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.openstack4j.model.compute.Server;

import com.ubiqube.etsi.mano.service.vim.ComputeInfo;
import com.ubiqube.etsi.mano.service.vim.ComputeStatus;

@Mapper
public interface ComputeInfoMapping {

	ComputeInfo mapServerInfo(Server server);

	@ValueMapping(source = "ACTIVE", target = "COMPLETED")
	@ValueMapping(source = "BUILD", target = "START")
	@ValueMapping(source = "ERROR", target = "FAILED")
	@ValueMapping(source = "SHUTOFF", target = "STOPPED")
	@ValueMapping(source = "DELETED", target = "STOPPED")
	@ValueMapping(source = "HARD_REBOOT", target = "DEPLOYING")
	@ValueMapping(source = "PAUSED", target = "STOPPED")
	@ValueMapping(source = "SUSPENDED", target = "STOPPED")
	@ValueMapping(source = "<ANY_REMAINING>", target = "DEPLOYING")
	ComputeStatus mapServerStatus(Server.Status server);
}
