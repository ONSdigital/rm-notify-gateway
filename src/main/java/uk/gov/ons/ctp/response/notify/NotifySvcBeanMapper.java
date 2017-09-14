package uk.gov.ons.ctp.response.notify;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.representation.NotifyRequestForEmailDTO;
import uk.gov.ons.ctp.response.notify.representation.NotifyRequestForSMSDTO;
import uk.gov.ons.ctp.response.notify.representation.ResponseDTO;

/**
 * The bean mapper that maps to/from DTOs and JPA entity types.
 *
 */
@Component
public class NotifySvcBeanMapper extends ConfigurableMapper {

    /**
     * Setup the mapper for all of our beans. Only fields having non identical names need mapping if we also use
     * byDefault() following.
     *
     * @param factory the factory to which we add our mappings
     */
    protected final void configure(final MapperFactory factory) {
        factory.classMap(NotifyRequest.class, NotifyRequestForEmailDTO.class).byDefault().register();
        factory.classMap(NotifyRequest.class, NotifyRequestForSMSDTO.class).byDefault().register();

        factory.classMap(Response.class, ResponseDTO.class).byDefault().register();
    }
}
