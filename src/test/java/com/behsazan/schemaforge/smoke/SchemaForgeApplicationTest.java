package com.behsazan.schemaforge.smoke;

import com.behsazan.schemaforge.SchemaForgeApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SchemaForgeApplication.class, properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
class SchemaForgeApplicationTest {
    @Test
    void contextLoads() {
    }
}
