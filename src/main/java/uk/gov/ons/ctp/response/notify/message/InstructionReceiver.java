package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
public interface InstructionReceiver {
  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
   * @throws CTPException when an error is received from Kirona DRS
   * @throws DatatypeConfigurationException when formatCalendar fails
   */
  void processInstruction(ActionInstruction instruction) throws CTPException, DatatypeConfigurationException;
}
