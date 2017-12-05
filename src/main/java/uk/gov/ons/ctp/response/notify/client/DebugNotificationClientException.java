package uk.gov.ons.ctp.response.notify.client;

import uk.gov.service.notify.NotificationClientException;

public class DebugNotificationClientException extends NotificationClientException {

    private int httpCode;
    private String message;

    public DebugNotificationClientException(int httpCode, String message) {
        super(new Exception(message));

        this.httpCode = httpCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getHttpResult() {
        return this.httpCode;
    }
}
