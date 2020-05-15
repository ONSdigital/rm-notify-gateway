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
    if (!config.getEnabled()) {
      return new NullNotificationClient();
    }
    NotificationClientApi client = new LoggingNotificationClient(new NotificationClient(config.getApiKey()), log);

    log.with("class_name", client.getClass().getCanonicalName())
        .debug("Creating NotificationClientApi");

    return client;
  }
}
