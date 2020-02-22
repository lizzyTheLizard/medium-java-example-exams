package lizzy.medium.security.exams.model;

import lizzy.medium.security.exams.config.SecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"lizzy.medium.security.exams"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {SecurityConfig.class})})
public class TestConfig {
}