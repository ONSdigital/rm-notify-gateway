package uk.gov.ons.ctp.response.notify.client;

import com.godaddy.logging.Logger;

import java.util.Map;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;
import uk.gov.service.notify.Template;
import uk.gov.service.notify.TemplateList;
import uk.gov.service.notify.TemplatePreview;

public class LoggingNotificationClient {
  private Logger log;
  private NotificationClientApi client;

  public LoggingNotificationClient(NotificationClientApi client, Logger log) {
    this.client = client;
    this.log = log;
  }

  public SendEmailResponse sendEmail(
      String templateId, String emailAddress, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    log.with("template_id", templateId)
        .with("personalisation", personalisation)
        .with("reference", reference)
        .debug("sendEmail");
    return client.sendEmail(templateId, emailAddress, personalisation, reference);
  }

  public Notification getNotificationById(String notificationId)
      throws NotificationClientException {
    log.with("notification_id", notificationId).debug("getNotificationById");
    return client.getNotificationById(notificationId);
  }

  public NotificationList getNotifications(
      String status, String notificationType, String reference, String olderThanId)
      throws NotificationClientException {
    log.with("status", status)
        .with("notification_type", notificationType)
        .with("reference", reference)
        .with("older_than_id", olderThanId)
        .debug("getNotifications");
    return client.getNotifications(status, notificationType, reference, olderThanId);
  }

  public Template getTemplateById(String templateId) throws NotificationClientException {
    log.with("template_id", templateId).debug("getTemplateById");
    return client.getTemplateById(templateId);
  }

  public Template getTemplateVersion(String templateId, int version)
      throws NotificationClientException {
    log.with("template_id", templateId).with("version", version).debug("getTemplateVersion");
    return client.getTemplateVersion(templateId, version);
  }

  public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
    log.with("template_type", templateType).debug("getAllTemplates");
    return client.getAllTemplates(templateType);
  }

  public TemplatePreview generateTemplatePreview(
      String templateId, Map<String, String> personalisation) throws NotificationClientException {
    log.with("template_id", templateId)
        .with("personalisation", personalisation)
        .debug("generateTemplatePreview");
    return client.generateTemplatePreview(templateId, personalisation);
  }
}
