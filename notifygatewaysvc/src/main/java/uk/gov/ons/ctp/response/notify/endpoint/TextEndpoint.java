package uk.gov.ons.ctp.response.notify.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.response.notify.domain.TextMessageRequest;
import uk.gov.ons.ctp.response.notify.representation.SendSmsResponseDTO;
import uk.gov.ons.ctp.response.notify.representation.TextMessageRequestDTO;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.service.notify.SendSmsResponse;

import javax.validation.Valid;
import java.net.URI;

/**
 * The REST endpoint controller for Text Messages
 */
@RestController
@RequestMapping(value = "/texts", produces = "application/json")
@Slf4j
public class TextEndpoint implements CTPEndpoint {

    @Qualifier("notifySvcBeanMapper")
    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private NotifyService notifyService;

    /**
     * To send a text message using template id
     *
     * @param templateId the GOV.UK Notify text message template id
     * @param textMessageRequestDTO the TextMessageRequestDTO containing phoneNumber and personalisation
     * @param bindingResult the bindingResult used to validate requests
     * @return the created SendSmsResponseDTO
     * @throws CTPException on failure to create CaseEvent
     * @throws InvalidRequestException if binding errors
     */
    @RequestMapping(value = "/{templateId}", method = RequestMethod.POST)
    public ResponseEntity<SendSmsResponseDTO> sendTextMessage(@PathVariable("templateId") final String templateId,
                                                              @RequestBody @Valid final TextMessageRequestDTO textMessageRequestDTO,
                                                              BindingResult bindingResult) throws CTPException, InvalidRequestException {
        log.info("Entering sendTextMessage with templateId {} and requestObject {}", templateId, textMessageRequestDTO);

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException("Binding errors for case event creation: ", bindingResult);
        }

        // TODO Publish to queue here rather than processing straight away.

        TextMessageRequest textMessageRequest = mapperFacade.map(textMessageRequestDTO, TextMessageRequest.class);
        textMessageRequest.setTemplateId(templateId);
        SendSmsResponse sendSmsResponse = notifyService.process(textMessageRequest);

        // TODO Define URI
        return ResponseEntity.created(URI.create("TODO")).body(mapperFacade.map(sendSmsResponse, SendSmsResponseDTO.class));
    }
}
