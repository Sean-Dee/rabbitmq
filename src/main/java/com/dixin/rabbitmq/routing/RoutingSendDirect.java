package com.dixin.rabbitmq.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by dixin on 17/1/7.
 */
public class RoutingSendDirect {

    private static final String   EXCHANGE_NAME = "direct_logs";
    // 路由关键字
    private static final String[] routingKeys   = new String[] { "info", "warning", "error" };

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 发送消息
        for (String key : routingKeys) {
            String message = "Send the message level: " + key;
            channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes());
            System.out.println(" [*] send '" + key + "':'" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
