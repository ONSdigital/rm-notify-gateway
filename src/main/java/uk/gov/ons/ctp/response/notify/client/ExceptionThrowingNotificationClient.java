package uk.gov.ons.ctp.response.notify.client;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
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

/**
 * This is an implementation of the NotificationClientApi where all of the operations throw an
 * exception supplied in the constructor. This class is mainly intended for reproducing certain
 * kinds of problems without actually sending API calls to gov.notify
 */
public class ExceptionThrowingNotificationClient implements NotificationClientApi {
  private static final Logger log =
      LoggerFactory.getLogger(ExceptionThrowingNotificationClient.class);

  private NotificationClientException exceptionToThrow;

  public ExceptionThrowingNotificationClient(NotificationClientException toThrow) {
    log.debug("Constructing ExceptionThrowingNotificationClient with " + toThrow);
    this.exceptionToThrow = toThrow;
  }

  public ExceptionThrowingNotificationClient(int httpCode) {
    this(new OverridenNotificationClientException(httpCode, "A " + httpCode + " error occurred"));
  }

  public ExceptionThrowingNotificationClient() {
    this(400);
  }

  @Override
  public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2)
      throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public Notification getNotificationById(String s) throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public NotificationList getNotifications(String s, String s1, String s2, String s3)
      throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public Template getTemplateById(String s) throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public Template getTemplateVersion(String s, int i) throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public TemplateList getAllTemplates(String s) throws NotificationClientException {
    throw this.exceptionToThrow;
  }

  @Override
  public TemplatePreview generateTemplatePreview(String s, Map<String, String> map)
      throws NotificationClientException {
    throw this.exceptionToThrow;
  }
}
