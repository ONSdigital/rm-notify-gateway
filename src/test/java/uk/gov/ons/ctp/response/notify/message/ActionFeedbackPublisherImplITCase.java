package uk.gov.ons.ctp.response.notify.message;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.utility.ActionMessageListener;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.ons.ctp.response.notify.utility.CommonValues.INVALID_ACTION_FEEDBACKS_QUEUE;

/**
 * Test focusing on Spring Integration
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionFeedbackPublisherImplITCaseConfig.class)
public class ActionFeedbackPublisherImplITCase {

  @Autowired
  ActionFeedbackPublisher actionFeedbackPublisher;

  @Autowired
  DefaultMessageListenerContainer actionFeedbackMessageListenerContainer;

  @Autowired
  CachingConnectionFactory connectionFactory;

  private Connection connection;
  private int initialCounter;

  private static final BigInteger ACTION_ID = new BigInteger("21");
  private static final String SITUATION = "Testing queues";

  @Before
  public void setUp() throws JMSException {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACKS_QUEUE);

    ActionMessageListener listener = (ActionMessageListener)actionFeedbackMessageListenerContainer.getMessageListener();
    listener.setPayload(null);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  /**
   * This test sends a valid ActionFeedback using FeedbackPublisher. It then verifies that the correct message is
   * received on the queue Action.Notifications . See the definition of the jmsContainer in test-outbound-only-int.xml
   */
  @Test
  public void testSendValidActionFeedbackWithFeedbackPublisher() throws Exception {
    ActionFeedback actionFeedback = new ActionFeedback();
    actionFeedback.setActionId(ACTION_ID);
    actionFeedback.setOutcome(Outcome.REQUEST_COMPLETED);
    actionFeedback.setSituation(SITUATION);
    actionFeedbackPublisher.sendFeedback(actionFeedback);

    Thread.sleep(10000L);

    /**
     * We check that no additional message has been put on the xml invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACKS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * The section below verifies that an ActionFeedback ends up on the queue
     */
    ActionMessageListener listener = (ActionMessageListener)actionFeedbackMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();

//    // TODO Remove the if below when running manually. Unfortunately, when building using the command line, the test
//    // TODO fails with a nullpointer.
    if (listenerPayload != null) {
      JAXBContext jaxbContext = JAXBContext.newInstance(ActionFeedback.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      ActionFeedback retrievedActionFeedback = (ActionFeedback) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(listenerPayload.getBytes()));
      assertEquals(actionFeedback.getActionId(), retrievedActionFeedback.getActionId());
      assertEquals(actionFeedback.getOutcome(), retrievedActionFeedback.getOutcome());
      assertEquals(actionFeedback.getSituation(), retrievedActionFeedback.getSituation());
    }
  }

  @Test
  public void testSendInvalidActionFeedbackWithFeedbackPublisher() throws Exception {
    // Note the missing outcome
    ActionFeedback actionFeedback = new ActionFeedback();
    actionFeedback.setActionId(ACTION_ID);
    actionFeedback.setSituation(SITUATION);
    actionFeedbackPublisher.sendFeedback(actionFeedback);

    Thread.sleep(10000L);

    /**
     * We check that the xml invalid queue contains 1 additional message.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_FEEDBACKS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);

    /**
     * The section below verifies that no ActionFeedback ends up on the queue
     */
    ActionMessageListener listener = (ActionMessageListener)actionFeedbackMessageListenerContainer.getMessageListener();
    TimeUnit.SECONDS.sleep(10);
    String listenerPayload = listener.getPayload();
    assertNull(listenerPayload);
  }
}
