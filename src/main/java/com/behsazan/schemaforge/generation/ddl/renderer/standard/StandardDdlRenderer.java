package com.behsazan.schemaforge.generation.ddl.renderer.standard;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.renderer.AbstractDdlRenderer;

public final class StandardDdlRenderer extends AbstractDdlRenderer {

    @Override
    public DatabaseProduct product() {
        return DatabaseProduct.STANDARD;
    }
}
