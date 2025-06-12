package com.example.graphqlbookstore.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Configuration class for GraphQL scalar types.
 * This class registers custom scalar types like DateTime (ZonedDateTime).
 */
@Configuration
public class GraphQLConfig {

    /**
     * Configures the GraphQL runtime wiring to include custom scalar types.
     * 
     * @return RuntimeWiringConfigurer with custom scalar types registered
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.DateTime) // Register ZonedDateTime scalar
                .scalar(ExtendedScalars.GraphQLLong) // Register Long scalar
                .scalar(ExtendedScalars.GraphQLBigDecimal); // Register BigDecimal scalar
    }
}
