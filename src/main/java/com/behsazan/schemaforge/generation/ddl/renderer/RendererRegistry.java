package com.behsazan.schemaforge.generation.ddl.renderer;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class RendererRegistry {
    private final Map<DatabaseProduct, DdlRenderer> renderers;

    public RendererRegistry(Collection<? extends DdlRenderer> renderers) {
        Objects.requireNonNull(renderers, "renderers must not be null");
        EnumMap<DatabaseProduct, DdlRenderer> indexed = new EnumMap<>(DatabaseProduct.class);
        for (DdlRenderer renderer : renderers) {
            DdlRenderer previous = indexed.putIfAbsent(
                    Objects.requireNonNull(renderer, "renderer must not be null").product(), renderer);
            if (previous != null) {
                throw new IllegalArgumentException(
                        "duplicate renderer for database product " + renderer.product());
            }
        }
        this.renderers = Map.copyOf(indexed);
    }

    public DdlRenderer require(DatabaseProduct product) {
        DdlRenderer renderer = renderers.get(Objects.requireNonNull(product, "product must not be null"));
        if (renderer == null) {
            throw new DdlRenderException("no DDL renderer registered for database product " + product);
        }
        return renderer;
    }

    public boolean supports(DatabaseProduct product) {
        return renderers.containsKey(Objects.requireNonNull(product, "product must not be null"));
    }
}
