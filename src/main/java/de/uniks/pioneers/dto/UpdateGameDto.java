package de.uniks.pioneers.dto;

import de.uniks.pioneers.model.GameSettings;

@SuppressWarnings("unused")
public record UpdateGameDto(
		String name,
		String owner,
		boolean started,
		GameSettings settings,
		String password
) {}
