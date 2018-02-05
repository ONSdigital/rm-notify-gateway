package uk.gov.ons.ctp.response.notify.client;

public class CommsTemplateClientException extends Exception {
    private int httpCode;
    private String message;

    public CommsTemplateClientException(int httpCode, String message) {
        super(new Exception(message));

        this.httpCode = httpCode;
        this.message = message;
    }
    public String getMessage() {
        return this.message;
    }

    public int getHttpResult() {
        return this.httpCode;
    }
}
