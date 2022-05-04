package de.uniks.pioneers.model;

import java.util.List;

public record Groupe(
		String createdAt,
		String updatedAt,
		String _id,
		List<String> members
) {}
