package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service to interact with GOV.UK Notify
 */
public interface NotifyService {
  void process(ActionInstruction actionInstruction) throws CTPException;
}
