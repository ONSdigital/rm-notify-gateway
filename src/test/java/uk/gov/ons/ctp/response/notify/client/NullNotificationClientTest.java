package uk.gov.ons.ctp.response.notify.client;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import uk.gov.service.notify.NotificationClientException;

public class NullNotificationClientTest {
  private NullNotificationClient client = new NullNotificationClient();

  @Test
  public void testSendEmailReturnsNull() throws NotificationClientException {

    assertNull(
        client.sendEmail("template-id", "email@address.com", personalisation(), "reference"));
  }

  @Test
  public void testSendSmsReturnsNull() throws NotificationClientException {

    assertNull(client.sendSms("template-id", "07123456789", personalisation(), "reference"));
  }

  @Test
  public void testGetNotificationIdReturnsNull() throws NotificationClientException {
    assertNull(client.getNotificationById("notification-id"));
  }

  @Test
  public void testGetNotificationsReturnsNull() throws NotificationClientException {
    assertNull(client.getNotifications("status", "type", "reference", "older-than-id"));
  }

  @Test
  public void testGetTemplateByIdReturnsNull() throws NotificationClientException {
    assertNull(client.getTemplateById("template-id"));
  }

  @Test
  public void testGetTemplateVersionReturnsNull() throws NotificationClientException {
    assertNull(client.getTemplateVersion("template-id", 2));
  }

  @Test
  public void testGetAllTemplatesReturnsNull() throws NotificationClientException {
    assertNull(client.getAllTemplates("template-type"));
  }

  @Test
  public void testGenerateTemplatePreviewReturnsNull() throws NotificationClientException {
    assertNull(client.generateTemplatePreview("template-id", personalisation()));
  }

  private Map<String, String> personalisation() {
    final Map<String, String> personalisations = new HashMap<>();
    personalisations.put("name", "example");
    return personalisations;
  }
}
