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
package com.ubiqube.etsi.mano.vim.k8s;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.ubiqube.etsi.mano.service.vim.VimException;
import com.ubiqube.etsi.mano.vim.k8sexecutor.K8sExecutor;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;

public class TestExecutor implements K8sExecutor {
	/** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(TestExecutor.class);

	private final KubernetesClient client;
	private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

	public TestExecutor(final KubernetesClient client) {
		this.client = client;
	}

	@Override
	public <R extends HasMetadata> R create(final Config k8sCfg, final Function<KubernetesClient, R> func) {
		try {
			final R o = func.apply(client);
			final JsonNode str = toObjectNode(o);
			final ObjectNode doc = findDocument(o.getKind(), o.getMetadata().getName());
			LOG.info("{}", doc);
			LOG.info("{}", str);
			assertEquals(doc, str);
			return o;
		} catch (final Exception e) {
			throw new VimException(e);
		}
	}

	ObjectNode findDocument(final String kind, final String name) throws IOException {
		final YAMLFactory yamlFactory = new YAMLFactory();
		final YAMLParser yamlParser = yamlFactory.createParser(new File("src/test/resources/capi/quickstart.yaml"));
		final List<ObjectNode> res = mapper.readValues(yamlParser, new TypeReference<ObjectNode>() {
		}).readAll();
		for (final ObjectNode objectNode : res) {
			final String type = objectNode.get("kind").asText();
			if (kind.equals(type) && matchName(objectNode, name)) {
				return objectNode;
			}
		}
		throw new VimException("Could not find: " + kind + "/" + name + " in \n" + res);
	}

	private static boolean matchName(final ObjectNode objectNode, final String name) {
		final JsonNode meta = objectNode.get("metadata");
		return meta.get("name").asText().equals(name);
	}

	private JsonNode toObjectNode(final Object o) throws JsonMappingException, JsonProcessingException {
		final String ser = Serialization.asYaml(o);
		return mapper.readTree(ser);
	}

	@Override
	public List<StatusDetails> delete(final Config k8sConfig, final Function<KubernetesClient, List<StatusDetails>> func) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void waitForClusterDelete(final Config k8sCfg, final HasMetadata obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitForClusterCreate(final Config k8sCfg, final HasMetadata obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public <R extends HasMetadata> R get(final Config k8sCfg, final Function<KubernetesClient, R> func) {
		return func.apply(client);
	}

}
