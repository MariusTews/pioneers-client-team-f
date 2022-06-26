package de.uniks.pioneers.dto;

public record CreateMoveDto(
        String action,
		RobDto rob,
		ResourcesDto resources,
		String partner,
        CreateBuildingDto building
) {}
