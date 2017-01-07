package com.dixin.rabbitmq.routing;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by dixin on 17/1/7.
 */
public class ReceiveLogsDirect2 {

    private static final String   EXCHANGE_NAME = "direct_logs";
    private static final String[] routingKeys   = new String[] { "error" };

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = channel.queueDeclare().getQueue();

        for (String key : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, key);
            System.out.println("ReceiveLogsDirect2 exchange:" + EXCHANGE_NAME + ", queue:"
                               + queueName + ", BindRoutingKey:" + key);
        }
        System.out.println("ReceiveLogsDirect2 [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out
                    .println(" [*] received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
