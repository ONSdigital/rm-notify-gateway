package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

import java.util.UUID;

/**
 * The service to store to database and post on queues
 */
public interface ResilienceService {

    /**
     * Builds a UUID for the NotifyRequest
     * Stores an associated Message in the database
     * Puts NotifyRequest on queue
     *
     * @param notifyRequest the request to process
     * @return
     */
    Response process(NotifyRequest notifyRequest);

    /**
     * Updates a message in the DB
     *
     * @param message The Message information to use for the update
     */
    void update(Message message);

    /**
     * Retrieves the Message associated with messageId
     *
     * @param messageId to search for
     * @return the associated Message
     */
    Message findMessageById(UUID messageId);
}
