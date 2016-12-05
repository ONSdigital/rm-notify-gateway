package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;

import javax.inject.Inject;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_NOT_SENT;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.*;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.PHONENUMBER;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURNAME;

/**
 * Note the class name in ManualCase as we do NOT want this test to be run as part of a Maven cycle. Reason: it fails
 * intermittently with 'token expired' exception.
 */
@SpringBootTest(classes = NotifyServiceImplITManualCaseConfig.class)
@RunWith(SpringRunner.class)
public class NotifyServiceImplITManualCase {

  @Inject
  private NotifyConfiguration notifyConfiguration;

  @Inject
  private NotifyService notifyService;

  @Test
  public void testProcessHappyPath() throws CTPException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_SENT, actionFeedback.getSituation());
  }

  @Test
  public void testProcessInvalidPhoneNumber() throws CTPException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, INVALIDPHONENUMBER, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_NOT_SENT, actionFeedback.getSituation());
  }
}
