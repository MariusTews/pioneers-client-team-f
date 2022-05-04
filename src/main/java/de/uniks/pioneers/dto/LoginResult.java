package de.uniks.pioneers.dto;

public record LoginResult(
		String _id,
		String name,
		String status,
		String avatar,
		String accessToken,
		String refreshToken
) {}
