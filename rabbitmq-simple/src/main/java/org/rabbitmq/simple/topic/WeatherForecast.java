package org.rabbitmq.simple.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.rabbitmq.simple.utils.RabbitConstant;
import org.rabbitmq.simple.utils.RabbitUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * topic 主题模式
 * Topic 类型与 Direct 相比，都是可以根据 RoutingKey 把消息路由到不同的队列。只不过 Topic 类型Exchange 可以让队列在绑定 Routing key 的时候使用通配符！
 * Routingkey 一般都是有一个或多个单词组成，多个单词之间以”.”分割，例如： item.insert
 * 通配符规则：# 匹配一个或多个词，* 匹配不多不少恰好1个词，例如：item.# 能够匹配 item.insert.abc 或者 item.insert，item.* 只能匹配 item.insert
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
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("china.zhejiang.hangzhou.20201201", "中国浙江杭州20201201的天气数据");
        map.put("china.zhejiang.hangzhou.20201202", "中国浙江杭州20201202的天气数据");
        map.put("china.jiangsu.suzhou.20201201", "中国江苏苏州20201201的天气数据");
        map.put("us.cal.lsj.20201201", "美国加州洛杉矶20201201的天气数据");


        final Connection connection = RabbitUtils.getConnection();
        final Channel channel = connection.createChannel();

        final Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> next = iterator.next();
            //四个参数
            //exchange 交换机
            //队列名称
            //额外的设置属性
            //最后一个参数是要传递的消息字节数组
            //  TODO 消费者消费的时候必须手动先创建交换机
            channel.basicPublish(RabbitConstant.EXCHANGE_WEATHER_TOPIC, next.getKey(), null, next.getValue().getBytes());
        }
        channel.close();
        connection.close();
    }
}
