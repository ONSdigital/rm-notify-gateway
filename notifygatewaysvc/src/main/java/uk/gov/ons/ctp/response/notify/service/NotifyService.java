package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.service.notify.SendSmsResponse;

/**
 * Service to interact with GOV.UK Notify
 */
public interface NotifyService {
  /**
   * To process an ActionRequest. It sends an SMS using GOV.UK Notify.
   *
   * @param actionRequest to be processed
   * @return the associated ActionFeedback
   * @throws CTPException if GOV.UK Notify gives an issue
   */
  ActionFeedback process(ActionRequest actionRequest) throws CTPException;

  /**
   * To process a NotifyRequest. It sends an SMS or an email using GOV.UK Notify.
   *
   * @param notifyRequest to be processed
   * @throws CTPException if GOV.UK Notify gives an issue
   */
  void process(NotifyRequest notifyRequest) throws CTPException;
}
