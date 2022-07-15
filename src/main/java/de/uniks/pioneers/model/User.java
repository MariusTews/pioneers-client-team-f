package de.uniks.pioneers.model;

import java.util.List;

@SuppressWarnings("unused")
public record User(
		String createdAt,
		String updatedAt,
		String _id,
		String name,
		String status,
		String avatar,
		List<String> friends
) {}
