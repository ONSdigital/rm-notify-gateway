package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestReceiver;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.NotificationClientException;

import java.util.UUID;

/**
 * The service that reads NotifyRequests from the inbound channel
 */
@Slf4j
@MessageEndpoint
public class NotifyRequestReceiverImpl implements NotifyRequestReceiver {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private ResilienceService resilienceService;

    /**
     * To process NotifyRequests from the input channel notifyRequestTransformed
     *
     * @param notifyRequest the NotifyRequest to be processed
     * @throws NotificationClientException when GOV.UK Notify does
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @ServiceActivator(inputChannel = "notifyRequestTransformed", adviceChain = "notifyRequestRetryAdvice")
    public void process(final NotifyRequest notifyRequest) throws NotificationClientException {
        log.debug("entering process with notifyRequest {}", notifyRequest);
        UUID notificationId = notifyService.process(notifyRequest);

        resilienceService.update(Message.builder()
                .id(UUID.fromString(notifyRequest.getId()))
                .notificationId(notificationId)
                .build());
    }
}
