package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.ons.ctp.response.notify.client.LoggingNotificationClient;
import uk.gov.ons.ctp.response.notify.client.NullNotificationClient;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;

/** Configuration specific to GOV.UK Notify */
@Configuration
@ConfigurationProperties("notify")
@Data
public class NotifyConfiguration {

  private static final Logger log = LoggerFactory.getLogger(NotifyConfiguration.class);
  @Value("${notify.enabled}")
  private Boolean enabled;
  @Value("${notify.apiKey}")
  private String apiKey;

  /**
   * To set up the GOV.UK Notify NotificationClient
   *
   * @return the notificationClient
   */
  @Bean
  public NotificationClientApi notificationClient() {
    if (enabled) {
      return nullNotificationClient();
    }
    return loggingNotificationClient();
  }

  private NullNotificationClient nullNotificationClient() {
    return new NullNotificationClient();
  }

  private LoggingNotificationClient loggingNotificationClient() {
    return new LoggingNotificationClient(new NotificationClient(apiKey), log);
  }
}
