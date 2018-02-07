package uk.gov.ons.ctp.response.notify.config;

import lombok.Builder;
import lombok.Data;

/**
 * Config POJO for Swagger UI Generation
 */
@Data
@Builder
public class SwaggerSettings {

  private Boolean swaggerUiActive;
  private String groupName;
  private String title;
  private String description;
  private String version;

}
