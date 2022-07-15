package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.GameSettings;

@SuppressWarnings("unused")
public record CreateGameDto(
		String name,
		boolean started,
		GameSettings settings,
		String password
) {}
