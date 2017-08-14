package ro.axonsoft.internship172.data.tests;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

public class RecrutareTestsConfigBase {

    @Bean
    public TestDbUtil testDbUtil(final DataSource dataSource) {
        return new TestDbUtil(dataSource);
    }
}
