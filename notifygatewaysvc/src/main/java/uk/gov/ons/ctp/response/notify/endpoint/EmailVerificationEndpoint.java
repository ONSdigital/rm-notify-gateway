package uk.gov.ons.ctp.response.notify.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.representation.SendEmailDTO;

import java.util.UUID;

/**
 * The REST endpoint controller for EmailVerification via Notify Gateway
 */
@RestController
@RequestMapping(value = "/notify", produces = "application/json")
@Slf4j
public class EmailVerificationEndpoint implements CTPEndpoint {

  @RequestMapping(value = "/emails/{emailTemplateId}", method = RequestMethod.POST)
  public ResponseEntity<SendEmailDTO> sendEmail(@PathVariable("emailTemplateId") final UUID emailTemplateId) throws CTPException {

    SendEmailDTO sendEmailDTO = new SendEmailDTO();
    sendEmailDTO.setNotificationId(UUID.fromString("845c73f5-e016-4610-8bfe-7699e9f4a3c2"));
    sendEmailDTO.setReference("Test Email");
    sendEmailDTO.setTemplateId(emailTemplateId);
    sendEmailDTO.setTemplateVersion(1);
    sendEmailDTO.setFromEmail("surveys@ons.gov.uk");

    return ResponseEntity.ok(sendEmailDTO);
  }

}
