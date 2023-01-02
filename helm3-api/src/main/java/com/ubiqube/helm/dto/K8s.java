package com.ubiqube.helm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class K8s {

	private String namespace;

	@NotNull
	@Pattern(regexp = "^(http|https)://.*")
	private String apiUrl;

	@Pattern(regexp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")
	@NotNull
	private String caData;

	@Pattern(regexp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")
	@NotNull
	private String clientCrt;

	@Pattern(regexp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")
	@NotNull
	private String clientKey;
}
