package org.example.assignment2skierserver;/*
This class code is provided by Ian as the examples supplement
Chapter 7 of the Foundations of Scalable Systems, O'Reilly Media 2022
Modified by Hyojin for assignment 2
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMQChannelPool {
    private final BlockingQueue<Channel> pool;
    // fixed size pool
    private int capacity;
    private final Connection connection;

    public RMQChannelPool(int maxSize, String host, int port, String username, String password, String virtualHost
                          ) throws IOException, TimeoutException {
        // initializes the RabbitMQ connection and creates a channel pool
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);

        this.capacity = maxSize;
        this.connection = factory.newConnection();
        pool = new LinkedBlockingQueue<>(capacity);

        for (int i = 0; i < capacity; i++) {
            Channel chan;
            try {
                chan = connection.createChannel();
                pool.put(chan);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(RMQChannelPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Channel borrowObject() throws IOException {
        // get a channel from rabbitmq
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error: no channels available" + e.toString());
        }
    }

    public void returnObject(Channel channel) throws Exception {
        //return a channel back to rabbitmq
        if (channel != null) {
            pool.add(channel);
        }
    }

    public void close() throws IOException, TimeoutException {
        connection.close();
    }
}