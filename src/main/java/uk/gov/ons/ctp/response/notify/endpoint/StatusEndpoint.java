package uk.gov.ons.ctp.response.notify.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Optional;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.representation.NotificationDTO;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

/** The REST endpoint controller for retrieving Messages status */
@RestController
@RequestMapping(value = "/messages", produces = "application/json")
public class StatusEndpoint {
  private static final Logger log = LoggerFactory.getLogger(StatusEndpoint.class);

  @Autowired private NotifyService notifyService;

  @Autowired private ResilienceService resilienceService;

  @Qualifier("notifySvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  public static final String ERRORMSG_MESSAGE_NOTFOUND = "Message not found for message id %s";
  public static final String ERRORMSG_NOTIFICATION_ISSUE =
      "Error encountered while retrieving notification. " + "Message is %s. Cause is %s";
  public static final String ERRORMSG_NOTIFICATION_NOTDEFINED =
      "Notification not yet defined for message id %s";
  public static final String ERRORMSG_NOTIFICATION_NOTFOUND =
      "Notification not found for message id %s";

  /**
   * To retrieve the GOV.UK Notify Notification associated with the given messageId
   *
   * @param messageId to search for
   * @return the associated NotificationDTO
   * @throws CTPException if GOV.UK Notify has thrown an exception or if no resource found for
   *     messageId
   */
  @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
  public ResponseEntity<NotificationDTO> getStatus(@PathVariable("messageId") final UUID messageId)
      throws CTPException {
    log.debug("Entering getStatus with messageId {}", messageId);

    Message message = findMessageById(messageId);
    UUID notificationId = getNotificationId(message, messageId);
    Notification notification = findNotificationById(notificationId, messageId);

    return ResponseEntity.ok(map(notification));
  }

  private Message findMessageById(final UUID messageId) throws CTPException {
    Message message = resilienceService.findMessageById(messageId);

    if (message == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(ERRORMSG_MESSAGE_NOTFOUND, messageId));
    }

    return message;
  }

  private UUID getNotificationId(final Message message, final UUID messageId) throws CTPException {
    UUID notificationId = message.getNotificationId();

    if (notificationId == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(ERRORMSG_NOTIFICATION_NOTDEFINED, messageId));
    }

    return notificationId;
  }

  private Notification findNotificationById(final UUID notificationId, final UUID messageId)
      throws CTPException {
    Notification notification = fetchNotificationFromNotifyService(notificationId);

    if (notification == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(ERRORMSG_NOTIFICATION_NOTFOUND, messageId));
    }

    return notification;
  }

  private Notification fetchNotificationFromNotifyService(final UUID notificationId)
      throws CTPException {
    try {
      return notifyService.findNotificationById(notificationId);
    } catch (NotificationClientException e) {
      String errorMsg = String.format(ERRORMSG_NOTIFICATION_ISSUE, e.getMessage(), e.getCause());
      log.error(errorMsg);
      log.error("Stack trace: " + e);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }

  // TODO Attempted to use mapperFacade but was getting the exception:
  // TODO ma.glasnost.orika.MappingException: No concrete class mapping defined for source class
  // TODO org.joda.time.chrono.ISOChronology
  private NotificationDTO map(Notification notification) {
    NotificationDTO result =
        NotificationDTO.builder()
            .id(notification.getId())
            .notificationType(notification.getNotificationType())
            .status(notification.getStatus())
            .templateId(notification.getTemplateId())
            .templateVersion(notification.getTemplateVersion())
            .createdAt(notification.getCreatedAt().toDate())
            .build();
    Optional<String> reference = notification.getReference();
    if (reference.isPresent()) {
      result.setReference(reference.get());
    }
    Optional<String> emailAddress = notification.getEmailAddress();
    if (emailAddress.isPresent()) {
      result.setEmailAddress(emailAddress.get());
    }
    Optional<String> phoneNumber = notification.getPhoneNumber();
    if (phoneNumber.isPresent()) {
      result.setPhoneNumber(phoneNumber.get());
    }
    Optional<DateTime> sentAt = notification.getSentAt();
    if (sentAt.isPresent()) {
      result.setSentAt(sentAt.get().toDate());
    }
    Optional<DateTime> completedAt = notification.getCompletedAt();
    if (completedAt.isPresent()) {
      result.setCompletedAt(completedAt.get().toDate());
    }

    return result;
  }
}
