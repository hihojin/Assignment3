package org.example.assignment2skierserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkiersConsumer {
    private final ConcurrentHashMap<String, String> liftRidesMap;
    private static final int THREADS = 5;


    public SkiersConsumer() {
        this.liftRidesMap = new ConcurrentHashMap<>();
    }

    public void receive() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("35.167.23.169"); // change on restarting ec2
            factory.setUsername("admin");
            factory.setPassword("password");
            factory.setVirtualHost("/");
            factory.setPort(5672);

            Connection connection = factory.newConnection();

            ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

            for (int i = 0; i < THREADS; i++) {
                Channel channel = connection.createChannel();
                String queueName = "skiersQueue";

                channel.queueDeclare(queueName, false, false, false, null);
                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
                channel.basicQos(1);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), "UTF-8");
                    processMessage(message);
                    // acknowledge the message
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                };

                channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});

                // Submit each channel to the ExecutorService
                executorService.submit(() -> {
                    try {
                        while (true) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String message) {
        // parse json here
         JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

         String skierID = jsonObject.get("skierID").getAsString();

         StringBuilder dataBuilder = new StringBuilder();
         dataBuilder.append("{")
                    .append("\"resortID\":").append(jsonObject.get("resortID")).append(",")
                    .append("\"seasonID\":").append(jsonObject.get("seasonID")).append(",")
                    .append("\"dayID\":").append(jsonObject.get("dayID")).append(",")
                    .append("\"time\":").append(jsonObject.get("time")).append(",")
                    .append("\"liftID\":").append(jsonObject.get("liftID"))
                    .append("}");

        String data = dataBuilder.toString();
        // replace or put condition if else add here
        this.liftRidesMap.put(skierID, data);

        // Print the updated map for demonstration purposes
        System.out.println("Updated lift rides map: " + this.liftRidesMap);
    }

    public static void main(String[] args) {
        new SkiersConsumer().receive();
    }
}

