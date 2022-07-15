package de.uniks.pioneers.dto;

import java.util.List;

@SuppressWarnings("unused")
public record UpdateGroupDto(
		String name,
		List<String> members
) {}
