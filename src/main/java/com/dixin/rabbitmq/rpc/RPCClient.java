package com.dixin.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.util.UUID;

/**
 * Created by dixin on 17/1/9.
 */
public class RPCClient {

    private Connection       connection;
    private Channel          channel;
    private String           requestQueueName = "rpc_queue";
    private String           replyQueueName;
    private QueueingConsumer consumer;

    public RPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
    }

    public String call(String message) throws Exception {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId)
            .replyTo(replyQueueName).build();

        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody(), "UTF-8");
                break;
            }
        }
        return response;
    }

    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] args) {
        RPCClient rpcClient = null;
        String response = null;
        try {
            rpcClient = new RPCClient();

            System.out.println("RPCClient [x] Requesting fib(30)");
            response = rpcClient.call("30");
            System.out.println("RPCClient [.] Got '" + response + "'");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rpcClient != null) {
                try {
                    rpcClient.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
