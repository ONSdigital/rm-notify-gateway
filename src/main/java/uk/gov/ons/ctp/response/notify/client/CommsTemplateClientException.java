package uk.gov.ons.ctp.response.notify.client;

import uk.gov.ons.ctp.common.error.CTPException;

public class CommsTemplateClientException extends CTPException {

    public CommsTemplateClientException(CTPException.Fault fault, String message) {
        super(fault, message);
    }
}
