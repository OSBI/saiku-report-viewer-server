package org.saiku.reportviewer.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.saiku.reportviewer.server.api.ReportServerImpl;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public class Main {
  private final static String QUEUE_NAME = "SAIKU";
  
  private static Main instance;
  private static ReportServerImpl server;
  
  private Main() {
    server = new ReportServerImpl();
    server.init();
  }
  
  public static Main getInstance() {
    if (instance == null) {
      instance = new Main();
    }
    
    return instance;
  }

  public byte[] processReport() throws Exception {
    File temp = File.createTempFile("saiku", ".pdf");
    server.processReport(temp, "test", "pdf", new org.saiku.reportviewer.server.util.MockUriInfo());
    
    byte[] buffer = new byte[(int)temp.length()];
    
    try {
      FileInputStream fis = new FileInputStream(temp);
      fis.read(buffer);
      fis.close();
    } catch (Exception ex) {
      System.err.println("Error converting the report to PDF: " + ex.getMessage());
    }
    
    return buffer;
  }

  public static void main(String[] args) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    Connection connection = null;
    try {
      connection = factory.newConnection();
      final Channel channel = connection.createChannel();

      channel.queueDeclare(QUEUE_NAME, false, false, false, null);

      channel.basicQos(1);

      System.out.println(" [x] Saiku Ready - Awaiting RPC requests");

      Consumer consumer = new DefaultConsumer(channel) {
        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
          AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(properties.getCorrelationId()).build();
          
          try {
            channel.basicPublish("", properties.getReplyTo(), replyProps, Main.getInstance().processReport());
          } catch (Exception ex) {
            System.err.println("Error sending the report bytes through queue: " + ex.getMessage());
          }
          
          channel.basicAck(envelope.getDeliveryTag(), false);
          
          // RabbitMq consumer worker thread notifies the RPC server owner thread
          synchronized (this) {
            this.notify();
          }
        }
      };

      channel.basicConsume(QUEUE_NAME, false, consumer);
      
      // Wait and be prepared to consume the message from RPC client.
      while (true) {
        synchronized (consumer) {
          try {
            consumer.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (IOException _ignore) {}
      }
    }
  } // End main method
} // End Main class
