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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import com.ubiqube.helm.HelmException;
import com.ubiqube.helm.dto.InstallMessage;
import com.ubiqube.helm.dto.K8s;

import jakarta.validation.constraints.NotNull;

public class WorkspaceService implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(WorkspaceService.class);

	private static final String WORKSPACE_ROOT = "/tmp/workspace";
	private File wsRoot;
	private final InstallMessage im;

	private UUID id;

	public WorkspaceService(final InstallMessage im) {
		this.im = im;
		createWorkspace();
	}

	private void createWorkspace() {
		id = UUID.randomUUID();
		wsRoot = new File(WORKSPACE_ROOT, id.toString());
		wsRoot.mkdirs();
		dropCertificates();
		dropTemplate();
	}

	public UUID getId() {
		return id;
	}

	private void dropTemplate() {
		String content;
		try (InputStream is = getClass().getResourceAsStream("/config.yaml")) {
			content = new String(is.readAllBytes());
		} catch (final IOException e) {
			throw new HelmException(e);
		}
		final File file = new File(wsRoot, "config");
		content = content.replace("{{SERVER_API}}", im.getK8s().getApiUrl());
		try (OutputStream os = new FileOutputStream(file)) {
			os.write(content.getBytes());
		} catch (final IOException e) {
			throw new HelmException(e);
		}
		final Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-------");
		try {
			Files.setPosixFilePermissions(Paths.get(file.toURI()), ownerWritable);
		} catch (final IOException e) {
			throw new HelmException(e);
		}
	}

	private void dropCertificates() {
		final File ca = new File(wsRoot, "ca.pem");
		writeFile(ca, im.getK8s().getCaData());
		//
		final File client = new File(wsRoot, "client-crt.pem");
		writeFile(client, im.getK8s().getClientCrt());
		//
		final File key = new File(wsRoot, "client-key.pem");
		writeFile(key, im.getK8s().getClientKey());
	}

	private static void writeFile(final File target, final String content) {
		try (FileOutputStream os = new FileOutputStream(target)) {
			os.write(Base64.getDecoder().decode(content.getBytes()));
		} catch (final RuntimeException | IOException e) {
			throw new HelmException("While writing: " + target, e);
		}
	}

	public void pushPayload(final InputStream is) {
		final File file = new File(wsRoot, "payload.tgz");
		try (OutputStream fos = new FileOutputStream(file)) {
			is.transferTo(fos);
		} catch (final IOException e) {
			throw new HelmException(e);
		}
	}

	public ProcessResult install() {
		final List<String> list = getBaseHelmCmd();
		list.addAll(List.of("-o", "json"));
		list.add("install");
		list.add(im.getName());
		list.add("payload.tgz");
		list.addAll(createNameSpace());
		final ProcessBuilder builder = new ProcessBuilder(list);
		builder.directory(wsRoot);
		return run(builder);
	}

	private static List<String> getBaseHelmCmd() {
		final List<String> list = new ArrayList<>();
		list.add("helm");
		if (LOG.isDebugEnabled()) {
			list.add("--debug");
		}
		list.addAll(List.of("--kubeconfig", "config"));
		return list;
	}

	private static ProcessResult run(final ProcessBuilder builder) {
		try (ExecutorService tp = Executors.newFixedThreadPool(2)) {
			final Process process = builder.start();
			try (InputStream stdIn = process.getInputStream();
					InputStream stdErr = process.getErrorStream()) {
				final Future<String> out = tp.submit(new StreamGobbler(stdIn));
				final Future<String> err = tp.submit(new StreamGobbler(stdErr));
				final int exitCode = process.waitFor();
				tp.shutdown();
				return ProcessResult.builder()
						.exitCode(exitCode)
						.errout(err.get())
						.stdout(out.get())
						.build();
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new HelmException(e);
		} catch (final IOException | ExecutionException e) {
			throw new HelmException(e);
		}
	}

	private List<String> createNameSpace() {
		@NotNull
		final K8s k8s = im.getK8s();
		if (k8s.getNamespace() == null) {
			return List.of();
		}
		return List.of("--namespace", k8s.getNamespace(), "--create-namespace");
	}

	public String list() {
		final List<String> list = getBaseHelmCmd();
		list.add("list");
		final ProcessBuilder builder = new ProcessBuilder(list);
		builder.directory(wsRoot);
		final ProcessResult res = run(builder);
		return res.getStdout();
	}

	public String uninstall(final String name) {
		final List<String> list = getBaseHelmCmd();
		list.add("uninstall");
		list.add(name);
		final ProcessBuilder builder = new ProcessBuilder(list);
		builder.directory(wsRoot);
		final ProcessResult res = run(builder);
		if (res.getExitCode() != 0) {
			throw new HelmException(res.getErrout());
		}
		return res.getStdout();
	}

	@Override
	public void close() {
		FileSystemUtils.deleteRecursively(wsRoot);
	}
}
