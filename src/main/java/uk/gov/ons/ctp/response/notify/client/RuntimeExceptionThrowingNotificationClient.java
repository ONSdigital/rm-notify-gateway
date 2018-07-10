package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationList;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;
import uk.gov.service.notify.Template;
import uk.gov.service.notify.TemplateList;
import uk.gov.service.notify.TemplatePreview;

/**
 * This is an implementation of the NotificationClientApi where all of the operations throw a
 * RuntimeException. This class is mainly intended for reproducing certain kinds of problems without
 * actually sending API calls to gov.notify
 */
@Slf4j
public class RuntimeExceptionThrowingNotificationClient implements NotificationClientApi {

  private static final RuntimeException RUNTIME_EXCEPTION =
      new RuntimeException("This is a RuntimeException");

  public RuntimeExceptionThrowingNotificationClient() {
    log.debug("Constructing RuntimeExceptionThrowingNotificationClient");
  }

  @Override
  public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public Notification getNotificationById(String s) throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public NotificationList getNotifications(String s, String s1, String s2, String s3)
      throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public Template getTemplateById(String s) throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public Template getTemplateVersion(String s, int i) throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public TemplateList getAllTemplates(String s) throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }

  @Override
  public TemplatePreview generateTemplatePreview(String s, Map<String, String> map)
      throws NotificationClientException {
    throw RUNTIME_EXCEPTION;
  }
}
