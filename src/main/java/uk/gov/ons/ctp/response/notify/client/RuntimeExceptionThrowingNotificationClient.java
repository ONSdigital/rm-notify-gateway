package uk.gov.ons.ctp.response.notify.client;

import uk.gov.service.notify.*;

import java.util.Map;

public class RuntimeExceptionThrowingNotificationClient implements NotificationClientApi {

    public RuntimeExceptionThrowingNotificationClient(){
    }

    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("This is a RuntimeException");

    @Override
    public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public Notification getNotificationById(String s) throws NotificationClientException {
        throw RUNTIME_EXCEPTION;
    }

    @Override
    public NotificationList getNotifications(String s, String s1, String s2, String s3) throws NotificationClientException {
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
    public TemplatePreview generateTemplatePreview(String s, Map<String, String> map) throws NotificationClientException {
        throw RUNTIME_EXCEPTION;
    }
}
