package uk.gov.ons.ctp.response.notify.client;

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

public abstract class NotificationClientDecorator implements NotificationClientApi {
  private final NotificationClientApi client;

  public NotificationClientDecorator(NotificationClientApi client) {
    this.client = client;
  }

  @Override
  public SendEmailResponse sendEmail(
      String templateId, String emailAddress, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    return client.sendEmail(templateId, emailAddress, personalisation, reference);
  }

  @Override
  public SendSmsResponse sendSms(
      String templateId, String phoneNumber, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    return client.sendSms(templateId, phoneNumber, personalisation, reference);
  }

  @Override
  public Notification getNotificationById(String notificationId)
      throws NotificationClientException {
    return client.getNotificationById(notificationId);
  }

  @Override
  public NotificationList getNotifications(
      String status, String notificationType, String reference, String olderThanId)
      throws NotificationClientException {
    return client.getNotifications(status, notificationType, reference, olderThanId);
  }

  @Override
  public Template getTemplateById(String templateId) throws NotificationClientException {
    return client.getTemplateById(templateId);
  }

  @Override
  public Template getTemplateVersion(String templateId, int version)
      throws NotificationClientException {
    return client.getTemplateVersion(templateId, version);
  }

  @Override
  public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
    return client.getAllTemplates(templateType);
  }

  @Override
  public TemplatePreview generateTemplatePreview(
      String templateId, Map<String, String> personalisation) throws NotificationClientException {
    return client.generateTemplatePreview(templateId, personalisation);
  }

  public NotificationClientApi getClient() {
    return client;
  }
}
