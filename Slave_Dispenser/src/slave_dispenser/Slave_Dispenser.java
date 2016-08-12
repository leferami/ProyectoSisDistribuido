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
import java.util.Scanner;
/**
 *
 * @author josanvel
 */
public class Slave_Dispenser {
    public enum ProductorAction {
        //Mensajes que envia el productor al consumidor
        DISPENSADOR("EL DISPENSADOR DE ALIMENTO ESTA VACIO"),
        PROGRESO("PROCESANDO...");
        
        //Accion del productor
        private final String productorAction;
        //Constructor de la accion del productor
        private ProductorAction(String productorAction) {
            this.productorAction = productorAction;
        }
        //Obtener el string del mensaje que pasa el consumidor
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
        
    private static final int CAPACITY_DISPENSER = 20;               //Capacidad de los dispensadores
    private static final int NUMBER_SLAVE = 3;                      //Cantidad de dispensadores esclavo
    public Slave[] dispensers_slaves = new Slave[NUMBER_SLAVE];     //Arreglo de dispensadores esclavos
    public Slave_Dispenser messageSender;                           
    
    private static Session session = null;                          //Declaracion de la Session
    private static Destination destination = null;                  //Declaracion de la destination
    private static MessageProducer producer = null;                 //Declaracion de la producer
        
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
        int indice;
        public MyThread(int parameter) {
            // store parameter for later user
            indice = parameter;
        }

        @Override
        public void run() {
            int random_porcion = 0, capacidad = 0;
            //Obtengo la capacidad del dispensador
            capacidad = dispensers_slaves[indice].getCapacidad();
            while(capacidad > 0){
                random_porcion = randInt(1, 4);                         //Simular el comportamiento de un dispensador   
                capacidad = capacidad - random_porcion;                 //Obtengo la capacidad actual
                dispensers_slaves[indice].setCapacidad(capacidad);      //Guarda la capacidad actual
                
                int id = dispensers_slaves[indice].getId();
                int actual_capac = dispensers_slaves[indice].getCapacidad();
                    
                    
                if (dispensers_slaves[indice].getCapacidad() < 1) {
                    final ProductorAction productorActionToSend = ProductorAction.values()[0];
                    String comentario = productorActionToSend.getActionAsString();
                    String mensaje_productor = id+"/"+actual_capac+"/"+comentario;
                    System.out.println(dispensers_slaves[indice].getId()+" Mensajes: "+mensaje_productor);

                    try {
                        //Envia el mensaje para que se consuma.
                        messageSender.sendMessage(mensaje_productor, session, producer);
                    } catch (JMSException ex) {
                        Logger.getLogger(Slave_Dispenser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    //Obtengo el mensaje correspondiente por el productor
                    final ProductorAction productorActionToSend = ProductorAction.values()[1];
                    String comentario = productorActionToSend.getActionAsString();
                    String mensaje_productor = id+"/"+actual_capac+"/"+comentario;
                    System.out.println(dispensers_slaves[indice].getId()+" Mensajes: "+mensaje_productor);

                    try {
                        //Envia el mensaje para que se consuma.
                        messageSender.sendMessage(mensaje_productor, session, producer);
                    } catch (JMSException ex) {
                        Logger.getLogger(Slave_Dispenser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
     }
    
    private void sendMessages(Session session, MessageProducer producer) throws JMSException {
        
        messageSender = new Slave_Dispenser();
        //Setea el numero de Dispensadores con ID, CAPACIDAD
        for( int i=0; i<NUMBER_SLAVE; i++ ){
            dispensers_slaves[i] = new Slave(i, CAPACITY_DISPENSER);
        }
        //Crea la cantidad de hilo con el numero de dispensadores
        Thread[] threads = new Thread[NUMBER_SLAVE];
        for (int num = 0; num < NUMBER_SLAVE; num++ ) {
            Runnable r = new MyThread(num);
            threads[num] = new Thread(r);
        }
        //Recorrer todos los hilos para iniciar su ejecucion
        for (Thread thread : threads ) {
            thread.start(); 
        }
        //Recorrer todos los hilos para esperarlos y terminar su ejecucion
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
    //Funcion para hallar el random entre dos numeros
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static void menu(){
        System.out.println("\n\to*****************************************************************o");
        System.out.println("\to\t\tProyecto de Sistemas Distribuidos\t\t  o");                      
        System.out.println("\to\tSistema distribuido para un conjunto de dispensador       o");
        System.out.println("\to\t de alimentos para animales domestico.                    o");
        System.out.println("\to\t\t\tLeonel FERNADO Ramirez Gonzalez           o");
        System.out.println("\to\t\t\tJose Antonio Velez Gomez                  o");
        System.out.println("\to*****************************************************************o\n\t");
        
    }
    public static void main(String[] args) throws JMSException {
        Scanner sc_no_slaves, sc_capacity_dispenser, sc_destination_queue;
        int no_slaves, capacity_dispenser, destination_queue;
        String name_queue;
        
        menu();
     
        System.out.print("\tIngrese la cantidad de dispensadores:  ");
        sc_no_slaves = new Scanner(System.in);
        no_slaves = sc_no_slaves.nextInt();
        
        System.out.print("\tIngrese la capacidad de los dispensadores:  ");
        sc_capacity_dispenser = new Scanner(System.in);
        capacity_dispenser = sc_capacity_dispenser.nextInt();
        
        System.out.print("\tIngrese el nombre de la cola:  ");
        sc_destination_queue = new Scanner(System.in);
        name_queue = sc_destination_queue.nextLine();
        
        final Slave_Dispenser messageSender = new Slave_Dispenser();
        messageSender.sendMessages();
    }   
}