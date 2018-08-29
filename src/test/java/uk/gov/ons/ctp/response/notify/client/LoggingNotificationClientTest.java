package uk.gov.ons.ctp.response.notify.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.godaddy.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import uk.gov.service.notify.*;

public class LoggingNotificationClientTest {

  private static final String NOTIFICATION_ID = "daf85300-6a89-413e-9a32-2f016efc700a";
  private static final String TEMPLATE_ID = "c2fb0d45-0b8a-488d-a324-2a53c00b5405";
  private NotificationClientApi wrappedClient = mock(NotificationClientApi.class);
  private Logger logger = mock(Logger.class);
  private LoggingNotificationClient client = new LoggingNotificationClient(wrappedClient, logger);

  @Before
  public void setUp() {
    when(logger.with(any(), any())).thenReturn(logger);
  }

  @Test
  public void testItIsAnInstanceOfNotificationClientApi() {
    assertTrue(client instanceof NotificationClientApi);
  }

  @Test
  public void testItWrapsSendEmail() throws NotificationClientException {
    final Map<String, String> personalisation = personalisation();
    SendEmailResponse expectedResponse = emailResponse();

    when(wrappedClient.sendEmail("template-id", "email@address.com", personalisation, "reference"))
        .thenReturn(expectedResponse);

    final SendEmailResponse actualResponse =
        client.sendEmail("template-id", "email@address.com", personalisation, "reference");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("sendEmail");
  }

  @Test
  public void testItWrapsSendSms() throws NotificationClientException {
    final Map<String, String> personalisation = personalisation();
    SendSmsResponse expectedResponse = smsResponse();

    when(wrappedClient.sendSms("template-id", "07123456789", personalisation, "reference"))
        .thenReturn(expectedResponse);

    final SendSmsResponse actualResponse =
        client.sendSms("template-id", "07123456789", personalisation, "reference");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("sendSms");
  }

  @Test
  public void testItWrapsGetNotificationId() throws NotificationClientException {
    Notification expectedResponse = returnedNotification();

    when(wrappedClient.getNotificationById("notification-id")).thenReturn(expectedResponse);

    final Notification actualResponse = client.getNotificationById("notification-id");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("getNotificationById");
  }

  @Test
  public void testItWrapsGetNotifications() throws NotificationClientException {
    NotificationList expectedResponse = returnedNotificationList();

    when(wrappedClient.getNotifications("status", "type", "reference", "older-than-id"))
        .thenReturn(expectedResponse);

    final NotificationList actualResponse =
        client.getNotifications("status", "type", "reference", "older-than-id");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("getNotifications");
  }

  @Test
  public void testItWrapsGetTemplateById() throws NotificationClientException {
    Template expectedResponse = returnedTemplate();

    when(wrappedClient.getTemplateById("template-id")).thenReturn(expectedResponse);

    final Template actualResponse = client.getTemplateById("template-id");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("getTemplateById");
  }

  @Test
  public void testItWrapsGetTemplateVersion() throws NotificationClientException {
    Template expectedResponse = returnedTemplate();

    when(wrappedClient.getTemplateVersion("template-id", 2)).thenReturn(expectedResponse);

    final Template actualResponse = client.getTemplateVersion("template-id", 2);

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("getTemplateVersion");
  }

  @Test
  public void testItWrapsGetAllTemplates() throws NotificationClientException {
    TemplateList expectedResponse = returnedTemplateList();

    when(wrappedClient.getAllTemplates("template-type")).thenReturn(expectedResponse);

    final TemplateList actualResponse = client.getAllTemplates("template-type");

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("getAllTemplates");
  }

  @Test
  public void testItWrapsGenerateTemplatePreview() throws NotificationClientException {
    final Map<String, String> personalisation = personalisation();
    TemplatePreview expectedResponse = returnedTemplatePreview();

    when(wrappedClient.generateTemplatePreview("template-id", personalisation))
        .thenReturn(expectedResponse);

    final TemplatePreview actualResponse =
        client.generateTemplatePreview("template-id", personalisation);

    assertEquals(expectedResponse, actualResponse);
    verify(logger).debug("generateTemplatePreview");
  }

  @Test
  public void testGetClient() {
    assertEquals(wrappedClient, client.getClient());
  }

  private Map<String, String> personalisation() {
    final Map<String, String> personalisations = new HashMap<>();
    personalisations.put("name", "example");
    return personalisations;
  }

  private SendEmailResponse emailResponse() {
    return new SendEmailResponse(
        "{"
            + "\"id\": \""
            + NOTIFICATION_ID
            + "\","
            + "\"content\": {\"body\": \"email content\", \"fromEmail\": \"sender@email.com\", \"subject\": \"test email\"},"
            + "\"template\": {\"id\": \""
            + TEMPLATE_ID
            + "\", \"version\": 1, \"uri\": \"template-uri\"}"
            + "}");
  }

  private SendSmsResponse smsResponse() {
    return new SendSmsResponse(
        "{"
            + "\"id\": \""
            + NOTIFICATION_ID
            + "\","
            + "\"content\": {\"body\": \"email content\", \"fromEmail\": \"sender@email.com\", \"subject\": \"test email\"},"
            + "\"template\": {\"id\": \""
            + TEMPLATE_ID
            + "\", \"version\": 1, \"uri\": \"template-uri\"}"
            + "}");
  }

  private Notification returnedNotification() {
    return new Notification(
        "{"
            + "\"id\": \""
            + NOTIFICATION_ID
            + "\","
            + "\"type\": \"email\","
            + "\"body\": \"email content\","
            + "\"status\": \"pending\","
            + "\"created_at\": \"2018-07-09\","
            + "\"template\": {\"id\": \""
            + TEMPLATE_ID
            + "\", \"version\": 1, \"uri\": \"template-uri\"}"
            + "}");
  }

  private NotificationList returnedNotificationList() {
    return new NotificationList("{\"links\": {\"current\": \"xxx\"}, \"notifications\": []}");
  }

  private Template returnedTemplate() {
    return new Template(
        "{\"id\": \""
            + TEMPLATE_ID
            + "\","
            + "\"type\": \"email\","
            + "\"created_at\": \"2018-07-09\","
            + "\"version\": 1,"
            + "\"body\": \"template-body\""
            + "}");
  }

  private TemplateList returnedTemplateList() {
    return new TemplateList("{\"templates\": []}");
  }

  private TemplatePreview returnedTemplatePreview() {
    return new TemplatePreview(
        "{"
            + "\"id\": \"1af5821d-5e35-4929-9826-71b3d3e5684e\","
            + "\"type\": \"email\","
            + "\"version\": 3,"
            + "\"body\": \"the-body\""
            + "}");
  }
}
