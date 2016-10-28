package uk.gov.ons.ctp.response.notify;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;

import javax.inject.Named;

/**
 * The main application class
 */
@Slf4j
@SpringBootApplication
public class Application {
  /**
   * To register classes in the JAX-RS world.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {
    /**
     * Its public constructor.
     */
    public JerseyConfig() {
      log.debug("entering the JerseyConfig constructor...");
      JAXRSRegister.listCommonTypes().forEach(t->register(t));

      System.setProperty("ma.glasnost.orika.writeSourceFiles", "false");
      System.setProperty("ma.glasnost.orika.writeClassFiles", "false");
    }
  }

  /**
   * This method is the entry point to the Spring Boot application.
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the Notify Gateway application...");
    SpringApplication.run(Application.class, args);
  }
}
