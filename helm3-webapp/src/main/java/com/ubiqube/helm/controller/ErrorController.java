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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ubiqube.helm.dto.ProblemDetails;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ErrorController extends AbstractErrorController {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorController.class);

	public ErrorController(final ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@RequestMapping("/error")
	public ResponseEntity<ProblemDetails> getError(final HttpServletRequest request) {
		final Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		final Map<String, Object> ea = getErrorAttributes(request, ErrorAttributeOptions.of(Include.EXCEPTION, Include.MESSAGE, Include.BINDING_ERRORS));
		final String message = buildMessage(ea);
		LOG.trace("Error controller: {} => {}", status, ea);
		if (status != null) {
			final ProblemDetails problemDetail = new ProblemDetails(status, message);
			return ResponseEntity.status(status)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.body(problemDetail);
		}
		final ProblemDetails problemDetail = new ProblemDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Some thing bad happend.");
		return ResponseEntity
				.status(500)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(problemDetail);
	}

	private static String buildMessage(final Map<String, Object> ea) {
		final StringBuilder message = new StringBuilder().append((String) ea.get("message"));
		final String ex = (String) ea.get("exception");
		if (MethodArgumentNotValidException.class.getName().equals(ex)) {
			final Object c = ea.get("errors");
			message.append(" ").append(c);
		}
		return message.toString();
	}

}
