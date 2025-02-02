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
package com.ubiqube.helm.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InstallMessageTest {

	@Test
	void testName() {
		InstallMessage installMessage = InstallMessage.builder()
				.name("test")
				.k8s(null)
				.registry(null)
				.build();
		assertEquals("test", installMessage.getName());
		installMessage.setName("test2");
		assertEquals("test2", installMessage.getName());
		int hashCode = installMessage.hashCode();
		assertEquals(hashCode, installMessage.hashCode());
		assertEquals(installMessage, installMessage);
		assertEquals(installMessage.toString(), installMessage.toString());
		assertEquals(installMessage, InstallMessage.builder()
				.name("test2")
				.k8s(null)
				.registry(null)
				.build());
	}
}
