package uk.gov.ons.ctp.response.notify.client;

import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.*;

import java.util.Map;

/**
 * This is an implementation of the NotificationClientApi where all of the operations throw an exception supplied
 * in the constructor.  This class is mainly intended for reproducing certain kinds of problems without actually
 * sending API calls to gov.notify
 */
@Slf4j
public class ExceptionThrowingNotificationClient implements NotificationClientApi {

    private NotificationClientException exceptionToThrow;

    public ExceptionThrowingNotificationClient(NotificationClientException toThrow){
        log.debug("Constructing ExceptionThrowingNotificationClient with " + toThrow);
        this.exceptionToThrow = toThrow;
    }

    public ExceptionThrowingNotificationClient(int httpCode){
        this(new OverridenNotificationClientException(httpCode, "A " + httpCode + " error occurred"));
    }

    public ExceptionThrowingNotificationClient(){
        this(400);
    }

    @Override
    public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2)
            throws NotificationClientException {
        log.debug("sendEmail: {} - {} - {} - {}", s, s1, map, s2);
        throw this.exceptionToThrow;
    }

    @Override
    public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2)
            throws NotificationClientException {
        log.debug("sendSms: {}  - {} - {} - {}", s, s1, map, s2);
        throw this.exceptionToThrow;
    }

    @Override
    public Notification getNotificationById(String s) throws NotificationClientException {
        log.debug("getNotificationById: {}", s);
        throw this.exceptionToThrow;
    }

    @Override
    public NotificationList getNotifications(String s, String s1, String s2, String s3)
            throws NotificationClientException {
        log.debug("getNotifications: {} - {} - {} - {}", s, s1, s2, s3);
        throw this.exceptionToThrow;
    }

    @Override
    public Template getTemplateById(String s) throws NotificationClientException {
        log.debug("getTemplateById: {}", s);
        throw this.exceptionToThrow;
    }

    @Override
    public Template getTemplateVersion(String s, int i) throws NotificationClientException {
        log.debug("getTemplateVersion: {} - {}", s, i);
        throw this.exceptionToThrow;
    }

    @Override
    public TemplateList getAllTemplates(String s) throws NotificationClientException {
        log.debug("getAllTemplates: {}", s);
        throw this.exceptionToThrow;
    }

    @Override
    public TemplatePreview generateTemplatePreview(String s, Map<String, String> map)
            throws NotificationClientException {
        log.debug("generateTemplatePreview: {} - {}", s, map);
        throw this.exceptionToThrow;
    }
}
