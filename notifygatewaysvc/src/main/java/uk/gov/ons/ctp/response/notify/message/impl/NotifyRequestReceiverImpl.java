package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.oxm.Marshaller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestReceiver;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

/**
 * The service that reads NotifyRequests from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class NotifyRequestReceiverImpl implements NotifyRequestReceiver {

    @Autowired
    @Qualifier("notifyRequestMarshaller")
    Marshaller marshaller;

    @Autowired
    private NotifyService notifyService;

    private static final String PROCESS_NOTIFY_REQUEST = "ProcessingNotifyRequest";

    /**
     * To process NotifyRequests from the input channel notifyRequestTransformed
     *
     * @param notifyRequest the NotifyRequest to be processed
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @ServiceActivator(inputChannel = "notifyRequestTransformed", adviceChain = "notifyRequestRetryAdvice")
    public void process(final NotifyRequest notifyRequest) throws CTPException {
        log.debug("entering process with notifyRequest {}", notifyRequest);

        notifyService.process(notifyRequest);
    }
}
