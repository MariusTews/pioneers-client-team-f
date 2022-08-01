package de.uniks.pioneers.dto;

import de.uniks.pioneers.template.HarborTemplate;
import de.uniks.pioneers.template.TileTemplate;

import java.util.List;

@SuppressWarnings("unused")
public record CreateMapTemplateDto(
        String name,
        String icon,
        List<TileTemplate> tiles,
        List<HarborTemplate> harbors
) {}
