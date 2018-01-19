package uk.gov.ons.ctp.response.notify.client;

import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.*;

import java.util.Map;

/**
 * This is an implementation of the NotificationClientApi where all of the operations throw a RuntimeException.
 * This class is mainly intended for reproducing certain kinds of problems without actually
 * sending API calls to gov.notify
 */
@Slf4j
public class RuntimeExceptionThrowingNotificationClient implements NotificationClientApi {

    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("This is a RuntimeException");

    public RuntimeExceptionThrowingNotificationClient(){
        log.debug("Constructing RuntimeExceptionThrowingNotificationClient");
    }

    @Override
    public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        log.debug("sendEmail: {} - {} - {} - {}", s, s1, map, s2);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        log.debug("sendSms: {}  - {} - {} - {}", s, s1, map, s2);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public Notification getNotificationById(String s) throws NotificationClientException {
        log.debug("getNotificationById: {}", s);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public NotificationList getNotifications(String s, String s1, String s2, String s3) throws NotificationClientException {
        log.debug("getNotifications: {} - {} - {} - {}", s, s1, s2, s3);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public Template getTemplateById(String s) throws NotificationClientException {
        log.debug("getTemplateById: {}", s);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public Template getTemplateVersion(String s, int i) throws NotificationClientException {
        log.debug("getTemplateVersion: {} - {}", s, i);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public TemplateList getAllTemplates(String s) throws NotificationClientException {
        log.debug("getAllTemplates: {}", s);
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public TemplatePreview generateTemplatePreview(String s, Map<String, String> map) throws NotificationClientException {
        log.debug("generateTemplatePreview: {} - {}", s, map);
        throw RUNTIME_EXCEPTION;
    }
}
