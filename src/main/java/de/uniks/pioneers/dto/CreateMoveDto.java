package de.uniks.pioneers.dto;

public record CreateMoveDto(
        String action,
        CreateBuildingDto building
) {}
