package com.ubiqube.etsi.mano.service.vim;

import com.ubiqube.etsi.mano.dao.mano.common.NicType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PortParameters {
	private final String name;
	private final String networkId;
	private final String deviceId;
	private final String macAddress;
	private final NicType nicType;
	private final String qosPolicyId;
}
