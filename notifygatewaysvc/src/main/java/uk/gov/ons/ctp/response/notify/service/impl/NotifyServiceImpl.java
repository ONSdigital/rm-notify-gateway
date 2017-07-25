package uk.gov.ons.ctp.response.notify.service.impl;

import static uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl.SITUATION_MAX_LENGTH;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.util.InternetAccessCodeFormatter;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

/**
 * The service implementation for NotifyService
 */
@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

  @Autowired
  private NotifyConfiguration notifyConfiguration;

  @Autowired
  private NotificationClient notificationClient;

  private static final String BAD_REQUEST = "Status code: 400";

  public static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";
  public static final String IAC_KEY = "iac";
  public static final String NOTIFY_SMS_NOT_SENT = "Notify Sms Not Sent";
  public static final String NOTIFY_SMS_SENT = "Notify Sms Sent";

  @Override
  public ActionFeedback process(ActionRequest actionRequest) throws CTPException {
    String actionId = actionRequest.getActionId();
    log.debug("Entering process with actionId {}", actionId);

    try {
      String templateId = notifyConfiguration.getTemplateId();

      ActionContact actionContact = actionRequest.getContact();
      String phoneNumber = actionContact.getPhoneNumber();
      Map<String, String> personalisation = new HashMap<>();
      personalisation.put(IAC_KEY, InternetAccessCodeFormatter.externalize(actionRequest.getIac()));

      log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {} for actionId = {}",
              templateId, phoneNumber, personalisation, actionId);
      SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation, null);

      if (log.isDebugEnabled()) {
        log.debug("status = {} for actionId = {}",
                notificationClient.getNotificationById(response.getNotificationId().toString()).getStatus(), actionId);
      }

      return new ActionFeedback(actionId,
              NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
              NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
              Outcome.REQUEST_COMPLETED);
    } catch (NotificationClientException e) {
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      if (errorMsg.contains(BAD_REQUEST)) {
        return new ActionFeedback(actionId, NOTIFY_SMS_NOT_SENT.length() <= SITUATION_MAX_LENGTH ?
                NOTIFY_SMS_NOT_SENT : NOTIFY_SMS_NOT_SENT.substring(0, SITUATION_MAX_LENGTH),
                Outcome.REQUEST_COMPLETED);
      }
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }

  @Override
  public void process(NotifyRequest notifyRequest) throws CTPException {
    try {
      String templateId = notifyRequest.getTemplateId();
      String phoneNumber = notifyRequest.getPhoneNumber();
      // TODO Check for phone or email address : separate service for sms and email?
      Map<String, String> personalisation = new HashMap<>();
      // TODO
      log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {}",
              templateId, phoneNumber, personalisation);
      SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation, null);

      if (log.isDebugEnabled()) {
        log.debug("status = {}", notificationClient.getNotificationById(response.getNotificationId().toString()).getStatus());
      }
    } catch (NotificationClientException e) {
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }
}
