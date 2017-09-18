package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
//import uk.gov.service.notify.NotificationClientException;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * The service that reads ActionInstructions from the inbound channel
 */
public interface ActionInstructionReceiver {
  /**
   * To process ActionInstructions from the input channel actionInstructionTransformed
   * @param instruction the ActionInstruction to be processed
//   * @throws NotificationClientException when an error is received from GOV.UK Notify
   * @throws DatatypeConfigurationException when formatCalendar fails
   */
  void processInstruction(ActionInstruction instruction) throws DatatypeConfigurationException;
//      throws NotificationClientException, DatatypeConfigurationException;
}
