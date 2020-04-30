package uk.gov.ons.ctp.response.notify.client;

import uk.gov.ons.ctp.response.notify.lib.common.CTPException;

public class CommsTemplateClientException extends CTPException {

  public CommsTemplateClientException(final CTPException.Fault fault, final String message) {
    super(fault, message);
  }
}
