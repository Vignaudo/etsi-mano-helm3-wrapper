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
package com.ubiqube.helm;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.ubiqube.helm.dto.InstallMessage;
import com.ubiqube.helm.dto.K8s;
import com.ubiqube.helm.dto.Registry;
import com.ubiqube.helm.service.WorkspaceService;

class HelmTest {

	@Test
	void testName() {
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
		assertNotNull(ws);
	}
}
