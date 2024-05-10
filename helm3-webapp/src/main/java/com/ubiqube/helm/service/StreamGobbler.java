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
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.helm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import com.ubiqube.helm.HelmException;

public class StreamGobbler implements Callable<String> {

	private final InputStream is;

	private String result;

	public StreamGobbler(final InputStream is) {
		this.is = is;
	}

	public String getAsString() {
		return result;
	}

	@Override
	public String call() throws Exception {
		int i;
		final byte[] buff = new byte[4096];
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			while ((i = is.read(buff)) != -1) {
				os.write(buff, 0, i);
			}
			result = new String(os.toByteArray());
		} catch (final IOException e) {
			throw new HelmException(e);
		}
		return result;
	}
}
