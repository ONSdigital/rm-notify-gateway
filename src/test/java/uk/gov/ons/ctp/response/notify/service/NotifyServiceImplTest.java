package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.*;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.*;

/**
 * To unit test NotifyServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceImplTest {

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @Mock
  private NotificationClient notificationClient;

  @Mock
  private NotifyConfiguration notifyConfiguration;

  @Test
  public void testProcessHappyPath() throws CTPException, NotificationClientException {
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class),any(String.class))).thenReturn(buildSendSmsResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotification());

    ActionFeedback result = notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    assertEquals(ACTION_ID, result.getActionId());
    assertEquals(NOTIFY_SMS_SENT, result.getSituation());
    assertEquals(Outcome.REQUEST_COMPLETED, result.getOutcome());

    HashMap<String, String> personalisation = new HashMap<>();
    personalisation.put(IAC_KEY, IAC_AS_DISPLAYED_IN_SMS);
    verify(notificationClient, times(1)).sendSms(any(String.class), eq(PHONENUMBER), eq(personalisation),any(String.class));
  }

  @Test
  public void testProcessSendSmsException() throws NotificationClientException {
    NotificationClientException exception = new NotificationClientException(new Exception());
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class), any(String.class))).thenThrow(exception);

    boolean exceptionThrown = false;
    try {
      notifyService.process(ObjectBuilder.buildActionRequest(ACTION_ID, FORENAME, SURNAME, PHONENUMBER, true));
    } catch(CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertTrue(e.getMessage().startsWith(EXCEPTION_NOTIFY_SERVICE));
    }

    assertTrue(exceptionThrown);
    verify(notificationClient, times(1)).sendSms(any(String.class), any(String.class), any(HashMap.class), any(String.class));
  }
}
