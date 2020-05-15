package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;

public class ConfigurationAwareNotificationClient extends NotificationClientDecorator {

  private final NotifyConfiguration configuration;

  public ConfigurationAwareNotificationClient(
      NotifyConfiguration config, NotificationClientApi realClient) {
    super(realClient);
    this.configuration = config;
  }

  public ConfigurationAwareNotificationClient(NotifyConfiguration config) {
    this(config, new NotificationClient(config.getApiKey()));
  }
}
