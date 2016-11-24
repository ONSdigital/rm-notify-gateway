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
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClientException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestData;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestInvalidData;

/**
 * To unit test ActionInstructionReceiverImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionInstructionReceiverImplTest {

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
    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(notifyService, times(3)).process(any(ActionRequest.class));
    verify(actionFeedbackPublisher, times(3)).sendFeedback(any(ActionFeedback.class));
    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionInvalidPhoneNumber() throws CTPException {
    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestInvalidData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(notifyService, times(2)).process(any(ActionRequest.class));
    verify(actionFeedbackPublisher, times(2)).sendFeedback(any(ActionFeedback.class));
    verify(actionInstructionPublisher, times(0)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }

  @Test
  public void testProcessInstructionNotifyThrowsException() throws CTPException {
    CTPException exception = new CTPException(CTPException.Fault.SYSTEM_ERROR);
    when(notifyService.process(any(ActionRequest.class))).thenThrow(exception);

    actionInstructionReceiver.processInstruction(ObjectBuilder.buildActionInstruction(buildTestData(), true));

    verify(tracer, times(1)).createSpan(any(String.class));
    verify(notifyService, times(3)).process(any(ActionRequest.class));
    verify(actionFeedbackPublisher, times(0)).sendFeedback(any(ActionFeedback.class));
    verify(actionInstructionPublisher, times(3)).send(any(ActionInstruction.class));
    verify(tracer, times(1)).close(any(Span.class));
  }
}
