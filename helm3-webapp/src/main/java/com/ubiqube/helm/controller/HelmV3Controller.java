package com.ubiqube.helm.controller;

import java.io.IOException;
import java.io.InputStream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ubiqube.helm.dto.InstallMessage;
import com.ubiqube.helm.service.ProcessResult;
import com.ubiqube.helm.service.WorkspaceService;

@SuppressWarnings("static-method")
@Validated
@RestController("/")
public class HelmV3Controller {

	private static final Logger LOG = LoggerFactory.getLogger(HelmV3Controller.class);

	@PostMapping(value = "/install", consumes = { "multipart/form-data" }, produces = "application/json")
	public ResponseEntity<String> install(@RequestPart("config") @Valid @NotNull final InstallMessage config, @RequestParam("file") @NotNull final MultipartFile file) throws IOException {
		try (final WorkspaceService ws = new WorkspaceService(config);
				InputStream is = file.getInputStream()) {
			LOG.info("{}: Deploying payload.", ws.getId());
			ws.pushPayload(is);
			LOG.info("{}: Call Install ", ws.getId());
			final ProcessResult res = ws.install();
			LOG.info("{}: Install done.", ws.getId());
			LOG.info(res.getStdout());
			LOG.error(res.getErrout());
			return ResponseEntity.ok(res.getStdout());
		}
	}

	@PostMapping(value = "/", consumes = { "multipart/form-data" }, produces = "application/json")
	public ResponseEntity<String> list(@RequestPart("config") @Valid @NotNull final InstallMessage config) {
		try (final WorkspaceService ws = new WorkspaceService(config)) {
			return ResponseEntity.ok(ws.list());
		}
	}

	@PostMapping(value = "/uninstall/{name}", consumes = { "multipart/form-data" }, produces = "application/json")
	public ResponseEntity<String> uninstall(@RequestPart("config") @Valid @NotNull final InstallMessage config, @PathVariable("name") final String name) {
		try (final WorkspaceService ws = new WorkspaceService(config)) {
			return ResponseEntity.ok(ws.uninstall(name));
		}
	}
}
