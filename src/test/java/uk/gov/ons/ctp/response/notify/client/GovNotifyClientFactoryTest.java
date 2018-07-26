package uk.gov.ons.ctp.response.notify.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;

public class GovNotifyClientFactoryTest {

  private static final String API_KEY = "apikey";

  private NotifyConfiguration config;
  private GovNotifyClientFactory factory = new GovNotifyClientFactory();

  @Before
  public void setUp() {
    config = new NotifyConfiguration();
    config.setApiKey(API_KEY);
  }

  @Test
  public void testCreatesAnInstanceOfNotificationClient() {
    assertTrue(factory.create(config) instanceof NotificationClient);
  }

  @Test
  public void testItSetsTheApiKey() {
    final NotificationClient client = (NotificationClient) factory.create(config);

    assertEquals(API_KEY, client.getApiKey());
  }

  @Test
  public void testItSetsTheBaseURLIfProvided() {
    config.setEndpointBaseUrl("http://testurl.com");

    final NotificationClient client = (NotificationClient) factory.create(config);

    assertEquals("http://testurl.com", client.getBaseUrl());
  }

  @Test
  public void testItSetsStripsTrailingSlashesOffTheBaseURL() {
    config.setEndpointBaseUrl("http://testurl.com/");

    final NotificationClient client = (NotificationClient) factory.create(config);

    assertEquals("http://testurl.com", client.getBaseUrl());
  }

  @Test
  public void testItUsesTheDefaultBaseURLIfOneIsNotProvided() {
    final NotificationClient client = (NotificationClient) factory.create(config);

    assertNotNull(client.getBaseUrl());
  }
}
