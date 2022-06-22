package de.uniks.pioneers.model;

import de.uniks.pioneers.dto.RobDto;

import java.util.HashMap;

public record Move(
		String createdAt,
        String _id,
        String gameId,
        String userId,
        String action,
        int roll,
        String building,
		RobDto rob,
		HashMap<String, Integer> resources,
		String partner
) {}
