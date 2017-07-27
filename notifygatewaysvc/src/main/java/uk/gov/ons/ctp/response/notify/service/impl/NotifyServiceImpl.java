package uk.gov.ons.ctp.response.notify.service.impl;

import static uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl.SITUATION_MAX_LENGTH;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import liquibase.util.StringUtils;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
import uk.gov.service.notify.*;

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

            log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {} for " +
                            "actionId = {}", templateId, phoneNumber, personalisation, actionId);
            SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation,
                    null);

            if (log.isDebugEnabled()) {
                log.debug("status = {} for actionId = {}",
                        notificationClient.getNotificationById(response.getNotificationId().toString()).getStatus(),
                        actionId);
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
    public UUID process(NotifyRequest notifyRequest) throws NotificationClientException {
        String phoneNumber = notifyRequest.getPhoneNumber();
        String emailAddress = notifyRequest.getEmailAddress();
        String templateId = notifyRequest.getTemplateId();
        String reference = notifyRequest.getReference();
        String personalisation = notifyRequest.getPersonalisation();
        Map<String, String> personalisationMap = buildMapFromString(personalisation);

        if (!StringUtils.isEmpty(phoneNumber)) {
            log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {}",
                    templateId, phoneNumber, personalisation);
            SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisationMap,
                    reference);
            if (log.isDebugEnabled()) {
                log.debug("status = {}", notificationClient.getNotificationById(response.getNotificationId().
                        toString()).getStatus());
            }

            return response.getNotificationId();
        } else {
            // The xsd enforces to have either a phoneNumber OR an emailAddress
            log.debug("About to invoke sendEmail with templateId {} - emailAddress {} - personalisation {}",
                    templateId , emailAddress, personalisation);
            SendEmailResponse response = notificationClient.sendEmail(templateId, emailAddress, null,
                    reference);
            if (log.isDebugEnabled()) {
                log.debug("status = {}", notificationClient.getNotificationById(response.getNotificationId().
                        toString()).getStatus());
            }

            return response.getNotificationId();
        }
    }

    @Override
    public Notification findNotificationById(UUID notificationId) throws NotificationClientException {
        return notificationClient.getNotificationById(notificationId.toString());
    }

    /**
     * Transform the string built originally by orika into a Map
     *
     * An example is {iac=ABCD-EFGH-IJKL-MNOP, otherfield=other,value} which was built from
     * "personalisation": {"iac":"ABCD-EFGH-IJKL-MNOP", "otherfield":"other,value"}
     *
     * @param personalisation the string built originally by orika
     * @return
     */
    private Map<String, String> buildMapFromString(String personalisation) {
        Map<String, String > result = new LinkedHashMap();
        // TODO
        return result;
    }
}
