package de.uniks.pioneers.dto;


@SuppressWarnings("unused")
public record UpdateMemberDto(
		boolean ready,
		String color,
		boolean spectator
) {}
