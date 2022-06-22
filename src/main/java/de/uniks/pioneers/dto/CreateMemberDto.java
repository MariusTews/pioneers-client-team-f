package de.uniks.pioneers.dto;


public record CreateMemberDto(
		boolean ready,
		String color,
		boolean spectator,
		String password
) {}
