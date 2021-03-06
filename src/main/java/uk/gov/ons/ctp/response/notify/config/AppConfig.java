package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Application Config bean */
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private CommsTemplateService commsTemplateService;
}
