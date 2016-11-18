package uk.gov.ons.ctp.response.notify.message;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Test focusing on Spring Integration
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionInstructionReceiverImplITCaseConfig.class)
public class ActionInstructionReceiverImplITCase {

  @Inject
  private MessageChannel actionInstructionXml;

  @Inject
  private MessageChannel testOutbound;

  @Inject
  private QueueChannel activeMQDLQXml;

  @Inject
  @Qualifier("actionInstructionUnmarshaller")
  private Jaxb2Marshaller actionInstructionUnmarshaller;

  @Inject
  private CachingConnectionFactory connectionFactory;

  @Inject
  private NotifyService notifyService;

  private Connection connection;
  private int initialCounter;

  private static final int RECEIVE_TIMEOUT = 5000;
  private static final String INVALID_ACTION_INSTRUCTIONS_QUEUE = "Action.InvalidActionInstructions";
  private static final String PACKAGE_ACTION_INSTRUCTION = "uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction";

  @Before
  public void setUp() throws Exception {
    Date now = new Date();
    long startSetUp = now.getTime();

    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);

    String jaxbContext = actionInstructionUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_INSTRUCTION));

    activeMQDLQXml.clear();

    reset(notifyService);

    now = new Date();
    long endSetUp = now.getTime();
    log.debug("time takenBySetup: {}", endSetUp - startSetUp);
  }

  @After
  public void finishCleanly() throws JMSException {
    Date now = new Date();
    long startFinishCleanly = now.getTime();

    connection.close();

    now = new Date();
    long endFinishCleanly = now.getTime();
    log.debug("time takenByFinishCleanly: {}", endFinishCleanly - startFinishCleanly);
  }

  @Test
  public void testSendActionInstructionInvalidXml() throws Exception {
    Date now = new Date();
    long startXmlInvalid = now.getTime();

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionInstruction.xml"), "UTF-8");
    actionInstructionXml.send(MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the invalid xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);

    now = new Date();
    long endXmlInvalid = now.getTime();
    log.debug("time takenByXmlInvalid: {}", endXmlInvalid - startXmlInvalid);
  }

  @Test
  public void testSendActionInstructiontXmlBadlyFormed() throws IOException, JMSException {
    Date now = new Date();
    long startBadlyFormed = now.getTime();

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/badlyFormedActionInstruction.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the badly formed xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    /**
     * We check that no badly formed xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(0, finalCounter - initialCounter);

    now = new Date();
    long endBadlyFormed = now.getTime();
    log.debug("time takenByBadlyFormed: {}", endBadlyFormed - startBadlyFormed);
  }

  @Test
  public void testSendValidActionInstructionWithRequestOnly() throws Exception {
    Date now = new Date();
    long startXmlValid = now.getTime();

    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch notifyServiceInvoked = new CountDownLatch(1);
    doAnswer(countsDownLatch(notifyServiceInvoked)).when(notifyService).process(any(ActionRequest.class));

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/actionInstructionWithRequestOnly.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    notifyServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no additional message has been put on the xml invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that no xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    assertNull(message);

    /**
     * We check the message was processed
     */
    verify(notifyService).process(any(ActionRequest.class));

    now = new Date();
    long endXmlValid = now.getTime();
    log.debug("time takenByXmlValid: {}", endXmlValid - startXmlValid);
  }

  /**
   * Should be called when mock method is called in asynchronous test to countDown the CountDownLatch test thread is
   * waiting on.
   *
   * @param serviceInvoked CountDownLatch to countDown
   * @return Answer<CountDownLatch> Mockito Answer object
   */
  private Answer<CountDownLatch> countsDownLatch(final CountDownLatch serviceInvoked) {
    return new Answer<CountDownLatch>() {
      @Override
      public CountDownLatch answer(InvocationOnMock invocationOnMock) throws Throwable {
        serviceInvoked.countDown();
        return null;
      }
    };
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
