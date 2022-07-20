package de.uniks.pioneers.Template;

import java.util.List;

public record MapTemplate(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String icon,
        String createdBy,
        Number votes,
        List<TileTemplate> tiles,
        List<HarborTemplate> harbors
) {}
