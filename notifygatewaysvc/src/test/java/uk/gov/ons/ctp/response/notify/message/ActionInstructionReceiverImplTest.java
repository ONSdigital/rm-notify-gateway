package uk.gov.ons.ctp.response.notify.message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl.SITUATION_MAX_LENGTH;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;

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
  private ActionInstructionPublisher actionInstructionPublisher;

  @Mock
  private NotifyService notifyService;

  @Mock
  private Tracer tracer;

  @Test
  public void testProcessInstructionHappyPath() throws CTPException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
            NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(
            "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131", true));

    verify(tracer, times(1)).createSpan(any(String.class));
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

    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionInvalidPhoneNumber() throws CTPException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
            NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(
            ObjectBuilder.buildActionInstruction(
                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,0774", true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(notifyService, times(0)).process(any(ActionRequest.class));

    ArgumentCaptor<ActionFeedback> argumentCaptor = ArgumentCaptor.forClass(ActionFeedback.class);
    verify(actionFeedbackPublisher, times(2)).sendFeedback(argumentCaptor.capture());
    List<ActionFeedback> actionFeedbackList = argumentCaptor.getAllValues();
    assertEquals(2, actionFeedbackList.size());
    int countAccepted = 0; int countDeclined = 0; int countCompleted = 0;
    for (ActionFeedback anActionFeedback :  actionFeedbackList) {
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_ACCEPTED)) {
        countAccepted += 1;
      }
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_DECLINED)) {
        countDeclined += 1;
      }
      if (anActionFeedback.getOutcome().equals(Outcome.REQUEST_COMPLETED)) {
        countCompleted += 1;
      }
    }
    assertEquals(1, countAccepted);
    assertEquals(1, countDeclined);
    assertEquals(0, countCompleted);
    verify(actionFeedbackPublisher, times(0)).sendFeedback(eq(mockedActionFeedback));

    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionPhoneNumberForCtpa1170() throws CTPException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
                    NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(
            ObjectBuilder.buildActionInstruction(
                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742 994131", true));

    verify(tracer, times(1)).createSpan(any(String.class));
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

    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionPhoneNumberMultipleSpacesAndParentheses() throws CTPException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID,
            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
                    NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(
            ObjectBuilder.buildActionInstruction(
                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,(4)77   42 99 41 31", true));

    verify(tracer, times(1)).createSpan(any(String.class));
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

    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionNotifyThrowsException() throws CTPException {
    CTPException exception = new CTPException(CTPException.Fault.SYSTEM_ERROR);
    when(notifyService.process(any(ActionRequest.class))).thenThrow(exception);

    actionInstructionReceiver.processInstruction(
            ObjectBuilder.buildActionInstruction(
                    "9a5f2be5-f944-41f9-982c-3517cfcfef3c", "Joe,Blogg,07742994131", true));

    verify(tracer, times(1)).createSpan(any(String.class));
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

    verify(actionInstructionPublisher, times(1)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }
}
