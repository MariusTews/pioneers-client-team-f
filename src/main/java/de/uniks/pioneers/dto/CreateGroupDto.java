package de.uniks.pioneers.dto;

import java.util.List;

@SuppressWarnings("unused")
public record CreateGroupDto(
		String name,
		List<String> members
){}
