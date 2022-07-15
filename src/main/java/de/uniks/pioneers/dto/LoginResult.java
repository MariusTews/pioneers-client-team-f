package de.uniks.pioneers.dto;

import java.util.List;

@SuppressWarnings("unused")
public record LoginResult(
		String createdAt,
		String updatedAt,
		String _id,
		String name,
		String status,
		String avatar,
		List<String> friends,
		String accessToken,
		String refreshToken
) {}
