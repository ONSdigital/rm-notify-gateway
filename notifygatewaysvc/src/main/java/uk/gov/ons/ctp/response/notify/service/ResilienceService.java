package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.domain.SendSmsResponse;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.service.notify.Notification;

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
    SendSmsResponse process(NotifyRequest notifyRequest);

    /**
     * Updates a message in the DB
     *
     * @param message The Message information to use for the update
     */
    void update(Message message);

    /**
     * Retrieves the NotificationID associated with messageId
     * Asks GOV.UK Notify for full details on the Notification
     *
     * @param messageId to search for
     * @return the GOV.UK Notify Notification
     * @throws throws CTPException if GOV.UK Notify has thrown an exception
     */
    Notification findNotificationByMessageId(UUID messageId) throws CTPException;
}
