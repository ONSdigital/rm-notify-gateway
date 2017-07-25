package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.response.notify.domain.SendSmsResponse;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

/**
 * The service to store to database and post on queues
 */
public interface ResilienceService {
    /**
     * Builds a UUID for the NotifyRequest, stores in the database and puts message on queue
     *
     * @param notifyRequest the request to process
     * @return
     */
    SendSmsResponse process(NotifyRequest notifyRequest);
}
