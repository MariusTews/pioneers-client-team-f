package de.uniks.pioneers.dto;

import javafx.scene.paint.Color;

public record UpdateMemberDto(
		boolean ready,
		Color color
) {}
