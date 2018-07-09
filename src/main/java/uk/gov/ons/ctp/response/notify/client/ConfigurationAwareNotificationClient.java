package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.*;

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

    boolean addressOverride = this.configuration.getAddressOverride();
    String overrideAddress = this.configuration.getOverrideAddress();

    String actualEmail =
        addressOverride && !StringUtils.isBlank(overrideAddress) ? overrideAddress : emailAddress;

    return realClient.sendEmail(templateId, actualEmail, personalisation, reference);
  }

  @Override
  public SendSmsResponse sendSms(
      String templateId, String phoneNumber, Map<String, String> personalisation, String reference)
      throws NotificationClientException {
    return realClient.sendSms(templateId, phoneNumber, personalisation, reference);
  }

  @Override
  public Notification getNotificationById(String notificationId)
      throws NotificationClientException {
    return realClient.getNotificationById(notificationId);
  }

  @Override
  public NotificationList getNotifications(
      String status, String notificationType, String reference, String olderThanId)
      throws NotificationClientException {
    return realClient.getNotifications(status, notificationType, reference, olderThanId);
  }

  @Override
  public Template getTemplateById(String templateId) throws NotificationClientException {
    return realClient.getTemplateById(templateId);
  }

  @Override
  public Template getTemplateVersion(String templateId, int version)
      throws NotificationClientException {
    return realClient.getTemplateVersion(templateId, version);
  }

  @Override
  public TemplateList getAllTemplates(String templateType) throws NotificationClientException {
    return realClient.getAllTemplates(templateType);
  }

  @Override
  public TemplatePreview generateTemplatePreview(
      String templateId, Map<String, String> personalisation) throws NotificationClientException {
    return realClient.generateTemplatePreview(templateId, personalisation);
  }
}
