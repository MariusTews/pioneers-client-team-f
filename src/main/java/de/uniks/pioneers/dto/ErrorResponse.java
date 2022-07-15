package de.uniks.pioneers.dto;

@SuppressWarnings("unused")
public record ErrorResponse(
		Number statusCode,
		String error,
		String message
) {}
