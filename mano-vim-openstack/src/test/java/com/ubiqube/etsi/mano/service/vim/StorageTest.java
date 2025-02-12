/**
 * Copyright (C) 2019-2025 Ubiqube.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.service.vim;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.ubiqube.etsi.mano.dao.mano.vim.SoftwareImage;
import com.ubiqube.etsi.mano.dao.mano.vim.VimConnectionInformation;
import com.ubiqube.etsi.mano.dao.mano.vim.VnfStorage;
import com.ubiqube.etsi.mano.vim.dto.SwImage;

@WireMockTest
class StorageTest {

	public StorageTest() {
		//
	}

	@Test
	void testCreateVolumeNoSW(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/8776/v3/volumes")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-create.json"))));
		stubFor(get(urlPathMatching("/8776/v3/volumes/d0ae47e1-7240-47f8-b9b6-65df236214f6")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-detail.json"))));
		//
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VnfStorage vnfStorage = new VnfStorage();
		vnfStorage.setSize(1 * 1024 * 1024 * 1024L);
		os.storage(vci).createStorage(vnfStorage, "alias");
		assertTrue(true);
	}

	private static OpenStackVim createOsVim() {
		return new OpenStackVim();
	}

	@Test
	void testCreateVolumeWithSW_NotFound(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		stubFor(post(urlPathMatching("/8776/v3/volumes")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-create.json"))));
		stubFor(get(urlPathMatching("/8776/v3/volumes/d0ae47e1-7240-47f8-b9b6-65df236214f6")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-detail.json"))));
		//
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VnfStorage vnfStorage = new VnfStorage();
		vnfStorage.setSize(1 * 1024 * 1024 * 1024L);
		final SoftwareImage si = new SoftwareImage();
		vnfStorage.setSoftwareImage(si);
		final Storage sto = os.storage(vci);
		assertThrows(VimException.class, () -> sto.createStorage(vnfStorage, "alias"));
	}

	@Test
	void testCreateVolumeWithSW_FoundOnName(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		stubFor(post(urlPathMatching("/8776/v3/volumes")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-create.json"))));
		stubFor(get(urlPathMatching("/8776/v3/volumes/d0ae47e1-7240-47f8-b9b6-65df236214f6")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-detail.json"))));
		//
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VnfStorage vnfStorage = new VnfStorage();
		vnfStorage.setSize(1 * 1024 * 1024 * 1024L);
		final SoftwareImage si = new SoftwareImage();
		si.setName("cirros-0.5.2");
		vnfStorage.setSoftwareImage(si);
		os.storage(vci).createStorage(vnfStorage, "alias");
		assertTrue(true);
	}

	@Test
	void testDeleteVolume(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(delete(urlPathMatching("/8776/v3/volumes/d0ae47e1-7240-47f8-b9b6-65df236214f6")).willReturn(aResponse()
				.withStatus(200)
				.withBody("")));
		//
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VnfStorage vnfStorage = new VnfStorage();
		vnfStorage.setSize(1 * 1024 * 1024 * 1024L);
		os.storage(vci).deleteStorage("d0ae47e1-7240-47f8-b9b6-65df236214f6");
		assertTrue(true);
	}

	@Test
	void testGetSwImageMatching_Found(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final SoftwareImage si = new SoftwareImage();
		si.setName("cirros-0.5.2");
		os.storage(vci).getSwImageMatching(si);
		assertTrue(true);
	}

	@Test
	void testGetSwImageMatching_NotFound(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final SoftwareImage si = new SoftwareImage();
		si.setName("cirros");
		os.storage(vci).getSwImageMatching(si);
		assertTrue(true);
	}

	@Test
	void testdoUpload(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/9292/v2/images")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/image-single2.json"))));
		stubFor(put(urlPathMatching("/9292/v2/images/9f326f5f-7839-4928-9722-ad51ca97b478/file")).willReturn(aResponse()
				.withStatus(200)
				.withBody("")));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final SoftwareImage si = new SoftwareImage();
		si.setName("cirros");
		final InputStream is = InputStream.nullInputStream();
		os.storage(vci).uploadSoftwareImage(is, si);
		assertTrue(true);
	}

	@Test
	void testGetImageDetail(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images/9f326f5f-7839-4928-9722-ad51ca97b478")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/image-single2.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		Storage sto = os.storage(vci);
		assertThrows(VimException.class, () -> sto.getImageDetail("9f326f5f-7839-4928-9722-ad51ca97b478"));
	}

	@Test
	void testGetAlgorithm(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images/9f326f5f-7839-4928-9722-ad51ca97b478")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/image-single.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final SoftwareImage imageDetail = os.storage(vci).getImageDetail("9f326f5f-7839-4928-9722-ad51ca97b478");
		assertEquals("SHA-512", imageDetail.getChecksum().getAlgorithm());
	}

	@Test
	void testGetAlgorithmNull(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images/9f326f5f-7839-4928-9722-ad51ca97b478")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/image-single-null.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final SoftwareImage imageDetail = os.storage(vci).getImageDetail("9f326f5f-7839-4928-9722-ad51ca97b478");
		assertEquals("MD5", imageDetail.getChecksum().getAlgorithm());
	}

	@Test
	void testDeleteObjectStorage(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.storage(vci).deleteObjectStorage("storageId");
		assertTrue(true);
	}

	@Test
	void testCreateObjectStorage(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VnfStorage vnfStorage = new VnfStorage();
		vnfStorage.setToscaName("name");
		os.storage(vci).createObjectStorage(vnfStorage);
		assertTrue(true);
	}

	@Test
	void testGetImageInformation(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/images.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		os.storage(vci).getImagesInformations("cirros-0.5.2");
		assertTrue(true);
	}

	@Test
	void testGetImageInformationError(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(post(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final Storage sto = os.storage(vci);
		assertThrows(VimException.class, () -> sto.getImagesInformations("cirros-0.5.2"));
	}

	@Test
	void testGetImageList(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/9292/v2/images"))
				.withQueryParam("limit", equalTo("500"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBody(OsHelper.getFile(wri, "/images.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final List<SwImage> images = os.storage(vci).getImageList();
		assertTrue(images.size() > 0);
	}

	@Test
	void testGetStorage(final WireMockRuntimeInfo wri) {
		stubFor(post(urlPathMatching("/auth/tokens")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/auth.json"))));
		stubFor(get(urlPathMatching("/8776/v3/volumes/d0ae47e1-7240-47f8-b9b6-65df236214f6")).willReturn(aResponse()
				.withStatus(200)
				.withBody(OsHelper.getFile(wri, "/volumes-detail.json"))));
		final OpenStackVim os = createOsVim();
		final VimConnectionInformation vci = OsHelper.createServer(wri);
		final VimVolume volume = os.storage(vci).getStorage("d0ae47e1-7240-47f8-b9b6-65df236214f6");
		assertNotNull(volume);
	}
}
