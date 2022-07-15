package de.uniks.pioneers.dto;

@SuppressWarnings("unused")
public record CreateBuildingDto(
        Number x,
        Number y,
        Number z,
        Number side,
        String type
) {}
