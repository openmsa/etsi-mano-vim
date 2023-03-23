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
package com.ubiqube.etsi.mano.service.vim.sfc.enity;

import java.util.Set;

import com.ubiqube.etsi.mano.dao.mano.nsd.Classifier;
import com.ubiqube.etsi.mano.dao.mano.v2.nfvo.NsTask;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Olivier Vignaud <ovi@ubiqube.com>
 *
 */
@Entity
@Getter
@Setter
public class SfcFlowClassifierTask extends NsTask {
	/** Serial. */
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	private Classifier classifier;

	private String srcPort;

	private String dstPort;

	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> element;

	public SfcFlowClassifierTask() {
		super(null);
	}

	@Override
	public NsTask copy() {
		final SfcFlowClassifierTask task = new SfcFlowClassifierTask();
		super.copy(task);
		task.setClassifier(classifier);
		task.setSrcPort(srcPort);
		task.setDstPort(dstPort);
		task.setElement(element);
		return task;
	}
}
