package uk.gov.ons.ctp.response.notify.service;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.ACTION_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.FORENAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.INVALIDPHONENUMBER;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.PHONENUMBER;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURNAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildActionRequest;

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

  @Test
  public void testProcessHappyPath() throws NotificationClientException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER,
            true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_SENT, actionFeedback.getSituation());
  }

  @Test
  public void testProcessInvalidPhoneNumber() throws NotificationClientException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME,
            INVALIDPHONENUMBER, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_NOT_SENT, actionFeedback.getSituation());
  }
}
