package uk.gov.ons.ctp.response.notify.client;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;

/** A simple factory class to create a NotificationClientApi object from a NotifyConfiguration */
public class NotificationClientFactory {
  private static final Logger log = LoggerFactory.getLogger(NotificationClientFactory.class);

  public NotificationClientApi getNotificationClient(NotifyConfiguration config) {
    NotificationClientApi client;

    client = new LoggingNotificationClient(createRealClient(config), log);

    log.with("class_name", client.getClass().getCanonicalName())
        .debug("Creating NotificationClientApi");

    return client;
  }

  private NotificationClientApi createRealClient(NotifyConfiguration config) {
    if (!config.getEnabled()) {
      return new NullNotificationClient();
    }
    return new NotificationClient(config.getApiKey());
  }
}
