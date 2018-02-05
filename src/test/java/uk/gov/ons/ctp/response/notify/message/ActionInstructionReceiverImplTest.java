package uk.gov.ons.ctp.response.notify.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl.SITUATION_MAX_LENGTH;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_EMAIL_SENT;

/**
 * To unit test ActionInstructionReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionInstructionReceiverImplTest {

  private static final String MOCKED_ACTIONID = "9a5f2be5-f944-41f9-982c-3517cfcfef3c";

  @InjectMocks
  ActionInstructionReceiverImpl actionInstructionReceiver;

  @Mock
  private ActionFeedbackPublisher actionFeedbackPublisher;

  @Mock
  private NotifyService notifyService;

  @Test
  public void testProcessBRESInstructionHappyPath() throws NotificationClientException, CommsTemplateClientException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
        NOTIFY_EMAIL_SENT.length() <= SITUATION_MAX_LENGTH ?
            NOTIFY_EMAIL_SENT : NOTIFY_EMAIL_SENT.substring(0, SITUATION_MAX_LENGTH), Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(
        "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131,blogg@gmail.com",
        true));

    verify(notifyService, times(1)).process(any(ActionRequest.class));

    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
    assertEquals(2, actionFeedbackList.size());
    int countAccepted = 0; int countCompleted = 0;
    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
        countAccepted += 1;
      }
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
        countCompleted += 1;
      }
    }
    assertEquals(1, countAccepted);
    assertEquals(1, countCompleted);
    verify(actionFeedbackPublisher, times(1)).sendFeedback(eq(mockedActionFeedback));
  }


  @Test
  public void testProcessBRESInstructionEmailAddressWithSpacesAtFrontAndBack() throws NotificationClientException, CommsTemplateClientException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
        NOTIFY_EMAIL_SENT.length() <= SITUATION_MAX_LENGTH ?
            NOTIFY_EMAIL_SENT : NOTIFY_EMAIL_SENT.substring(0, SITUATION_MAX_LENGTH), Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(
              ObjectBuilder.buildActionInstruction(
                  "9a5f2be5-f944-41f9-982c-3517cfcfef3c",
                  "Joe,Blogg,(4)77   42 99 41 31,  tester@gmail.com  ", true));

    verify(notifyService, times(1)).process(any(ActionRequest.class));

    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
    assertEquals(2, actionFeedbackList.size());
    int countAccepted = 0; int countCompleted = 0;
    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
        countAccepted += 1;
      }
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
        countCompleted += 1;
      }
    }
    assertEquals(1, countAccepted);
    assertEquals(1, countCompleted);
    verify(actionFeedbackPublisher, times(1)).sendFeedback(eq(mockedActionFeedback));
  }

  @Test
  public void testProcessBRESInstructionNotifyThrowsException() throws NotificationClientException, CommsTemplateClientException {
    NotificationClientException exception = new NotificationClientException(new Exception());
    when(notifyService.process(any(ActionRequest.class))).thenThrow(exception);

    try {
      actionInstructionReceiver.processInstruction(
          ObjectBuilder.buildActionInstruction(
              "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131,tester@gmail.com", true));
      // Fail the test if an exception is not thrown
      fail();
    } catch (NotificationClientException e) {
    }

    verify(notifyService, times(1)).process(any(ActionRequest.class));

    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
    verify(actionFeedbackPublisher, times(1)).sendFeedback(argumentCaptor.capture());
    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
    assertEquals(1, actionFeedbackList.size());
    int countAccepted = 0;
    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
        countAccepted += 1;
      }
    }
    assertEquals(1, countAccepted);
  }

  /**
   * All the below are tests written for Census. Keep them to save time when we produce a solution
   * serving both BRES & Census.
   */

  //  @Test
