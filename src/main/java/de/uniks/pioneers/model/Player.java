package de.uniks.pioneers.model;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public record Player(
        String gameId,
        String userId,
        String color,
		boolean active,
        int foundingRoll,
        HashMap<String, Integer> resources,
        HashMap<String, Integer> remainingBuildings,
		Number victoryPoints,
		Number longestRoad,
		// swagger documentation is not clear about the data type required for previousTradeOffer,
		// therefore a generic list of objects is used that might need to be changed later.
		List<Object> previousTradeOffer
) {}
