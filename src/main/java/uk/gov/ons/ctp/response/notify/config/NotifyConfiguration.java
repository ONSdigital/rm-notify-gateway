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

  @Bean
  public LoggingNotificationClient notificationClient() {
    return enabled ? new LoggingNotificationClient(new NotificationClient(apiKey), log) :
      new LoggingNotificationClient(new NullNotificationClient(), log);
  }
}
