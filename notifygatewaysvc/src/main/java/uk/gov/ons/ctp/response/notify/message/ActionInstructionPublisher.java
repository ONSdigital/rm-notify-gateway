package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * The publisher of ActionInstructions to queue
 */
public interface ActionInstructionPublisher {
  /**
   * To publish a actionInstruction to queue
   * @param actionInstruction to be published
   */
  void send(ActionInstruction actionInstruction);
}
