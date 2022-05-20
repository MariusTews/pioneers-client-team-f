package de.uniks.pioneers.dto;

import java.util.Optional;

public record UpdateUserDto(
		String name,
		String status,
		String avatar,
		String password
) {}
