package de.uniks.pioneers.dto;

import java.util.Optional;

public record UpdateUserDto(
		String name,
		String status,
		String avatar,
		String password
) {
	Optional<String> getName(){
		return Optional.ofNullable(name());
	}

	Optional<String> getStatus(){
		return Optional.ofNullable(status());
	}

	Optional<String> getAvatar(){
		return Optional.ofNullable(avatar());
	}

	Optional<String> getPassword(){
		return Optional.ofNullable(password());
	}
}
