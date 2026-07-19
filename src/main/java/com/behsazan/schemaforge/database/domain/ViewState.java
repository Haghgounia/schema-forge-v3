package com.behsazan.schemaforge.database.domain;

public record ViewState(String name, String query, boolean materialized) {
}
