package de.uniks.pioneers.dto;

public record ErrorResponse(
		Number statusCode,
		String error,
		String message
) {}
