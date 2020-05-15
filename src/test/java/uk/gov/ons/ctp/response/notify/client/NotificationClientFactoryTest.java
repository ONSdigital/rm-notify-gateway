package uk.gov.ons.ctp.response.notify.client;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientApi;

public class NotificationClientFactoryTest {
  private NotifyConfiguration config;

  @Before
  public void setUp() {
    config = new NotifyConfiguration();
    config.setEnabled(true);
    config.setApiKey("api-key");
  }

  @Test
  public void testItReturnsALoggingClient() {
    assertTrue(getNotificationClient() instanceof LoggingNotificationClient);
  }

  @Test
  public void testItCreatesANullNotificationClientIfDisabled() {
    config.setEnabled(false);

    assertTrue(getUnwrappedClient() instanceof NullNotificationClient);
  }

  private NotificationClientApi getUnwrappedClient() {
    return ((LoggingNotificationClient) getNotificationClient()).getClient();
  }

  private NotificationClientApi getNotificationClient() {
    NotificationClientFactory factory = new NotificationClientFactory();

    return factory.getNotificationClient(config);
  }
}
