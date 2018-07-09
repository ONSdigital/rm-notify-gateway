package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import uk.gov.service.notify.*;

public class NullNotificationClient implements NotificationClientApi {
  @Override
  public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    return null;
  }

  @Override
  public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    return null;
  }

  @Override
  public Notification getNotificationById(String s) throws NotificationClientException {
    return null;
  }

  @Override
  public NotificationList getNotifications(String s, String s1, String s2, String s3)
      throws NotificationClientException {
    return null;
  }

  @Override
  public Template getTemplateById(String s) throws NotificationClientException {
    return null;
  }

  @Override
  public Template getTemplateVersion(String s, int i) throws NotificationClientException {
    return null;
  }

  @Override
  public TemplateList getAllTemplates(String s) throws NotificationClientException {
    return null;
  }

  @Override
  public TemplatePreview generateTemplatePreview(String s, Map<String, String> map)
      throws NotificationClientException {
    return null;
  }
}
