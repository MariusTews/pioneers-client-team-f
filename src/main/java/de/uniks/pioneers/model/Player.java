package de.uniks.pioneers.model;

public record Player(
        String gameId,
        String userId,
        String color,
        int foundingRoll,
        int[] resources,
        int[] remainingBuildings
) {}
