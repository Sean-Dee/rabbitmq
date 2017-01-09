package com.dixin.rabbitmq.rpc;

import com.rabbitmq.client.*;

/**
 * Created by dixin on 17/1/9.
 */
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.basicQos(1);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

            System.out.println("RPCServer [x] awating RPC request.");

            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                AMQP.BasicProperties properties = delivery.getProperties();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(properties.getCorrelationId()).build();

                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);

                    System.out.println("RPCServer [.] fib(" + message + ")");
                    response = "" + fib(n);
                } catch (Exception e) {
                    System.out.println(" [.] " + e.toString());
                    response = "";
                } finally {
                    channel.basicPublish("", properties.getReplyTo(), replyProps,
                        response.getBytes("UTF-8"));

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
