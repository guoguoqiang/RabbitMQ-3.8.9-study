package org.rabbitmq.simple.routing;

import com.rabbitmq.client.*;
import org.rabbitmq.simple.utils.RabbitConstant;
import org.rabbitmq.simple.utils.RabbitUtils;

import java.io.IOException;


/**
 * 消费者：
 *
 * @Author ggq
 * @Date 2020/12/1 11:06
 */
public class Sina {

    public static void main(String[] args) throws Exception {
        // 获取TCP长连接
        final Connection connection = RabbitUtils.getConnection();
        // 获取虚拟连接
        final Channel channel = connection.createChannel();

        // 声明队列的信息

        //创建队列,声明并创建一个队列，如果队列已存在，则使用这个队列
        //第一个参数：队列名称ID
        //第二个参数：是否持久化，false对应不持久化数据，MQ停掉数据就会丢失
        //第三个参数：是否队列私有化，false则代表所有消费者都可以访问，true代表只有第一次拥有它的消费者才能一直使用，其他消费者不让访问
        //第四个：是否自动删除,false代表连接停掉后不自动删除掉这个队列
        //其他额外的参数, null
        channel.queueDeclare(RabbitConstant.QUEUE_SINA, false, false, false, null);

        // 队列绑定交换机
        // 参数1：队列名 参数2：交换机名 参数3：路由key
        channel.queueBind(RabbitConstant.QUEUE_SINA, RabbitConstant.EXCHANGE_WEATHER_ROUTING, "china.zhejiang.hangzhou.20201201");

        //如果不写basicQos（1），则自动MQ会将所有请求平均发送给所有消费者
        //basicQos,MQ不再对消费者一次发送多个请求，而是消费者处理完一个消息后（确认后），在从队列中获取一个新的
        //处理完一个取一个
        channel.basicQos(1);

        channel.basicConsume(RabbitConstant.QUEUE_SINA, false, new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("新浪天气收到气象信息：" + new String(body));

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
