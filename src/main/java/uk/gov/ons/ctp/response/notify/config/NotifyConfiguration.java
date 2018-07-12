package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.ctp.response.notify.client.GovNotifyClientFactory;
import uk.gov.ons.ctp.response.notify.client.NotificationClientFactory;
import uk.gov.ons.ctp.response.notify.util.BitCalculator;
import uk.gov.service.notify.NotificationClientApi;

/** Configuration specific to GOV.UK Notify */
@Configuration
@ConfigurationProperties("notify")
@Data
public class NotifyConfiguration {
  private Boolean enabled;
  private String debugType;
  private String apiKey;
  private String debugHttpCode;

  // NOTE: only team members or emails explicitly whitelisted with notifications.gov.uk can be used
  // with the override
  // facility (otherwise notifications.gov.uk will reject).  Also, this feature is only for testing
  // purposes.

  // If addressOverride is true, what address should the mails be sent to?
  private String overrideAddress;
  // Should all the emails be sent to a single email address?
  private Boolean addressOverride;

  private String censusUacSmsTemplateId;
  private String onsSurveysRasEmailReminderTemplateId;
  private String endpointBaseUrl;

  /**
   * To set up the GOV.UK Notify NotificationClient
   *
   * @return the notificationClient
   */
  @Bean
  public NotificationClientApi notificationClient() {
    validate();

    NotificationClientFactory factory = new NotificationClientFactory(new GovNotifyClientFactory());

    return factory.getNotificationClient(this);
  }

  /**
   * Method to validate the gov.notify api key and template id. RuntimeException thrown as there
   * really is no point in this service continuing if the keys are bogus
   *
   * @throws RuntimeException
   */
  private void validate() {
    BitCalculator bitCalc = new BitCalculator();

    BitCalculator.KeyInfo keyInfo = bitCalc.analyseNotifyKey(this.apiKey);
    if (!keyInfo.valid) {
      throw new NotifyConfgurationException("Invalid gov.notify API key: " + this.apiKey);
    }

    keyInfo = bitCalc.analyseUUID(this.onsSurveysRasEmailReminderTemplateId);
    if (!keyInfo.valid) {
      throw new NotifyConfgurationException(
          "Invalid email reminder template id: " + this.onsSurveysRasEmailReminderTemplateId);
    }

    keyInfo = bitCalc.analyseUUID(this.censusUacSmsTemplateId);
    if (!keyInfo.valid) {
      throw new NotifyConfgurationException(
          "Invalid census UAC SMS template id: " + this.censusUacSmsTemplateId);
    }
  }
}
