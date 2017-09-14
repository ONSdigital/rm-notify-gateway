package uk.gov.ons.ctp.response.notify.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientException;

/**
 * Note the class name in ManualCase as we do NOT want this test to be run as part of a Maven cycle. Reason: it fails
 * intermittently with 'token expired' exception.
 */
@SpringBootTest(classes = NotifyServiceImplITManualCaseConfig.class)
@RunWith(SpringRunner.class)
public class NotifyServiceImplITManualCase {

  @Autowired
  private NotifyConfiguration notifyConfiguration;

  @Autowired
  private NotifyService notifyService;

  /**
   * To test the sending of an SMS. Note the PHONENUMBER is not null.
   *
   * @throws NotificationClientException if GOV.UK Notify issue
   */
  @Test
  public void testProcessHappyPathSMS() throws NotificationClientException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER,
        EMAIL_ADDRESS, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_SENT, actionFeedback.getSituation());
  }

  /**
   * To test the sending of an SMS with an invalid phone number.
   *
   * @throws NotificationClientException if GOV.UK Notify issue
   */
  @Test
  public void testProcessSMSInvalidPhoneNumber() throws NotificationClientException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME,
        INVALID_PHONENUMBER, INVALID_EMAIL_ADDRESS,true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_NOT_SENT, actionFeedback.getSituation());
  }
}
