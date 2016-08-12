/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumer_Client;

/**
 *
 * @author josanvel
 */
import java.util.ArrayList;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class Consumer_Client {
   private static final String URL = "tcp://localhost:61616";
   private static final String USER = ActiveMQConnection.DEFAULT_USER;

   private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

   private static final String DESTINATION_QUEUE = "DISPENSADORES.QUEUE";

   private static final boolean TRANSACTED_SESSION = false;
   private static final int TIMEOUT = 1000;
   private final Map<String, Integer> consumedMessageTypes;

   private int totalConsumedMessages = 0;
   
   ArrayList<SlaveConsume> dispensers = new ArrayList<>();
   
  public Consumer_Client() {
        this.consumedMessageTypes = new HashMap<String, Integer>();
    }
  
   public void processMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        final Connection connection = connectionFactory.createConnection();

        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(DESTINATION_QUEUE);
        final MessageConsumer consumer = session.createConsumer(destination);

        processAllMessagesInQueue(consumer);

        consumer.close();
        session.close();
        connection.close();

        showProcessedResults();
    }

    private void processAllMessagesInQueue(MessageConsumer consumer) throws JMSException {
        Message message;
        while ((message = consumer.receive(TIMEOUT)) != null) {
            proccessMessage(message);
        }
    }

    private void proccessMessage(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            final String text = textMessage.getText();
            incrementMessageType(text);
            totalConsumedMessages++;
            //String str = "This is a sentence.  This is a question, right?  Yes!  It is.";
          

        }
    }

    private void incrementMessageType(String message) {
        if (consumedMessageTypes.get(message) == null) {
            consumedMessageTypes.put(message, 1);
        }
    }
    
    private void showProcessedResults() {
        System.out.println("Procesados un total de " + totalConsumedMessages + " mensajes");
        SlaveConsume cosume;
        for (String messageType : consumedMessageTypes.keySet()) {
            final int numberOfTypeMessages = consumedMessageTypes.get(messageType);
           // System.out.println("Tipo " + messageType + " Procesados " + numberOfTypeMessages + " (" +
             //       (numberOfTypeMessages * 100 / totalConsumedMessages) + "%)");
            String delims = ("/");
            String[] tokens = messageType.split(delims);
            cosume = new SlaveConsume(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),tokens[2]);
            dispensers.add(cosume);
            System.out.println(cosume.getEstado());
    
        }
    }
    
    public static void main(String[] args) throws JMSException {
        final Consumer_Client userActionConsumer = new Consumer_Client();
        userActionConsumer.processMessages();
    }
   
}