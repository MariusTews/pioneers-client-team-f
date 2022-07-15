package de.uniks.pioneers.dto;


@SuppressWarnings("unused")
public record CreateMemberDto(
		boolean ready,
		String color,
		boolean spectator,
		String password
) {}
