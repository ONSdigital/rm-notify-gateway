package uk.gov.ons.ctp.response.notify.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.service.notify.NotificationClient;

/**
 * Configuration specific to GOV.UK Notify
 */
@Configuration
public class NotifyConfiguration {
  @Value("${API_KEY}")
  private String apiKey;

  /**
   * To set up the GOV.UK Notify notificationClient
   * @return the notificationClient
   */
  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }
}
