package com.dixin.rabbitmq.queues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * Created by dixin on 17/1/6.
 */
public class NewTask {

    private static final String TASK_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_NAME, true, false, false, null);
        // 分发消息
        for (int i = 0; i < 5; i++) {
            String message = "Hello World! " + i;
            channel.basicPublish("", TASK_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
