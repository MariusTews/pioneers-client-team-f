package de.uniks.pioneers.dto;

@SuppressWarnings("unused")
public record ValidationErrorResponse(
		Number statusCode,
		String error,
		String message
) {}
