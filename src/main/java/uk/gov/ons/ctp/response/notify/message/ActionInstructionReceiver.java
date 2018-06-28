package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.service.notify.NotificationClientException;

/** The service that reads ActionInstructions from the inbound channel */
public interface ActionInstructionReceiver {
  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   *
   * @param instruction the ActionInstruction to be processed
   * @throws NotificationClientException when an error is received from GOV.UK Notify
   * @throws CommsTemplateClientException when an error is received from the comms template service
   */
  void processInstruction(ActionInstruction instruction)
      throws NotificationClientException, CommsTemplateClientException;
}
