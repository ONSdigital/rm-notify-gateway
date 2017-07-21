package uk.gov.ons.ctp.response.notify.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.domain.TextMessageRequest;
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
   * To process a TextMessageRequest. It sends an SMS using GOV.UK Notify.
   *
   * @param textMessageRequest to be processed
   * @return the GOV.UK Notify SendSmsResponse
   * @throws CTPException if GOV.UK Notify gives an issue
   */
  SendSmsResponse process(TextMessageRequest textMessageRequest) throws CTPException;
}
