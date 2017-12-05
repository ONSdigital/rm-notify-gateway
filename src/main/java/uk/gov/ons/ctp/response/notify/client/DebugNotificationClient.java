package uk.gov.ons.ctp.response.notify.client;

import uk.gov.service.notify.*;

import java.util.Map;

public class DebugNotificationClient implements NotificationClientApi {

    private NotificationClientException exceptionToThrow;

    public DebugNotificationClient(NotificationClientException toThrow){
        this.exceptionToThrow = toThrow;
    }

    public DebugNotificationClient(int httpCode){
        this(new DebugNotificationClientException(httpCode, "A " + httpCode + " error occurred"));
    }

    public DebugNotificationClient(){
        this(400);
    }

    @Override
    public SendEmailResponse sendEmail(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        throw this.exceptionToThrow;
    }

    @Override
    public SendSmsResponse sendSms(String s, String s1, Map<String, String> map, String s2) throws NotificationClientException {
        throw this.exceptionToThrow;
    }

    @Override
    public Notification getNotificationById(String s) throws NotificationClientException {
        throw this.exceptionToThrow;
    }

    @Override
    public NotificationList getNotifications(String s, String s1, String s2, String s3) throws NotificationClientException {
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
    public TemplatePreview generateTemplatePreview(String s, Map<String, String> map) throws NotificationClientException {
        throw this.exceptionToThrow;
    }
}
