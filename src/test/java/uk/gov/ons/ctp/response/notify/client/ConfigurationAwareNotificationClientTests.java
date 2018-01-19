package uk.gov.ons.ctp.response.notify.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.notify.client.ConfigurationAwareNotificationClient;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationAwareNotificationClientTests {
    private static final String DUMMY_API_KEY = "dummykey-ffffffff-ffff-ffff-ffff-ffffffffffff-ffffffff-ffff-ffff-ffff-ffffffffffff";
    private static final String DUMMY_TEMPLATE_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";

    @Mock
    private NotificationClient realNotificationClient;

    @Mock
    private NotifyConfiguration notifyConfiguration;

    private NotificationClientApi testNotificationClient;

    @Before
    public void setUp(){
        when(notifyConfiguration.getApiKey()).thenReturn(DUMMY_API_KEY);
        when(notifyConfiguration.getCensusUacSmsTemplateId()).thenReturn(DUMMY_TEMPLATE_ID);
        when(notifyConfiguration.getOnsSurveysRasEmailReminderTemplateId()).thenReturn(DUMMY_TEMPLATE_ID);

        this.testNotificationClient = new ConfigurationAwareNotificationClient(this.notifyConfiguration, this.realNotificationClient);
    }

    @Test
    public void testGenerateTemplatePreviewEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.generateTemplatePreview(null, null);
        verify(this.realNotificationClient, times(1)).generateTemplatePreview(any(), any());
    }

    @Test
    public void testGetAllTemplatesEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.getAllTemplates(null);
        verify(this.realNotificationClient, times(1)).getAllTemplates(any());
    }

    @Test
    public void testGetNotificationByIdEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.getNotificationById(null);
        verify(this.realNotificationClient, times(1)).getNotificationById(any());
    }

    @Test
    public void testGetNotificationsEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.getNotifications(null, null, null, null);
        verify(this.realNotificationClient, times(1)).getNotifications(any(), any(), any(), any());
    }

    @Test
    public void testGetTemplateByIdEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.getTemplateById(null);
        verify(this.realNotificationClient, times(1)).getTemplateById(any());
    }

    @Test
    public void testGetTemplateVersionEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.getTemplateVersion(null, 0);
        verify(this.realNotificationClient, times(1)).getTemplateVersion(any(), anyInt());
    }

    @Test
    public void testSendEmailEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.sendEmail(null, null, null, null);
        verify(this.realNotificationClient, times(1)).sendEmail(any(), any(), any(), any());
    }

    @Test
    public void testSendSmsEnabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(true);

        this.testNotificationClient.sendSms(null, null, null, null);
        verify(this.realNotificationClient, times(1)).sendSms(any(), any(), any(), any());
    }

    @Test
    public void testGenerateTemplatePreviewDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.generateTemplatePreview(null, null);
        verify(this.realNotificationClient, never()).generateTemplatePreview(any(), any());
    }

    @Test
    public void testGetAllTemplatesDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.getAllTemplates(null);
        verify(this.realNotificationClient, never()).getAllTemplates(any());
    }

    @Test
    public void testGetNotificationByIdDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.getNotificationById(null);
        verify(this.realNotificationClient, never()).getNotificationById(any());
    }

    @Test
    public void testGetNotificationsDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.getNotifications(null, null, null, null);
        verify(this.realNotificationClient, never()).getNotifications(any(), any(), any(), any());
    }

    @Test
    public void testGetTemplateByIdDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.getTemplateById(null);
        verify(this.realNotificationClient, never()).getTemplateById(any());
    }

    @Test
    public void testGetTemplateVersionDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.getTemplateVersion(null, 0);
        verify(this.realNotificationClient, never()).getTemplateVersion(any(), anyInt());
    }

    @Test
    public void testSendEmailDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.sendEmail(null, null, null, null);
        verify(this.realNotificationClient, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    public void testSendSmsDisabled() throws NotificationClientException {
        when(notifyConfiguration.getEnabled()).thenReturn(false);

        this.testNotificationClient.sendSms(null, null, null, null);
        verify(this.realNotificationClient, never()).sendSms(any(), any(), any(), any());
    }
}
