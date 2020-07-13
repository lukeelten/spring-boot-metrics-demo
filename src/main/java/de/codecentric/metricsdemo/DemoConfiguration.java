package de.codecentric.metricsdemo;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DemoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DemoConfiguration.class);

    public DemoConfiguration(@Autowired MeterRegistry registry) {
        registry.config().meterFilter(MeterFilter.deny(id -> {
            String tag = id.getTag("uri");
            if (tag != null && !tag.isEmpty()) {
                return tag.startsWith("/actuator");
            }

            return false;
        }));
    }

    @Bean
    public DatasourceStatus datasourceStatus(DataSource dataSource) {
        logger.info("called");
        return new DatasourceStatus(dataSource);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
