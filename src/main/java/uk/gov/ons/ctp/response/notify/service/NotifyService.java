package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service to interact with GOV.UK Notify
 */
public interface NotifyService {
  /**
   * To process an ActionInstruction. It reads all embedded ActionRequests and for each one, it sends an SMS using
   * GOV.UK Notify.
   *
   * @param actionInstruction to be processed
   * @throws CTPException if GOV.UK Notify gives an issue
   */
  void process(ActionInstruction actionInstruction) throws CTPException;
}
