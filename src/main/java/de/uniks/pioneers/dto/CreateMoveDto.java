package de.uniks.pioneers.dto;

import java.util.HashMap;

public record CreateMoveDto(
        String action,
		RobDto rob,
		HashMap<String, Integer> resources,
		String partner,
        CreateBuildingDto building
) {}
