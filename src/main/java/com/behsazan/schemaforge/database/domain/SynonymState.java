package com.behsazan.schemaforge.database.domain;

public record SynonymState(String name, String targetOwner, String targetName, boolean publicSynonym) {
}
