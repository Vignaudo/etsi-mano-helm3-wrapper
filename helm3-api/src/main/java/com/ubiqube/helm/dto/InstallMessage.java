package com.ubiqube.helm.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstallMessage {
	@NotNull
	private String name;

	@Valid
	@NotNull
	private K8s k8s;

	@Valid
	private Registry registry;

}
