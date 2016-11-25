package uk.gov.ons.ctp.response.notify.message;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.math.BigInteger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.NOTIFY_SMS_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestData;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestInvalidData;

/**
 * To unit test ActionInstructionReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionInstructionReceiverImplTest {

  private static final BigInteger MOCKED_ACTIONID = BigInteger.ONE;
  private static final String MOCKED_STATUS = "mocked status";

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
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID, NOTIFY_SMS_SENT, Outcome.REQUEST_COMPLETED, MOCKED_STATUS);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(notifyService, times(3)).process(any(ActionRequest.class));
    verify(actionFeedbackPublisher, times(6)).sendFeedback(any(ActionFeedback.class));
    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionInvalidPhoneNumber() throws CTPException {
    ActionFeedback mockedActionFeedback = new ActionFeedback(MOCKED_ACTIONID, NOTIFY_SMS_SENT, Outcome.REQUEST_COMPLETED, MOCKED_STATUS);
    when(notifyService.process(any(ActionRequest.class))).thenReturn(mockedActionFeedback);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestInvalidData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(actionFeedbackPublisher, times(6)).sendFeedback(any(ActionFeedback.class));
    verify(notifyService, times(2)).process(any(ActionRequest.class));
    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionNotifyThrowsException() throws CTPException {
    CTPException exception = new CTPException(CTPException.Fault.SYSTEM_ERROR);
    when(notifyService.process(any(ActionRequest.class))).thenThrow(exception);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(actionFeedbackPublisher, times(3)).sendFeedback(any(ActionFeedback.class));
    verify(notifyService, times(3)).process(any(ActionRequest.class));  // only the 3 REQUEST_ACCEPTED
    verify(actionInstructionPublisher, times(3)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }
}
