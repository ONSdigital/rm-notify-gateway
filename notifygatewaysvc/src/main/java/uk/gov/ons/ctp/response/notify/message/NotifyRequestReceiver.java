package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

/**
 * The service that reads NotifyRequests from the inbound channel
 */
public interface NotifyRequestReceiver {
    /**
     * To process NotifyRequests from the input channel notifyRequestTransformed
     * @param notifyRequest the NotifyRequest to be processed
     */
    void process(NotifyRequest notifyRequest) throws CTPException;
}
