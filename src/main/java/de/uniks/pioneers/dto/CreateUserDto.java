package de.uniks.pioneers.dto;

import java.util.Optional;

public record CreateUserDto(
		String name,
		String avatar,
		String password
) {
	Optional<String> getAvatar(){
		return Optional.ofNullable(avatar());
	}
}
