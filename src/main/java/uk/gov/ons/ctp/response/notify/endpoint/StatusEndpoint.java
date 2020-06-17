package uk.gov.ons.ctp.response.notify.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.lib.common.CTPException;
import uk.gov.ons.ctp.response.notify.lib.notify.NotificationDTO;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.ons.ctp.response.notify.util.NotifyRequestMapper;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

/** The REST endpoint controller for retrieving Messages status */
@RestController
@RequestMapping(value = "/messages", produces = "application/json")
public class StatusEndpoint {
  private static final Logger log = LoggerFactory.getLogger(StatusEndpoint.class);

  @Autowired private NotifyService notifyService;

  @Autowired private ResilienceService resilienceService;

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
  @Operation(summary = "Get status of an existing message")
  @Parameter(in = ParameterIn.PATH, name = "messageId", description = "Message Id")
  @ApiResponses( value = {
          @ApiResponse(responseCode = "200", description = "Successful Operation"),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
                  content = @Content(examples = {})),
          @ApiResponse(responseCode = "404", description = "Resource Not Found",
                  content = @Content(examples = {}))
  })
  @RequestMapping(value = "/{messageId}", method = RequestMethod.GET)
  public ResponseEntity<NotificationDTO> getStatus(@PathVariable("messageId") final UUID messageId)
      throws CTPException {
    log.with("message_id", messageId).debug("Entering getStatus");
    Message message = findMessageById(messageId);
    UUID notificationId = getNotificationId(message, messageId);
    Notification notification = findNotificationById(notificationId, messageId);
    NotificationDTO notificationDto = NotifyRequestMapper.INSTANCE.mapToNotificationDTO(notification);
    return ResponseEntity.ok(notificationDto);
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
      log.error(errorMsg, e);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }
}
