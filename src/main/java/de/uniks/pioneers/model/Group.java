package de.uniks.pioneers.model;

import java.util.List;

@SuppressWarnings("unused")
public record Group(
		String createdAt,
		String updatedAt,
		String _id,
		String name,
		List<String> members
) {}