//  public void testProcessInstructionHappyPath() throws NotificationClientException {
//    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
//            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
//            NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
//            Outcome.REQUEST_COMPLETED);
//    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);
//
//    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(
//            "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131", true));
//
//    verify(notifyService, times(1)).process(any(ActionRequest.class));
//
//    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
//    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
//    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
//    assertEquals(2, actionFeedbackList.size());
//    int countAccepted = 0; int countCompleted = 0;
//    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
//        countAccepted += 1;
//      }
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
//        countCompleted += 1;
//      }
//    }
//    assertEquals(1, countAccepted);
//    assertEquals(1, countCompleted);
//    verify(actionFeedbackPublisher, times(1)).sendFeedback(eq(mockedActionFeedback));
//  }
//
//  @Test
//  public void testProcessInstructionInvalidPhoneNumber() throws NotificationClientException {
//    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
//            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
//            NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
//            Outcome.REQUEST_COMPLETED);
//    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);
//
//    actionInstructionReceiver.processInstruction(
//            ObjectBuilder.buildActionInstruction(
//                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,0774", true));
//
//    verify(notifyService, times(0)).process(any(ActionRequest.class));
//
//    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
//    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
//    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
//    assertEquals(2, actionFeedbackList.size());
//    int countAccepted = 0; int countDeclined = 0; int countCompleted = 0;
//    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
//        countAccepted += 1;
//      }
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_DECLINED)) {
//        countDeclined += 1;
//      }
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
//        countCompleted += 1;
//      }
//    }
//    assertEquals(1, countAccepted);
//    assertEquals(1, countDeclined);
//    assertEquals(0, countCompleted);
//    verify(actionFeedbackPublisher, times(0)).sendFeedback(eq(mockedActionFeedback));
//  }
//
//  @Test
//  public void testProcessInstructionPhoneNumberForCtpa1170() throws NotificationClientException {
//    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
//            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
//                    NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
//            Outcome.REQUEST_COMPLETED);
//    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);
//
//    actionInstructionReceiver.processInstruction(
//            ObjectBuilder.buildActionInstruction(
//                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742 994131", true));
//
//    verify(notifyService, times(1)).process(any(ActionRequest.class));
//
//    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
//    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
//    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
//    assertEquals(2, actionFeedbackList.size());
//    int countAccepted = 0; int countCompleted = 0;
//    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
//        countAccepted += 1;
//      }
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
//        countCompleted += 1;
//      }
//    }
//    assertEquals(1, countAccepted);
//    assertEquals(1, countCompleted);
//    verify(actionFeedbackPublisher, times(1)).sendFeedback(eq(mockedActionFeedback));
//  }
//
//  @Test
//  public void testProcessInstructionPhoneNumberMultipleSpacesAndParentheses() throws NotificationClientException {
//    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
//            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
//                    NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
//            Outcome.REQUEST_COMPLETED);
//    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);
//
//    actionInstructionReceiver.processInstruction(
//            ObjectBuilder.buildActionInstruction(
//                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,(4)77   42 99 41 31", true));
//
//    verify(notifyService, times(1)).process(any(ActionRequest.class));
//
//    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
//    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
//    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
//    assertEquals(2, actionFeedbackList.size());
//    int countAccepted = 0; int countCompleted = 0;
//    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
//        countAccepted += 1;
//      }
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
//        countCompleted += 1;
//      }
//    }
//    assertEquals(1, countAccepted);
//    assertEquals(1, countCompleted);
//    verify(actionFeedbackPublisher, times(1)).sendFeedback(eq(mockedActionFeedback));
//  }
//
//  @Test
//  public void testProcessInstructionNotifyThrowsException() throws NotificationClientException {
//    NotificationClientException exception = new NotificationClientException(new Exception());
//    when(notifyService.process(any(ActionRequest.class))).thenThrow(exception);
//
//    try {
//      actionInstructionReceiver.processInstruction(
//              ObjectBuilder.buildActionInstruction(
//                      "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131", true));
//      fail();
//    } catch (NotificationClientException e) {
//    }
//
//    verify(notifyService, times(1)).process(any(ActionRequest.class));
//
//    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
//    verify(actionFeedbackPublisher, times(1)).sendFeedback(argumentCaptor.capture());
//    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
//    assertEquals(1, actionFeedbackList.size());
//    int countAccepted = 0;
//    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
//      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
//        countAccepted += 1;
//      }
//    }
//    assertEquals(1, countAccepted);
//  }
}
