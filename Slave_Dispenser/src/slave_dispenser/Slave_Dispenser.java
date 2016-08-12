/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slave_dispenser;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josanvel
 */
public class Slave_Dispenser {
    public enum ProductorAction {
        
        DISPENSADOR("EL DISPENSADOR DE ALIMENTO ESTA VACIO"),
        PROGRESO("PROCESANDO...");
        
        private final String productorAction;

        private ProductorAction(String productorAction) {
            this.productorAction = productorAction;
        }

        public String getActionAsString() {
            return this.productorAction;
        }
    }   
    
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final String URL = "tcp://localhost:61616";
    private static final String USER = ActiveMQConnection.DEFAULT_USER;
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    private static final String DESTINATION_QUEUE = "DISPENSADORES.QUEUE";
    private static final boolean TRANSACTED_SESSION = true;
        
    private static final int CAPACITY_DISPENSER = 20;
    private static final int NUMBER_SLAVE = 3;
    public Slave[] arr = new Slave[NUMBER_SLAVE];
    public Slave_Dispenser messageSender;
    
    private static Session session = null;
    private static Destination destination = null;
    private static MessageProducer producer = null;
    
    int num = 0;
    
    public void sendMessages() throws JMSException {

        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(USER, PASSWORD, URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(TRANSACTED_SESSION, Session.AUTO_ACKNOWLEDGE);
        destination = session.createQueue(DESTINATION_QUEUE);

        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        sendMessages(session, producer);
        session.commit();

        session.close();
        connection.close();

        System.out.println("Mensajes enviados correctamente");
    }
    
    public class MyThread implements Runnable {
        int n;
        public MyThread(int parameter) {
            // store parameter for later user
            n = parameter;
        }

        public void run() {
            int random_porcion = 0;
            int value_new = 0;
            int capacidad = 0;
            capacidad = arr[n].getCapacidad();
            while(capacidad > 0){
                random_porcion = randInt(1, 4);
                capacidad = capacidad - random_porcion;
                arr[n].setCapacidad(capacidad);
                if (arr[n].getCapacidad() < 1) {
                    final ProductorAction productorActionToSend = ProductorAction.values()[0];
                    System.out.println(arr[n].getId()+"   Mensajes Terminado: "+productorActionToSend.getActionAsString());

                    try {
                        messageSender.sendMessage(productorActionToSend.getActionAsString(), session, producer);
                    } catch (JMSException ex) {
                        Logger.getLogger(Slave_Dispenser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    final ProductorAction productorActionToSend = ProductorAction.values()[1];
                    System.out.println(arr[n].getId()+"  Mensajes Terminado: "+productorActionToSend.getActionAsString());

                    try {
                        messageSender.sendMessage(productorActionToSend.getActionAsString(), session, producer);
                    } catch (JMSException ex) {
                        Logger.getLogger(Slave_Dispenser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
     }
    
    private void sendMessages(Session session, MessageProducer producer) throws JMSException {
        
        messageSender = new Slave_Dispenser();
        
        for( int i=0; i<NUMBER_SLAVE; i++ ){
            arr[i] = new Slave(i, CAPACITY_DISPENSER);
        }
        
        Thread[] threads = new Thread[NUMBER_SLAVE];
        for (num=0; num<NUMBER_SLAVE; num++ ) {
            Runnable r = new MyThread(num);
            threads[num] = new Thread(r);
        }
        
        for (Thread thread : threads ) {
            thread.start(); 
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Slave_Dispenser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 
    private void sendMessage(String message, Session session, MessageProducer producer) throws JMSException {
        final TextMessage textMessage = session.createTextMessage(message);
        producer.send(textMessage);
    }

    private static ProductorAction getRandomUserAction() {
        final int userActionNumber = (int) (RANDOM.nextFloat() * ProductorAction.values().length);
        return ProductorAction.values()[userActionNumber];
    }
    
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static void main(String[] args) throws JMSException {
        final Slave_Dispenser messageSender = new Slave_Dispenser();
        messageSender.sendMessages();
    }   
}