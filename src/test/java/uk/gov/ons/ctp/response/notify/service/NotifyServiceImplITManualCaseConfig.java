package uk.gov.ons.ctp.response.notify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.service.notify.NotificationClient;

@PropertySource("classpath:application.yml")
@SpringBootConfiguration
public class NotifyServiceImplITManualCaseConfig {

  @Value("${notify.apiKey}")
  private String apiKey;

  @Value("${notify.templateId}")
  private String templateId;

  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }

  @Bean
  public NotifyConfiguration notifyConfiguration() {
    NotifyConfiguration notifyConfiguration = new NotifyConfiguration();
    notifyConfiguration.setApiKey(apiKey);
    notifyConfiguration.setTemplateId(templateId);
    return notifyConfiguration;
  }

  @Bean
  public NotifyService notifyService() {
    return new NotifyServiceImpl();
  }
}
