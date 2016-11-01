package uk.gov.ons.ctp.response.notify.message;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;

import javax.jms.Connection;
import javax.jms.JMSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.notify.utility.CommonValues.INVALID_ACTION_INSTRUCTIONS_QUEUE;
import static uk.gov.ons.ctp.response.notify.utility.CommonValues.PACKAGE_ACTION_INSTRUCTION;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstructionReceiverImplITCaseConfig.class)
public class InstructionReceiverImplITCase {

  @Autowired
  MessageChannel actionInstructionXml;

  @Autowired
  @Qualifier("actionInstructionUnmarshaller")
  Jaxb2Marshaller actionInstructionUnmarshaller;

  @Autowired
  CachingConnectionFactory connectionFactory;

  private Connection connection;
  private int initialCounter;

  @Before
  public void setUp() throws Exception {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);

    String jaxbContext = actionInstructionUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_INSTRUCTION));
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void testSendInvalidActionInstruction() throws Exception {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionInstruction.xml"), "UTF-8");

    // Note that we bypass the queue Action.Notify and go straight to actionInstructionXml
    actionInstructionXml.send(MessageBuilder.withPayload(testMessage).build());

    Thread.sleep(10000L);

    /**
     * We check that the bad xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);
  }

  @Test
  public void testSendValidActionInstructionWithRequestOnly() throws Exception {
    // message below created by IntelliJ using the XSD schema in responsemanagement/actionsvc-api
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/actionInstructionWithRequestOnly.xml"), "UTF-8");

    // Note that we bypass the queue Action.Notify and go straight to actionInstructionXml
    actionInstructionXml.send(MessageBuilder.withPayload(testMessage).build());

    Thread.sleep(10000L);

    /**
     * We check that no additional message has been put on the xml invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(initialCounter, finalCounter);
  }

  private File provideTempFile(String inputStreamLocation) throws IOException {
    InputStream is = getClass().getResourceAsStream(inputStreamLocation);
    File tempFile = File.createTempFile("prefix","suffix");
    tempFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(tempFile);
    IOUtils.copy(is, out);
    return tempFile;
  }
}
