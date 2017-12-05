package uk.gov.ons.ctp.response.notify.service.impl;

import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.*;

import java.util.Map;

/**
 * This is a delegate class for NotificationClient that suppresses all calls to the actual NotificationClient if
 * NotifyConfiguration.getEnabled() is false
 */
public class ConfigurationAwareNotificationClient implements NotificationClientApi {

    private NotifyConfiguration configuration;
    private NotificationClientApi realClient;

    public ConfigurationAwareNotificationClient(NotifyConfiguration config, NotificationClientApi realClient) {
        this.configuration = config;
        this.realClient = realClient;
    }

    public ConfigurationAwareNotificationClient(NotifyConfiguration config) {
        this(config, new NotificationClient(config.getApiKey()));
    }

    @Override
    public SendEmailResponse sendEmail(String templateId, String emailAddress, Map<String, String> personalisation, String reference) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.sendEmail(templateId, emailAddress, personalisation, reference);
        } else {
            return null;
        }
    }

    @Override
    public SendSmsResponse sendSms(String templateId, String phoneNumber, Map<String, String> personalisation, String reference) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.sendSms(templateId, phoneNumber, personalisation, reference);
        } else {
            return null;
        }
    }

    @Override
    public Notification getNotificationById(String notificationId) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.getNotificationById(notificationId);
        } else {
            return null;
        }
    }

    @Override
    public NotificationList getNotifications(String status, String notification_type, String reference, String olderThanId) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.getNotifications(status, notification_type, reference, olderThanId);
        } else {
            return null;
        }
    }

    @Override
    public Template getTemplateById(String templateId) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.getTemplateById(templateId);
        } else {
            return null;
        }
    }

    @Override
    public Template getTemplateVersion(String templateId, int version) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.getTemplateVersion(templateId, version);
        } else {
            return null;
        }
    }

    @Override
    public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.getAllTemplates(templateType);
        } else {
            return null;
        }
    }

    @Override
    public TemplatePreview generateTemplatePreview(String templateId, Map<String, String> personalisation) throws NotificationClientException {
        if (this.configuration.getEnabled()) {
            return realClient.generateTemplatePreview(templateId, personalisation);
        } else {
            return null;
        }
    }
}
