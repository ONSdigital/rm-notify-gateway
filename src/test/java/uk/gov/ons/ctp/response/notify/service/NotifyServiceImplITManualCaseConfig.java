package uk.gov.ons.ctp.response.notify.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.service.notify.NotificationClient;

@SpringBootConfiguration
public class NotifyServiceImplITManualCaseConfig {

  @Value("${API_KEY}")
  private String apiKey;

  @Bean
  public NotificationClient notificationClient() {
    return new NotificationClient(apiKey);
  }

  @Bean
  public NotifyService notifyService() {
    return new NotifyServiceImpl();
  }
}
