package com.stridehub.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FlywayMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyBaselineMigrationsAndSeedRoles() {
        assertThat(tableExists("users")).isTrue();
        assertThat(tableExists("products")).isTrue();
        assertThat(tableExists("inventory_items")).isTrue();
        assertThat(tableExists("orders")).isTrue();
        assertThat(tableExists("seller_profiles")).isTrue();
        assertThat(tableExists("audit_logs")).isTrue();
        assertThat(jdbcTemplate.queryForObject("select count(*) from roles", Integer.class))
                .isEqualTo(3);
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.tables
                where lower(table_name) = ?
                """,
                Integer.class
                ,
                tableName
        );
        return count != null && count > 0;
    }
}
