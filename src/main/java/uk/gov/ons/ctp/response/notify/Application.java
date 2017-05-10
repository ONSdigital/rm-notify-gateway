package uk.gov.ons.ctp.response.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;

/**
 * The main application class
 */
@Slf4j
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@ImportResource("springintegration/main.xml")
@SpringBootApplication
public class Application {
  /**
   * This method is the entry point to the Spring Boot application.
   * 
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the Notify Gateway application...");
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }

  @Bean @Primary
  public CustomObjectMapper CustomObjectMapper() {
    return new CustomObjectMapper();
  }
}
