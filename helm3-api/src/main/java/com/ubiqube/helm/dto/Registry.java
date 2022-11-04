package com.ubiqube.helm.dto;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Registry {

	private String name;
	@NotNull
	private String username;
	@NotNull
	private String password;
}
