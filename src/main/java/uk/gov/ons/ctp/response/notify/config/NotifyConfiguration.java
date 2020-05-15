package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.ctp.response.notify.client.NotificationClientFactory;
import uk.gov.service.notify.NotificationClientApi;

/** Configuration specific to GOV.UK Notify */
@Configuration
@ConfigurationProperties("notify")
@Data
public class NotifyConfiguration {
  private Boolean enabled;
  private String apiKey;
  private String debugHttpCode;

  // NOTE: only team members or emails explicitly whitelisted with notifications.gov.uk can be used
  // with the override
  // facility (otherwise notifications.gov.uk will reject).  Also, this feature is only for testing
  // purposes.

  private String onsSurveysRasEmailReminderTemplateId;

  /**
   * To set up the GOV.UK Notify NotificationClient
   *
   * @return the notificationClient
   */
  @Bean
  public NotificationClientApi notificationClient() {
    NotificationClientFactory factory = new NotificationClientFactory();

    return factory.getNotificationClient(this);
  }
}
