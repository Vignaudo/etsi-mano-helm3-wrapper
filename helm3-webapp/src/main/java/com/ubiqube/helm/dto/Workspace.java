package com.ubiqube.helm.dto;

import java.io.File;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Workspace {

	private File root;
}
