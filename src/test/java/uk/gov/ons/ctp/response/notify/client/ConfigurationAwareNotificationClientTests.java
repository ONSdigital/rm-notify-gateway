package uk.gov.ons.ctp.response.notify.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationAwareNotificationClientTests {
  private static final String DUMMY_API_KEY =
      "dummykey-ffffffff-ffff-ffff-ffff-ffffffffffff-ffffffff-ffff-ffff-ffff-ffffffffffff";
  private static final String DUMMY_TEMPLATE_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";

  @Mock private NotificationClient realNotificationClient;

  @Mock private NotifyConfiguration notifyConfiguration;

  private NotificationClientApi testNotificationClient;

  @Before
  public void setUp() {
    when(notifyConfiguration.getApiKey()).thenReturn(DUMMY_API_KEY);
    when(notifyConfiguration.getCensusUacSmsTemplateId()).thenReturn(DUMMY_TEMPLATE_ID);
    when(notifyConfiguration.getOnsSurveysRasEmailReminderTemplateId())
        .thenReturn(DUMMY_TEMPLATE_ID);

    this.testNotificationClient =
        new ConfigurationAwareNotificationClient(
            this.notifyConfiguration, this.realNotificationClient);
  }

  @Test
  public void testGenerateTemplatePreview() throws NotificationClientException {
    this.testNotificationClient.generateTemplatePreview(null, null);
    verify(this.realNotificationClient, times(1)).generateTemplatePreview(any(), any());
  }

  @Test
  public void testGetAllTemplates() throws NotificationClientException {
    this.testNotificationClient.getAllTemplates(null);
    verify(this.realNotificationClient, times(1)).getAllTemplates(any());
  }

  @Test
  public void testGetNotificationById() throws NotificationClientException {
    this.testNotificationClient.getNotificationById(null);
    verify(this.realNotificationClient, times(1)).getNotificationById(any());
  }

  @Test
  public void testGetNotifications() throws NotificationClientException {
    this.testNotificationClient.getNotifications(null, null, null, null);
    verify(this.realNotificationClient, times(1)).getNotifications(any(), any(), any(), any());
  }

  @Test
  public void testGetTemplateById() throws NotificationClientException {
    this.testNotificationClient.getTemplateById(null);
    verify(this.realNotificationClient, times(1)).getTemplateById(any());
  }

  @Test
  public void testGetTemplateVersion() throws NotificationClientException {
    this.testNotificationClient.getTemplateVersion(null, 0);
    verify(this.realNotificationClient, times(1)).getTemplateVersion(any(), anyInt());
  }

  @Test
  public void testSendEmail() throws NotificationClientException {
    this.testNotificationClient.sendEmail(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendEmail(any(), any(), any(), any());
  }

  @Test
  public void testSendSms() throws NotificationClientException {
    this.testNotificationClient.sendSms(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendSms(any(), any(), any(), any());
  }
}
