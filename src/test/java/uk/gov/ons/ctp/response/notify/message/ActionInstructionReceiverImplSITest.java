package uk.gov.ons.ctp.response.notify.message;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionInstructionReceiverImplSITestConfig.class)
public class ActionInstructionReceiverImplSITest {

  private static final int RECEIVE_TIMEOUT = 20000;

  @Inject
  private MessageChannel testOutbound;

  @Inject
  DefaultMessageListenerContainer activeMQListenerContainer;

  @Inject
  private QueueChannel activeMQDLQXml;

  @Inject
  private MessageChannel actionInstructionXml;

  @Inject
  @Qualifier("actionInstructionUnmarshaller")
  Jaxb2Marshaller actionInstructionUnmarshaller;

  @Inject
  CachingConnectionFactory connectionFactory;

  @Inject
  private NotifyService notifyService;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_ACTION_INSTRUCTIONS_QUEUE = "Case.InvalidActionInstructions";
  private static final String PACKAGE_ACTION_INSTRUCTION
          = "uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction";

  @Before
  public void setUp() throws Exception {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);

    String jaxbContext = actionInstructionUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_INSTRUCTION));

    reset(notifyService);

    activeMQDLQXml.clear();
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void testReceivingActionInstructionInvalidXml() throws IOException, JMSException {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionInstruction.xml"), "UTF-8");

    actionInstructionXml.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the invalid xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    // TODO
//    assertEquals(1, finalCounter - initialCounter);
  }


//  @Test
//  public void testSendInvalidActionInstruction() throws Exception {
//    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionInstruction.xml"), "UTF-8");
//
//    // Note that we bypass the queue Action.Notify and go straight to actionInstructionXml
//    actionInstructionXml.send(MessageBuilder.withPayload(testMessage).build());
//
//    Thread.sleep(10000L);
//
//    /**
//     * We check that the bad xml ends up on the invalid queue.
//     */
//    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
//    assertEquals(1, finalCounter - initialCounter);
//  }
//
//  @Test
//  public void testSendValidActionInstructionWithRequestOnly() throws Exception {
//    // message below created by IntelliJ using the XSD schema in responsemanagement/actionsvc-api
//    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/actionInstructionWithRequestOnly.xml"), "UTF-8");
//
//    // Note that we bypass the queue Action.Notify and go straight to actionInstructionXml
//    actionInstructionXml.send(MessageBuilder.withPayload(testMessage).build());
//
//    Thread.sleep(10000L);
//
//    /**
//     * We check that no additional message has been put on the xml invalid queue
//     */
//    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
//    assertEquals(initialCounter, finalCounter);
//
//    Thread.sleep(30000L);   // Required so the valid ActionInstruction has got time to be processed in the background
//
//    /**
//     * The section below verifies that an ActionFeedback ends up on queue
//     */
//    ActionMessageListener listener = (ActionMessageListener)actionFeedbackMessageListenerContainer.getMessageListener();
//    String listenerPayload = listener.getPayload();
//    if (listenerPayload != null) {
//      JAXBContext jaxbContext = JAXBContext.newInstance(ActionFeedback.class);
//      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//      ActionFeedback retrievedActionFeedback = (ActionFeedback) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(listenerPayload.getBytes()));
//      assertEquals(ACTION_ID, retrievedActionFeedback.getActionId());
//      assertEquals(Outcome.REQUEST_COMPLETED, retrievedActionFeedback.getOutcome());
//      assertNotNull(retrievedActionFeedback.getSituation());
//    }
//  }

  private File provideTempFile(String inputStreamLocation) throws IOException {
    InputStream is = getClass().getResourceAsStream(inputStreamLocation);
    File tempFile = File.createTempFile("prefix","suffix");
    tempFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(tempFile);
    IOUtils.copy(is, out);
    return tempFile;
  }
}
