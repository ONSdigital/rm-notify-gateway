package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

import java.util.UUID;

/**
 * Service to interact with GOV.UK Notify
 */
public interface NotifyService {
  /**
   * To process an ActionRequest. It sends an SMS using GOV.UK Notify.
   *
   * @param actionRequest to be processed
   * @return the associated ActionFeedback
//   * @throws NotificationClientException if GOV.UK Notify gives an issue
   */
  ActionFeedback process(ActionRequest actionRequest) throws NotificationClientException;

  /**
   * To process a NotifyRequest. It sends an SMS or an email using GOV.UK Notify.
   *
   * @param notifyRequest to be processed
   * @return the associated notificationId
   * @throws NotificationClientException if GOV.UK Notify gives an issue
   */
  UUID process(NotifyRequest notifyRequest) throws NotificationClientException;

  /**
   * To retrieve the full details of a Notification by Id from GOV.UK Notify.
   *
   * @param notificationId to search for
   * @return the associated Notification
   * @throws NotificationClientException if GOV.UK Notify gives an issue
   */
  Notification findNotificationById(UUID notificationId) throws NotificationClientException;
}
