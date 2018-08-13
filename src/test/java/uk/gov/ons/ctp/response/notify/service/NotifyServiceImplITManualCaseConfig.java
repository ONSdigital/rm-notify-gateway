package uk.gov.ons.ctp.response.notify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;

@SpringBootConfiguration
public class NotifyServiceImplITManualCaseConfig {

  @Value("${notify.apiKey}")
  private String apiKey;

  @Value("${notify.censusUacSmsTemplateId}")
  private String censusUacSmsTemplateId;

  @Value("${notify.onsSurveysRasEmailReminderTemplateId}")
  private String onsSurveysRasEmailReminderTemplateId;

  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }

  @Bean
  public NotifyConfiguration notifyConfiguration() {
    NotifyConfiguration notifyConfiguration = new NotifyConfiguration();
    notifyConfiguration.setApiKey(apiKey);
    notifyConfiguration.setCensusUacSmsTemplateId(censusUacSmsTemplateId);
    notifyConfiguration.setOnsSurveysRasEmailReminderTemplateId(
        onsSurveysRasEmailReminderTemplateId);
    return notifyConfiguration;
  }

  @Bean
  public NotifyService notifyService() {
    return new NotifyService();
  }
}
