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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ProblemDetailsTest {

	@Test
	void test() {
		ProblemDetails problemDetails = new ProblemDetails();
		assertEquals("about:blank", problemDetails.getType().toString());
		problemDetails.setType(null);
		assertNull(problemDetails.getType());
		problemDetails.setDetail(null);
		assertNull(problemDetails.getDetail());
		problemDetails.setInstance(null);
		assertNull(problemDetails.getInstance());
		problemDetails.setStatus(200);
		assertEquals(200, problemDetails.getStatus());
		problemDetails.setTitle("test");
		assertEquals("test", problemDetails.getTitle());

		int hashCode = problemDetails.hashCode();
		assertEquals(hashCode, problemDetails.hashCode());
		assertEquals(problemDetails, problemDetails);
		assertEquals(problemDetails.toString(), problemDetails.toString());
		problemDetails.type(null).detail(null).instance(null).title(null).status(null);

	}

	@Test
	void testNameCtor() throws Exception {
		ProblemDetails problemDetails = new ProblemDetails(200, "test");
		assertEquals("about:blank", problemDetails.getType().toString());
		assertEquals("test", problemDetails.getDetail());
		assertEquals(200, problemDetails.getStatus());
	}
}
