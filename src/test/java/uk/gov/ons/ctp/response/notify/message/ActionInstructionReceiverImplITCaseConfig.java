package uk.gov.ons.ctp.response.notify.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.sleuth.DefaultSpanNamer;
import org.springframework.cloud.sleuth.NoOpSpanReporter;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.log.NoOpSpanLogger;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Random;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:springintegration/ActionInstructionReceiverImplITCase-context.xml" })
public class ActionInstructionReceiverImplITCaseConfig {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigInTest() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public Tracer tracer() {
    return new DefaultTracer(new AlwaysSampler(), new Random(), new DefaultSpanNamer(), new NoOpSpanLogger(), new NoOpSpanReporter());
  }
}
