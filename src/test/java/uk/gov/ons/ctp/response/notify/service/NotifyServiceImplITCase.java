package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.EXCEPTION_NOTIFY_SERVICE;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.*;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.PHONENUMBER;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURNAME;

@SpringBootTest(classes = NotifyServiceImplITCaseConfig.class)
@RunWith(SpringRunner.class)
public class NotifyServiceImplITCase {

  private static final String CREATED = "created";

  @Autowired
  private NotifyService notifyService;

  @Test
  public void testProcessHappyPath() throws CTPException {
    ActionFeedback actionFeedback = notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_SMS_SENT, actionFeedback.getSituation());
    assertEquals(CREATED, actionFeedback.getNotes());
  }

  @Test
  public void testProcessInvalidPhoneNumber() {
    boolean exceptionThrown = false;
    try {
      notifyService.process(buildActionRequest(ACTION_ID, FORENAME, SURNAME, INVALID_PHONENUMBER, true));
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertTrue(e.getMessage().startsWith(EXCEPTION_NOTIFY_SERVICE));
    }
    assertTrue(exceptionThrown);
  }
}
