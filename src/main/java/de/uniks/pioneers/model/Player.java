package de.uniks.pioneers.model;

import java.util.HashMap;

public record Player(
        String gameId,
        String userId,
        String color,
        int foundingRoll,
        HashMap<String, Integer> resources,
        HashMap<String, Integer> remainingBuildings
) {}
