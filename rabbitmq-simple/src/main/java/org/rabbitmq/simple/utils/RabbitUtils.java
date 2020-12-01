package org.rabbitmq.simple.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author ggq
 * @Date 2020/11/30 16:24
 */
public class RabbitUtils {
    private static ConnectionFactory connectionFactory = new ConnectionFactory();

    static {
        connectionFactory.setHost("192.168.56.123");
        //5672是RabbitMQ的默认端口号
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/test");
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = connectionFactory.newConnection();
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
