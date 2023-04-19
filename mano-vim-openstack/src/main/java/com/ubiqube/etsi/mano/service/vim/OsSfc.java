/**
 *     Copyright (C) 2019-2023 Ubiqube.
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
package com.ubiqube.etsi.mano.service.vim;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.network.ext.Ethertype;
import org.openstack4j.model.network.ext.FlowClassifier;
import org.openstack4j.model.network.ext.PortChain;
import org.openstack4j.model.network.ext.PortPair;
import org.openstack4j.model.network.ext.PortPairGroup;

import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.openstack.OsUtils;
import com.ubiqube.etsi.mano.orchestrator.entities.SystemConnections;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@SuppressWarnings("static-method")
public class OsSfc {

	private static OSClientV3 internalAuthenticate(final SystemConnections vci) {
		return OsUtils.authenticate(vci.getInterfaceInfo(), vci.getAccessInfo());
	}

	public String createPortPair(final SystemConnections vci, final String name, final String egressId, final String ingressId) {
		final OSClientV3 os = internalAuthenticate(vci);
		final PortPair portPair = Builders.portPair()
				.egressId(egressId)
				.ingressId(ingressId)
				.name(name)
				.build();
		final PortPair obj = os.sfc().portpairs().create(portPair);
		return obj.getId();
	}

	public String createPortPairGroup(final SystemConnections vci, final String name, final List<String> portPairs) {
		final OSClientV3 os = internalAuthenticate(vci);
		final PortPairGroup portPairGroup = Builders.portPairGroup()
				.name(name)
				.portPairs(portPairs)
				.build();
		final PortPairGroup obj = os.sfc().portpairgroups().create(portPairGroup);
		return obj.getId();
	}

	public String createFlowClassifier(final SystemConnections vci, final Classifier classifier, final String src, final String dst) {
		final OSClientV3 os = internalAuthenticate(vci);
		final FlowClassifier flowClassifier = Builders.flowClassifier()
				.name(classifier.getClassifierName())
				.destinationIpPrefix(classifier.getDestinationIpAddressPrefix())
				.destinationPortRangeMax(Optional.ofNullable(classifier.getDestinationPortRangeMax()).map(Math::toIntExact).orElse(null))
				.destinationPortRangeMin(Optional.ofNullable(classifier.getDestinationPortRangeMin()).map(Math::toIntExact).orElse(null))
				.ethertype(Ethertype.forValue(classifier.getEtherType()))
				.sourceIpPrefix(classifier.getSourceIpAddressPrefix())
				.sourcePortRangeMax(Optional.ofNullable(classifier.getSourcePortRangeMax()).map(Math::toIntExact).orElse(null))
				.sourcePortRangeMin(Optional.ofNullable(classifier.getSourcePortRangeMin()).map(Math::toIntExact).orElse(null))
				.protocol(classifier.getProtocol())
				.logicalSourcePort(src)
				.logicalDestinationPort(dst)
				.build();
		final FlowClassifier obj = os.sfc().flowclassifiers().create(flowClassifier);
		return obj.getId();
	}

	public void deleteFlowClassifier(final SystemConnections vci, final String flowClassifierId) {
		final OSClientV3 os = internalAuthenticate(vci);
		os.sfc().flowclassifiers().delete(flowClassifierId);
	}

	public String createPortChain(final SystemConnections vci, final String name, final Set<String> flowClassifiers, final Set<String> portPairGroups) {
		final OSClientV3 os = internalAuthenticate(vci);
		final PortChain portChain = Builders.portChain()
				.flowClassifiers(flowClassifiers.stream().toList())
				.name(name)
				.portPairGroups(portPairGroups.stream().toList())
				.build();
		final PortChain obj = os.sfc().portchains().create(portChain);
		return obj.getId();
	}

	public void deletePortChain(final SystemConnections vci, final String vimResourceId) {
		final OSClientV3 os = internalAuthenticate(vci);
		os.sfc().portchains().delete(vimResourceId);
	}

	public void deletePortPairGroup(final SystemConnections vci, final String vimResourceId) {
		final OSClientV3 os = internalAuthenticate(vci);
		os.sfc().portpairgroups().delete(vimResourceId);
	}

	public void deletePortPair(final SystemConnections vci, final String vimResourceId) {
		final OSClientV3 os = internalAuthenticate(vci);
		os.sfc().portpairs().delete(vimResourceId);
	}
}
