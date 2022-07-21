package de.uniks.pioneers.dto;

import java.util.HashMap;

@SuppressWarnings("unused")
public record CreateMoveDto(
        String action,
		RobDto rob,
		HashMap<String, Integer> resources,
		String partner,
		String developmentCard,
        CreateBuildingDto building
) {}
