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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Consumer_Client {
   private static final String URL = "tcp://localhost:61616";
   private static final String USER = ActiveMQConnection.DEFAULT_USER;
   private int a = 0;
   private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

   private static final String DESTINATION_QUEUE = "DISPENSADORES.QUEUE";

   private static final boolean TRANSACTED_SESSION = false;
   private static final int TIMEOUT = 1000;
   private final Map<String, Integer> consumedMessageTypes;

   private int totalConsumedMessages = 0;
   
   List<SlaveConsume> dispensers = new ArrayList<>();
   
  public Consumer_Client() {
        this.consumedMessageTypes = new HashMap<String, Integer>();
    }
  
   public void processMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        final Connection connection = connectionFactory.createConnection();

        connection.start();

        final Session session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        final Destination destination = session.createQueue(DESTINATION_QUEUE);
        //final MessageConsumer consumer = session.createConsumer(destination);
       
        //consumer.close();
        boolean var = true;
        int i = 0;
        final int x = dispensers.size();
       // while(var){
            QueueBrowser queueConsumer = session.createBrowser((Queue) destination);
            Enumeration<?> messagesInQueue = queueConsumer.getEnumeration();
            while (messagesInQueue.hasMoreElements()) {
                Message peek = (Message)messagesInQueue.nextElement();
                TextMessage textMessage = (TextMessage) peek;
                String text = textMessage.getText();
                if (i == 0) {
                    prueba(text);
                 }
            }
            
            for (SlaveConsume dispenser : dispensers) {
                 System.out.println(dispenser.toString());
            }
            //queueConsumer.close();
            i++;
        //}
        //session.close();
        //connection.close();
        //showProcessedResults();
    }

    private void processAllMessagesInQueue(QueueBrowser queueConsumer) throws JMSException {
        Enumeration<?> messagesInQueue = queueConsumer.getEnumeration();
       
        while (messagesInQueue.hasMoreElements()) {
            Message msjConsumer = (Message) messagesInQueue.nextElement();
            proccessMessage(msjConsumer);
           
        }
        
    }

    private void proccessMessage(Message message) throws JMSException {
       int i = 0;
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            if (i==0){
                incrementMessageType(text);
                totalConsumedMessages++;
                showProcessedResults();
                
            }
           
        }
    }

    private void incrementMessageType(String message) {
        if (consumedMessageTypes.get(message) == null) {
            consumedMessageTypes.put(message, 1);
        }
    }
    
    private void showProcessedResults() {
        //System.out.println("Procesados un total de " + totalConsumedMessages + " mensajes");
        SlaveConsume consumer, lastConsumer = new SlaveConsume(0, 0, "");  
        for (String messageType : consumedMessageTypes.keySet()) {
            final int numberOfTypeMessages = consumedMessageTypes.get(messageType);
           // System.out.println("Tipo " + messageType + " Procesados " + numberOfTypeMessages + " (" +
           // (numberOfTypeMessages * 100 / totalConsumedMessages) + "%)");
            String delims = ("/");
            String[] tokens = messageType.split(delims);
            consumer = new SlaveConsume(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),tokens[2]);
            dispensers.add(consumer); 
            Collections.sort(dispensers, new Comparator<SlaveConsume>(){
                @Override
                public int compare(SlaveConsume o1, SlaveConsume o2) {
                 return new Integer(o1.getId()).compareTo(o2.getId());
                }              
            });
            lastConsumer = dispensers.get(dispensers.size()-1);
            for (SlaveConsume dispenser : dispensers) {
                 System.out.println(dispenser.toString());
            }
        }
        //System.out.println("hoa"+lastConsumer.getId());
       
    }
    private void prueba(String texto){
        SlaveConsume consumer;
        String delims = ("/");
        String[] tokens = texto.split(delims);
        consumer = new SlaveConsume(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]),tokens[2]);
        dispensers.add(consumer); 
        Collections.sort(dispensers, new Comparator<SlaveConsume>(){
                @Override
                public int compare(SlaveConsume o1, SlaveConsume o2) {
                 return new Integer(o1.getId()).compareTo(o2.getId());
                }              
            }); 

    }
    
    public static void main(String[] args) throws JMSException {
        final Consumer_Client userActionConsumer = new Consumer_Client();
        userActionConsumer.processMessages();
    }
   
}