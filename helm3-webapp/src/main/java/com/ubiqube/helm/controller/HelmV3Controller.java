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
package com.ubiqube.helm.controller;

import java.io.IOException;
import java.io.InputStream;

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

import com.ubiqube.helm.ExecutionException;
import com.ubiqube.helm.dto.InstallMessage;
import com.ubiqube.helm.service.ProcessResult;
import com.ubiqube.helm.service.WorkspaceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings("static-method")
@Validated
@RestController("/")
public class HelmV3Controller {

	private static final Logger LOG = LoggerFactory.getLogger(HelmV3Controller.class);

	@PostMapping(value = "/install", consumes = { "multipart/form-data" }, produces = "application/json")
	public ResponseEntity<String> install(@RequestPart("config") @Valid @NotNull final InstallMessage config,
			@RequestParam @NotNull final MultipartFile file, @RequestParam @NotNull final MultipartFile values) throws IOException {
		try (final WorkspaceService ws = new WorkspaceService(config);
				InputStream is = file.getInputStream()) {
			LOG.info("{}: Deploying payload.", ws.getId());
			ws.pushPayload(is);
			LOG.info("{}: Call Install ", ws.getId());
			final ProcessResult res = ws.install();
			LOG.info("{}: Install done. exit code: {}", ws.getId(), res.getExitCode());
			LOG.info(res.getStdout());
			if (res.getExitCode() != 0) {
				LOG.error(res.getErrout());
				throw new ExecutionException(res.getExitCode(), res.getErrout());
			}
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
	public ResponseEntity<String> uninstall(@RequestPart("config") @Valid @NotNull final InstallMessage config, @PathVariable final String name) {
		LOG.info("Uninstalling: {}", name);
		try (final WorkspaceService ws = new WorkspaceService(config)) {
			return ResponseEntity.ok(ws.uninstall(name));
		}
	}
}
