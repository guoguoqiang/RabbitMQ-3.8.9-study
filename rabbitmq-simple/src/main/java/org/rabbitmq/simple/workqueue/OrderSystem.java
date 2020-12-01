package org.rabbitmq.simple.workqueue;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.rabbitmq.simple.utils.RabbitConstant;
import org.rabbitmq.simple.utils.RabbitUtils;


/**
 * 工作队列模式：
 * Work Queues：与入门程序的简单模式相比，多了一个或一些消费端，多个消费端共同消费同一个队列中的消息。
 * 应用场景：对于任务过重或任务较多情况使用工作队列可以提高任务处理的速度。
 *
 * @Author ggq
 * @Date 2020/11/30 17:08
 */
public class OrderSystem {
    public static void main(String[] args) throws Exception {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RabbitConstant.QUEUE_SMS, false, false, false, null);

        for (int i = 1; i <= 100; i++) {
            SMS sms = new SMS("乘客" + i, "13900000" + i, "您的车票已预订成功");
            String jsonSMS = new Gson().toJson(sms);
            channel.basicPublish("", RabbitConstant.QUEUE_SMS, null, jsonSMS.getBytes());
        }
        System.out.println("发送数据成功");
        channel.close();
        connection.close();
    }
}
