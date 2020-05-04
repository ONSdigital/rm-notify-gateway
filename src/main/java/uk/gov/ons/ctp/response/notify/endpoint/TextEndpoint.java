package uk.gov.ons.ctp.response.notify.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.net.URI;
import javax.validation.Valid;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.notify.lib.common.CTPEndpoint;
import uk.gov.ons.ctp.response.notify.lib.common.InvalidRequestException;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequestForSMSDTO;
import uk.gov.ons.ctp.response.notify.lib.notify.ResponseDTO;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;

/** The REST endpoint controller for Text Messages */
@RestController
@RequestMapping(value = "/texts", produces = "application/json")
public class TextEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(TextEndpoint.class);

  @Qualifier("notifySvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  @Autowired private ResilienceService resilienceService;

  /**
   * To send a text message using template id
   *
   * @param templateId the GOV.UK Notify text message template id
   * @param notifyRequestForSMSDTO the NotifyRequestForSMSDTO containing phoneNumber and
   *     personalisation
   * @param bindingResult the bindingResult used to validate requests
   * @return the created ResponseDTO
   * @throws InvalidRequestException if binding errors
   */
  @RequestMapping(value = "/{censusUacSmsTemplateId}", method = RequestMethod.POST)
  public ResponseEntity<ResponseDTO> sendSMS(
      @PathVariable("censusUacSmsTemplateId") final String templateId,
      @RequestBody @Valid final NotifyRequestForSMSDTO notifyRequestForSMSDTO,
      BindingResult bindingResult)
      throws InvalidRequestException {
    log.with("template_id", templateId)
        .with("notify_request_for_sms", notifyRequestForSMSDTO)
        .debug("Entering sendSMS");

    if (bindingResult.hasErrors()) {
      throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
    }

    NotifyRequest notifyRequest = mapperFacade.map(notifyRequestForSMSDTO, NotifyRequest.class);
    notifyRequest.setTemplateId(templateId);

    return ResponseEntity.created(URI.create("TODO"))
        .body(mapperFacade.map(resilienceService.process(notifyRequest), ResponseDTO.class));
  }
}
