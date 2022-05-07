package de.uniks.pioneers.dto;

public record ValidationErrorResponse(
		Number statusCode,
		String error,
		String message
) {}
