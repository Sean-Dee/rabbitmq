package com.dixin.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created by dixin on 17/1/9.
 */
public class TopicSend {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) {

        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            String[] routingKeys = new String[] { "quick.orange.rabbit", "lazy.orange.elephant",
                                                  "quick.orange.fox", "lazy.brown.fox",
                                                  "quick.brown.fox", "quick.orange.male.rabbit",
                                                  "lazy.orange.male.rabbit" };

            for (String key : routingKeys) {
                String message = "From " + key + " routingKey's message";
                channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes());
                System.out.println("TopicSend [x] sent '" + key + "':'" + message + "'");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}
