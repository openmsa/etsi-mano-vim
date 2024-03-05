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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;

/**
 *
 * @author Olivier Vignaud
 *
 */
public class HelmHelper {
	public HelmHelper() {
		// Nothing.
	}

	public static void buildHelmTarball() throws IOException {
		final File file = new File("/tmp/chart.tgz");
		final TarOutputStream tar = new TarOutputStream(file);
		final GZIPOutputStream tos = new GZIPOutputStream(tar);
		final File in = new File("src/test/resources/helm/Chart.yaml");
		tar.putNextEntry(new TarEntry(in, "Chart.yaml"));
		final FileInputStream fis = new FileInputStream(in);
		fis.transferTo(tar);
		tar.flush();
		tar.close();
		tos.close();
	}

	public static String getResourceContent(final Object cp, final String string) throws IOException {
		final byte[] bytes = cp.getClass().getResourceAsStream(string).readAllBytes();
		return new String(bytes);
	}

	public static String pemEncode(final Object key) {
		try (final Writer out = new StringWriter();
				final PemWriter pw = new PemWriter(out);) {
			pw.writeObject(new JcaMiscPEMGenerator(key));
			pw.flush();
			return out.toString();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
