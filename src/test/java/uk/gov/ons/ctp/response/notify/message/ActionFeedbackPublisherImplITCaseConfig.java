package uk.gov.ons.ctp.response.notify.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:ActionFeedbackPublisherImplITCase-context.xml" })
public class ActionFeedbackPublisherImplITCaseConfig {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
