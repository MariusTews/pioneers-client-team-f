package de.uniks.pioneers.dto;

@SuppressWarnings("unused")
public record CreateAchievementDto(
        String unlockedAt,
        Number progress
) {}
