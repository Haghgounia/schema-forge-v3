package com.behsazan.schemaforge.generation.ddl.renderer;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;

public interface DdlRenderer {

    DatabaseProduct product();

    RenderedDdl render(DdlScript script, RenderContext context);
}
