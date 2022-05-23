package de.uniks.pioneers.dto;

import java.util.List;
import java.util.Optional;

public record CreateGroupDto(
		String name,
		List<String> members
){}
