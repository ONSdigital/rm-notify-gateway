package uk.gov.ons.ctp.response.notify.lib.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** Custom Object Mapper */
public class CustomObjectMapper extends ObjectMapper {

  /** Custom Object Mapper Constructor */
  public CustomObjectMapper() {
    this.setDateFormat(new MultiIsoDateFormat());
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.registerModule(new JavaTimeModule());
    this.findAndRegisterModules();
  }
}
