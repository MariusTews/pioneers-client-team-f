package de.uniks.pioneers.model;

@SuppressWarnings("unused")
public record AchievementSummary(
        String id,
        Number started,
        Number unlocked,
        Number progress
) {}
