package de.uniks.pioneers.dto;


@SuppressWarnings("unused")
public record CreateUserDto(
		String name,
		String avatar,
		String password
) {}
