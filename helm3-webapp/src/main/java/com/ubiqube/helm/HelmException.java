package com.ubiqube.helm;

public class HelmException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HelmException(final Throwable e) {
		super(e);
	}

	public HelmException(final String string) {
		super(string);
	}

}
