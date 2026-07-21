package com.behsazan.schemaforge.generation.plugin;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DatabaseDdlPluginRegistry {
    private final Map<DatabaseProduct, DatabaseDdlPlugin> plugins;

    public DatabaseDdlPluginRegistry(Collection<? extends DatabaseDdlPlugin> plugins) {
        Objects.requireNonNull(plugins, "plugins must not be null");
        EnumMap<DatabaseProduct, DatabaseDdlPlugin> indexed = new EnumMap<>(DatabaseProduct.class);
        for (DatabaseDdlPlugin plugin : plugins) {
            Objects.requireNonNull(plugin, "plugin must not be null");
            DatabaseDdlPlugin previous = indexed.putIfAbsent(plugin.product(), plugin);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate DDL plugin for " + plugin.product());
            }
        }
        this.plugins = Map.copyOf(indexed);
    }

    public DatabaseDdlPlugin require(DatabaseProduct product) {
        Objects.requireNonNull(product, "product must not be null");
        DatabaseDdlPlugin plugin = plugins.get(product);
        if (plugin == null) throw new IllegalArgumentException("No DDL plugin registered for " + product);
        return plugin;
    }

    public Set<DatabaseProduct> registeredProducts() { return plugins.keySet(); }
}
