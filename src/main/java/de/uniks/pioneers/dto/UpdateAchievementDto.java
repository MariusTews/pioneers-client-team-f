package de.uniks.pioneers.dto;

@SuppressWarnings("unused")
public record UpdateAchievementDto(
        String unlockedAt,
        Number progress
) {}
