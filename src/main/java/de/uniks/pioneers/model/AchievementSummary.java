package de.uniks.pioneers.model;

public record AchievementSummary(
        String id,
        Number started,
        Number unlocked,
        Number progress
) {}
