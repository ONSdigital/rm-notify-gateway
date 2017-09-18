package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;

/**
 * Configuration specific to GOV.UK Notify
 */
@Configuration
@ConfigurationProperties("notify")
@Data
public class NotifyConfiguration {
  private String apiKey;

  private String censusUacSmsTemplateId;
  private String onsSurveysRasEmailReminderTemplateId;

  /**
   * To set up the GOV.UK Notify NotificationClient
   * @return the notificationClient
   */
  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }
}
