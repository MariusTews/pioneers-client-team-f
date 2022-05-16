package de.uniks.pioneers.dto;

import java.util.List;
import java.util.Optional;

public record CreateGroupDto(
		String name,
		List<String> members
) {
	Optional<String> getName(){return Optional.ofNullable(name());}
}
