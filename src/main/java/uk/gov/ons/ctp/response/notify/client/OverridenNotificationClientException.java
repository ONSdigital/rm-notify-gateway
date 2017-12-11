package uk.gov.ons.ctp.response.notify.client;

import uk.gov.service.notify.NotificationClientException;

/**
 * A derivation of NotificationClientException that allows the httpCode to be overridden (in the base class it's package
 * scope access).  If they didn't want anyone to do this they would have made it final ...
 */
public class OverridenNotificationClientException extends NotificationClientException {

    private int httpCode;
    private String message;

    public OverridenNotificationClientException(int httpCode, String message) {
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
