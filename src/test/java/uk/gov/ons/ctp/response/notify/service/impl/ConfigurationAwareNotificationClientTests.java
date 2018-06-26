package uk.gov.ons.ctp.response.notify.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
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
  public void setup() {
    when(notifyConfiguration.getApiKey()).thenReturn(DUMMY_API_KEY);
    when(notifyConfiguration.getCensusUacSmsTemplateId()).thenReturn(DUMMY_TEMPLATE_ID);
    when(notifyConfiguration.getOnsSurveysRasEmailReminderTemplateId())
        .thenReturn(DUMMY_TEMPLATE_ID);

    this.testNotificationClient =
        new ConfigurationAwareNotificationClient(
            this.notifyConfiguration, this.realNotificationClient);
  }

  @Test
  public void testAllApiEnabled() throws NotificationClientException {
    when(notifyConfiguration.getEnabled()).thenReturn(true);

    this.testNotificationClient.generateTemplatePreview(null, null);
    verify(this.realNotificationClient, times(1)).generateTemplatePreview(any(), any());
    this.testNotificationClient.getAllTemplates(null);
    verify(this.realNotificationClient, times(1)).getAllTemplates(any());
    this.testNotificationClient.getNotificationById(null);
    verify(this.realNotificationClient, times(1)).getNotificationById(any());
    this.testNotificationClient.getNotifications(null, null, null, null);
    verify(this.realNotificationClient, times(1)).getNotifications(any(), any(), any(), any());
    this.testNotificationClient.getTemplateById(null);
    verify(this.realNotificationClient, times(1)).getTemplateById(any());
    this.testNotificationClient.getTemplateVersion(null, 0);
    verify(this.realNotificationClient, times(1)).getTemplateVersion(any(), anyInt());
    this.testNotificationClient.sendEmail(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendEmail(any(), any(), any(), any());
    this.testNotificationClient.sendSms(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendSms(any(), any(), any(), any());
  }

  @Test
  public void testAllApiDisabled() throws NotificationClientException {
    when(notifyConfiguration.getEnabled()).thenReturn(false);

    this.testNotificationClient.generateTemplatePreview(null, null);
    verify(this.realNotificationClient, never()).generateTemplatePreview(any(), any());
    this.testNotificationClient.getAllTemplates(null);
    verify(this.realNotificationClient, never()).getAllTemplates(any());
    this.testNotificationClient.getNotificationById(null);
    verify(this.realNotificationClient, never()).getNotificationById(any());
    this.testNotificationClient.getNotifications(null, null, null, null);
    verify(this.realNotificationClient, never()).getNotifications(any(), any(), any(), any());
    this.testNotificationClient.getTemplateById(null);
    verify(this.realNotificationClient, never()).getTemplateById(any());
    this.testNotificationClient.getTemplateVersion(null, 0);
    verify(this.realNotificationClient, never()).getTemplateVersion(any(), anyInt());
    this.testNotificationClient.sendEmail(null, null, null, null);
    verify(this.realNotificationClient, never()).sendEmail(any(), any(), any(), any());
    this.testNotificationClient.sendSms(null, null, null, null);
    verify(this.realNotificationClient, never()).sendSms(any(), any(), any(), any());
  }

  @Test
  public void testOverrideEnabled() throws NotificationClientException {
    final String email = "test@test.com";
    when(notifyConfiguration.getEnabled()).thenReturn(true);
    when(notifyConfiguration.getAddressOverride()).thenReturn(Boolean.TRUE);
    when(notifyConfiguration.getOverrideAddress()).thenReturn(email);

    this.testNotificationClient.generateTemplatePreview(null, null);
    verify(this.realNotificationClient, times(1)).generateTemplatePreview(any(), any());
    this.testNotificationClient.getAllTemplates(null);
    verify(this.realNotificationClient, times(1)).getAllTemplates(any());
    this.testNotificationClient.getNotificationById(null);
    verify(this.realNotificationClient, times(1)).getNotificationById(any());
    this.testNotificationClient.getNotifications(null, null, null, null);
    verify(this.realNotificationClient, times(1)).getNotifications(any(), any(), any(), any());
    this.testNotificationClient.getTemplateById(null);
    verify(this.realNotificationClient, times(1)).getTemplateById(any());
    this.testNotificationClient.getTemplateVersion(null, 0);
    verify(this.realNotificationClient, times(1)).getTemplateVersion(any(), anyInt());
    this.testNotificationClient.sendEmail(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendEmail(any(), eq(email), any(), any());
    this.testNotificationClient.sendSms(null, null, null, null);
    verify(this.realNotificationClient, times(1)).sendSms(any(), any(), any(), any());
  }
}
