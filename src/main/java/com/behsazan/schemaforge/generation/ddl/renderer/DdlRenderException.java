package com.behsazan.schemaforge.generation.ddl.renderer;

public final class DdlRenderException extends RuntimeException {

    public DdlRenderException(String message) {
        super(message);
    }

    public DdlRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
