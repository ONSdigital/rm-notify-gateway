package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;
import uk.gov.service.notify.Template;
import uk.gov.service.notify.TemplateList;
import uk.gov.service.notify.TemplatePreview;

/**
 * This is a delegate class for NotificationClient that suppresses all calls to the actual
 * NotificationClient if NotifyConfiguration.getEnabled() is false
 */
@Slf4j
public class ConfigurationAwareNotificationClient implements NotificationClientApi {

  private final NotifyConfiguration configuration;
  private final NotificationClientApi realClient;

  public ConfigurationAwareNotificationClient(
      NotifyConfiguration config, NotificationClientApi realClient) {
    this.configuration = config;
    this.realClient = realClient;
  }

  public ConfigurationAwareNotificationClient(NotifyConfiguration config) {
    this(config, new NotificationClient(config.getApiKey()));
  }

  @Override
  public SendEmailResponse sendEmail(
      String templateId, String emailAddress, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    log.debug("sendEmail: {} - {} - {} - {}", templateId, emailAddress, personalisation, reference);

    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.sendEmail(
        templateId,
        getEmailOrOverrideAddress(emailAddress),
        personalisation,
        reference);
  }

  @Override
  public SendSmsResponse sendSms(
      String templateId, String phoneNumber, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    log.debug("sendSms: {} - {} - {} - {}", templateId, phoneNumber, personalisation, reference);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.sendSms(templateId, phoneNumber, personalisation, reference);
  }

  @Override
  public Notification getNotificationById(String notificationId)
      throws NotificationClientException {
    log.debug("getNotificationById: {}", notificationId);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.getNotificationById(notificationId);
  }

  @Override
  public NotificationList getNotifications(
      String status, String notificationType, String reference, String olderThanId)
      throws NotificationClientException {
    log.debug("getNotifications: {}", status, notificationType, reference, olderThanId);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.getNotifications(status, notificationType, reference, olderThanId);
  }

  @Override
  public Template getTemplateById(String templateId) throws NotificationClientException {
    log.debug("getTemplateById: {}", templateId);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.getTemplateById(templateId);
  }

  @Override
  public Template getTemplateVersion(String templateId, int version)
      throws NotificationClientException {
    log.debug("getTemplateVersion: {} - {}", templateId, version);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.getTemplateVersion(templateId, version);
  }

  @Override
  public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
    log.debug("getAllTemplates: {}", templateType);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.getAllTemplates(templateType);
  }

  @Override
  public TemplatePreview generateTemplatePreview(
      String templateId, Map<String, String> personalisation) throws NotificationClientException {
    log.debug("generateTemplatePreview: {} - {}", templateId, personalisation);
    if (!this.configuration.getEnabled()) {
      return null;
    }

    return realClient.generateTemplatePreview(templateId, personalisation);
  }

  private String getEmailOrOverrideAddress(String emailAddress) {
    Boolean addressOverrideEnabled = this.configuration.getAddressOverride();
    String overrideAddress = this.configuration.getOverrideAddress();

    if (addressOverrideEnabled && !StringUtils.isBlank(overrideAddress)) {
      return overrideAddress;
    }

    return emailAddress;
  }
}
