package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.ctp.response.notify.client.NotificationClientFactory;
import uk.gov.ons.ctp.response.notify.util.BitCalculator;
import uk.gov.service.notify.NotificationClientApi;

/**
 * Configuration specific to GOV.UK Notify
 */
@Configuration
@ConfigurationProperties("notify")
@Data
public class NotifyConfiguration {
  private Boolean enabled;
  private String debugType;
  private String apiKey;
  private String debugHttpCode;

  private String censusUacSmsTemplateId;
  private String onsSurveysRasEmailReminderTemplateId;

  /**
   * To set up the GOV.UK Notify NotificationClient
   * @return the notificationClient
   */
  @Bean
  public NotificationClientApi notificationClient() {
    //TODO: Remove all of the associated notification validation for keys etc,
    // TODO: superceded by use of comms template service
    validate();

    return NotificationClientFactory.getNotificationClient(this);
  }

  /**
   * Method to validate the gov.notify api key and template id.  RuntimeException thrown as there really is no point
   * in this service continuing if the keys are bogus
   * @throws RuntimeException
   */
  private void validate(){
    BitCalculator bitCalc = new BitCalculator();

    BitCalculator.KeyInfo keyInfo = bitCalc.analyseNotifyKey(this.apiKey);
    if (!keyInfo.valid){
      throw new NotifyConfgurationException("Invalid gov.notify API key: " + this.apiKey);
    }

    keyInfo = bitCalc.analyseUUID(this.onsSurveysRasEmailReminderTemplateId);
    if (!keyInfo.valid) {
      throw new NotifyConfgurationException("Invalid email reminder template id: " + this.onsSurveysRasEmailReminderTemplateId);
    }

    keyInfo = bitCalc.analyseUUID(this.censusUacSmsTemplateId);
    if (!keyInfo.valid){
      throw new NotifyConfgurationException("Invalid census UAC SMS template id: " + this.censusUacSmsTemplateId);
    }
  }
}
