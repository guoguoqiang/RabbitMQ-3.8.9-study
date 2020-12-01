package org.rabbitmq.simple.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.rabbitmq.simple.utils.RabbitConstant;
import org.rabbitmq.simple.utils.RabbitUtils;

import java.util.Scanner;

/**
 * 发布订阅模式 因为所有消费者获得相同的消息，所以特别适合  数据提供商与应用商
 * <p>
 * 模拟场景：
 * 中国气象局提供 天气预报 送入交换机，网易、新浪、百度、搜狐等门户网站接入通过 队列绑定到该交换机，
 * 自动获取气象局数据。
 * <p>
 * 生产者：天气预报（气象局）
 *
 * @Author ggq
 * @Date 2020/12/1 10:39
 */
public class WeatherForecast {

    public static void main(String[] args) throws Exception {
        final Connection connection = RabbitUtils.getConnection();
        final String input = new Scanner(System.in).next();
        final Channel channel = connection.createChannel();
        //四个参数
        //exchange 交换机
        //队列名称
        //额外的设置属性
        //最后一个参数是要传递的消息字节数组
        channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER, "", null, input.getBytes());
        channel.close();
        connection.close();
    }
}
