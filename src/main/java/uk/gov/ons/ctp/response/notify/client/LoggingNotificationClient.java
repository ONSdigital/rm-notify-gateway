package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import org.slf4j.Logger;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;
import uk.gov.service.notify.Template;
import uk.gov.service.notify.TemplateList;
import uk.gov.service.notify.TemplatePreview;

public class LoggingNotificationClient implements NotificationClientApi {
  private final NotificationClientApi client;
  private final Logger log;

  public LoggingNotificationClient(NotificationClientApi client, Logger log) {
    this.client = client;
    this.log = log;
  }

  @Override
  public SendEmailResponse sendEmail(
      String templateId, String emailAddress, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    log.debug("sendEmail: {} - {} - {} - {}", templateId, emailAddress, personalisation, reference);
    return client.sendEmail(templateId, emailAddress, personalisation, reference);
  }

  @Override
  public SendSmsResponse sendSms(
      String templateId, String phoneNumber, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    log.debug("sendSms: {} - {} - {} - {}", templateId, phoneNumber, personalisation, reference);
    return client.sendSms(templateId, phoneNumber, personalisation, reference);
  }

  @Override
  public Notification getNotificationById(String notificationId)
      throws NotificationClientException {
    log.debug("getNotificationById: {}", notificationId);
    return client.getNotificationById(notificationId);
  }

  @Override
  public NotificationList getNotifications(
      String status, String notificationType, String reference, String olderThanId)
      throws NotificationClientException {
    log.debug("getNotifications: {}", status, notificationType, reference, olderThanId);
    return client.getNotifications(status, notificationType, reference, olderThanId);
  }

  @Override
  public Template getTemplateById(String templateId) throws NotificationClientException {
    log.debug("getTemplateById: {}", templateId);
    return client.getTemplateById(templateId);
  }

  @Override
  public Template getTemplateVersion(String templateId, int version)
      throws NotificationClientException {
    log.debug("getTemplateVersion: {} - {}", templateId, version);
    return client.getTemplateVersion(templateId, version);
  }

  @Override
  public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
    log.debug("getAllTemplates: {}", templateType);
    return client.getAllTemplates(templateType);
  }

  @Override
  public TemplatePreview generateTemplatePreview(
      String templateId, Map<String, String> personalisation) throws NotificationClientException {
    log.debug("generateTemplatePreview: {} - {}", templateId, personalisation);
    return client.generateTemplatePreview(templateId, personalisation);
  }

  public NotificationClientApi getClient() {
    return client;
  }
}
