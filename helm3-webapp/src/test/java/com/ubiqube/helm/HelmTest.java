package com.ubiqube.helm;

import org.junit.jupiter.api.Test;

import com.ubiqube.helm.dto.InstallMessage;
import com.ubiqube.helm.dto.K8s;
import com.ubiqube.helm.dto.Registry;
import com.ubiqube.helm.service.WorkspaceService;

class HelmTest {

	@Test
	void testName() throws Exception {
		final InstallMessage im = InstallMessage.builder()
				.k8s(K8s.builder()
						.apiUrl("api-url")
						.caData("Y2EtZGF0YQo=")
						.clientCrt("Y2xpZXRuLWNydAo=")
						.clientKey("Y2xpZXRuLWtleQo=")
						.build())
				.registry(Registry.builder()
						.name("name")
						.username("user")
						.password("pass")
						.build())
				.build();
		final WorkspaceService ws = new WorkspaceService(im);
	}
}
