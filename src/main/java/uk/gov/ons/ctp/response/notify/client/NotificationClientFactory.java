package uk.gov.ons.ctp.response.notify.client;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import jodd.util.StringUtil;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientApi;

/** A simple factory class to create a NotificationClientApi object from a NotifyConfiguration */
public class NotificationClientFactory {
  private static final Logger log = LoggerFactory.getLogger(NotificationClientFactory.class);

  private final GovNotifyClientFactory govNotifyClientFactory;

  public NotificationClientFactory(GovNotifyClientFactory govNotifyClientFactory) {
    this.govNotifyClientFactory = govNotifyClientFactory;
  }

  public NotificationClientApi getNotificationClient(NotifyConfiguration config) {
    NotificationClientApi client;
    String debugTypeString = config.getDebugType();

    client = new LoggingNotificationClient(createClient(config, debugTypeString), log);

    log.with("debug_type", debugTypeString)
        .with("class_name", client.getClass().getCanonicalName())
        .debug("Creating NotificationClientApi");

    return client;
  }

  private NotificationClientApi createClient(NotifyConfiguration config, String debugTypeString) {
    DebugType type = getDebugType(debugTypeString);

    if (type == DebugType.NotifyClientException) {
      return createExceptionThrowingClient(config);
    }

    if (type == DebugType.RuntimeException) {
      return new RuntimeExceptionThrowingNotificationClient();
    }

    return createRealClient(config);
  }

  private DebugType getDebugType(String debugTypeString) {
    DebugType type = DebugType.None;
    if (!StringUtil.isBlank(debugTypeString)) {
      type = DebugType.valueOf(debugTypeString);
    }
    return type;
  }

  private NotificationClientApi createRealClient(NotifyConfiguration config) {
    if (!config.getEnabled()) {
      return new NullNotificationClient();
    }

    return new ConfigurationAwareNotificationClient(config, govNotifyClientFactory.create(config));
  }

  private NotificationClientApi createExceptionThrowingClient(NotifyConfiguration config) {
    String httpCode = config.getDebugHttpCode();

    if (!StringUtil.isBlank(httpCode)) {
      return new ExceptionThrowingNotificationClient(Integer.parseInt(httpCode));
    }

    return new ExceptionThrowingNotificationClient();
  }

  /**
   * An enum to represent the different possible configurations. Parsed from notify.debugType
   * application.yml configuration item (or env etc) None - default, uses
   * ConfigurationAwareNotificationClient NotifyClientException - uses an
   * ExceptionThrowingNotificationClient with an optional supplied httpCode RuntimeException - uses
   * RuntimeExceptionThrowingNotificationClient
   */
  public enum DebugType {
    None,
    NotifyClientException,
    RuntimeException
  }
}
