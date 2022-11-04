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
