package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Created by centos on 28/10/16.
 */
public interface NotifyService {
  void process(ActionInstruction actionInstruction) throws CTPException;
}
