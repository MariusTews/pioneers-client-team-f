package de.uniks.pioneers.dto;


import javafx.scene.paint.Color;

public record CreateMemberDto(
		boolean ready,
		Color color,
		String password
) {}
