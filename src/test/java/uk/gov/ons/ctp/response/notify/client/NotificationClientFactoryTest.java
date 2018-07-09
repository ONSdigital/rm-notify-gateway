package uk.gov.ons.ctp.response.notify.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

public class NotificationClientFactoryTest {
  private static final int IM_A_TEAPOT = 418;
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
  public void testItCreatesConfigurationAwareNotificationClientIfDebugTypeIsNone() {
    config.setDebugType("None");

    assertTrue(getUnwrappedClient() instanceof ConfigurationAwareNotificationClient);
  }

  @Test
  public void
      testItCreatesRuntimeExceptionThrowingNotificationClientIfDebugTypeIsRuntimeException() {
    config.setDebugType("RuntimeException");

    assertTrue(getUnwrappedClient() instanceof RuntimeExceptionThrowingNotificationClient);
  }

  @Test
  public void testItCreatesExceptionThrowingNotificationClientIfDebugTypeIsNotifyClientException() {
    config.setDebugType("NotifyClientException");

    assertTrue(getUnwrappedClient() instanceof ExceptionThrowingNotificationClient);
  }

  @Test
  public void
      testItCreatesExceptionThrowingNotificationClientWithHttpCodeIfDebugHttpCodeIsProvided()
          throws NotificationClientException {
    config.setDebugType("NotifyClientException");
    config.setDebugHttpCode(Integer.toString(IM_A_TEAPOT));

    final NotificationClientApi client = getUnwrappedClient();

    int httpCode = 0;

    try {
      client.getNotificationById("random-id-string");
    } catch (OverridenNotificationClientException e) {
      httpCode = e.getHttpResult();
    }

    assertEquals(IM_A_TEAPOT, httpCode);
  }

  @Test
  public void testItCreatesConfigurationAwareNotificationClientIfDebugTypeIsNull() {
    assertTrue(getUnwrappedClient() instanceof ConfigurationAwareNotificationClient);
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
    return NotificationClientFactory.getNotificationClient(config);
  }
}
