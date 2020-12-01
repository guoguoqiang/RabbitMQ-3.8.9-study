# 1、MQ的基本概念
## MQ概述
`MQ`全称 **M**essage **Q**ueue（消息队列），是在消息的传输过程中保存消息的容器。多用于分布式系统之间进行通信。

<img src="https://note.youdao.com/yws/api/personal/file/C75AE581F30B48CD8CD15D5779909792?method=download&shareKey=dfdb276695886b4817fd711a90590d47" width = "700" />

## MQ的优势

### 1、应用解耦

**传统系统服务之间的交互**

<img src="https://note.youdao.com/yws/api/personal/file/88F22B31C7684969B7A698C08A0A6004?method=download&shareKey=02f33225bae891a0653987dd0d417b86" width="700">

系统的耦合性高，容错性就低，可维护性就越低。



**加入MQ**

<img src="https://note.youdao.com/yws/api/personal/file/D2F7562BD25A4133953F8EDF26915A04?method=download&shareKey=d65e53bbacb6b6fe71aab002adc1ec1c" width="700">

MQ使得应用间解耦，提升容错性和可维护性

### 2、异步提速

从上图可以看出传统服务之间如果有多个应用交互，需要等待100ms,而加入MQ之后只需要10ms，速度提升很多

提升用户体验和系统吞吐量（单位时间内处理请求的数目）

### 3、削峰填谷

**传统服务**

<img src="https://note.youdao.com/yws/api/personal/file/B3D57D6A4676496F9121D9ADAB3C0D3D?method=download&shareKey=3c3682194852d9650eb576be1ecf3f78" >



由于A系统最大每秒处理请求1000个，而系统瞬间5000个请求直接导致系统挂掉



**加入MQ**

<img src="https://note.youdao.com/yws/api/personal/file/08A7460B118B48A5B4C33A5C933D26B7?method=download&shareKey=1c13f4e57efecb29864cf77aae7bcb3a">



<img src="https://note.youdao.com/yws/api/personal/file/F3A7FA8B0F194BBF8302379DB422AE6D?method=download&shareKey=38b0cb7d3317340162098bb850f6b45b">

使用了 MQ 之后，限制消费消息的速度为1000，这样一来，高峰期产生的数据势必会被积压在 MQ 中，高峰就被“削”掉了，但是因为消息积压，在高峰期过后的一段时间内，消费消息的速度还是会维持在1000，直到消费完积压的消息，这就叫做“填谷”。

使用MQ后，可以提高系统稳定性。

## MQ的劣势

### 系统的可用性降低

系统引入的外部依赖越多，系统稳定性越差。一旦 MQ 宕机，就会对业务造成影响。如何保证MQ的高可用？

### 系统的复杂度提高

MQ 的加入大大增加了系统的复杂度，以前系统间是同步的远程调用，现在是通过 MQ 进行异步调用。如何保证消息不被丢失等情况？



# 2、RabbitMQ简介

AMQP，即 Advanced Message Queuing Protocol（高级消息队列协议），是一个网络协议，是应用层协议的一个开放标准，为面向消息的中间件设计。基于此协议的客户端与消息中间件可传递消息，并不受客户端/中间件不同产品，不同的开发语言等条件的限制。2006年，AMQP 规范发布。类比HTTP。

<img src="https://note.youdao.com/yws/api/personal/file/FFEADEBB6D524C14A3F09E21F00E5D19?method=download&shareKey=248922f9f3a14debca6ea3a54f82436c">

2007年，Rabbit 技术公司基于 AMQP 标准开发的 RabbitMQ 1.0 发布。RabbitMQ 采用 Erlang 语言开发。Erlang 语言由 Ericson 设计，专门为开发高并发和分布式系统的一种语言，在电信领域使用广泛。

<img src="https://note.youdao.com/yws/api/personal/file/53D4E7BF14134D479021CF2917AC56C1?method=download&shareKey=9170ef0d61d27edab8f35495a6bdf79f">

### RabbitMQ中的基本概念

- Broker：接收和分发消息的应用，RabbitMQ Server就是 Message Broker
- Virtual host：出于多租户和安全因素设计的，把 AMQP 的基本组件划分到一个虚拟的分组中，类似于网络中的 namespace 概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出多个vhost，每个用户在自己的 vhost 创建 exchange／queue 等
- Connection：publisher／consumer 和 broker 之间的 TCP 连接
- Channel：如果每一次访问 RabbitMQ 都建立一个 Connection，在消息量大的时候建立 TCP Connection的开销将是巨大的，效率也较低。Channel 是在 connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个thread创建单独的 channel 进行通讯，AMQP method 包含了channel id 帮助客户端和message broker 识别 channel，所以 channel 之间是完全隔离的。Channel 作为轻量级的 Connection 极大减少了操作系统建立 TCP connection 的开销
- Exchange：message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发消息到queue 中去。常用的类型有：direct (point-to-point), topic (publish-subscribe) and fanout (multicast)
- Queue：消息最终被送到这里等待 consumer 取走
- Binding：exchange 和 queue 之间的虚拟连接，binding 中可以包含 routing key。Binding 信息被保存到 exchange 中的查询表中，用于 message 的分发依据

### RabbitMQ提供的7种工作模式

RabbitMQ 提供了 7 种工作模式：简单模式、work queues、Publish/Subscribe 发布与订阅模式、Routing 路由模式、Topics 主题模式、RPC 远程调用模式（远程调用，不太算 MQ；暂不作介绍）、Publicsher Confirms。

官网对应模式介绍：https://www.rabbitmq.com/getstarted.html

<img src="https://note.youdao.com/yws/api/personal/file/EFC6327388B04FF0AB9328153176E080?method=download&shareKey=7baf2abe8910f158268ca86bd6386840">

#### 1、简单模式

<img src="https://note.youdao.com/yws/api/personal/file/6464BC1ACC4948688FFF4E2812218FA9?method=download&shareKey=28eb3a25b78448f48dcbfc095d9bda2d">

在上图的模型中，有以下概念：

- P：生产者，也就是要发送消息的程序
- C：消费者：消息的接收者，会一直等待消息到来
- queue：消息队列，图中红色部分。类似一个邮箱，可以缓存消息；生产者向其中投递消息，消费者从其中取出消息

#### 2、Work queues 工作队列模式

<img src="https://note.youdao.com/yws/api/personal/file/4A5A1656D0434D04B1339F909CE5A37C?method=download&shareKey=53d60a3318d291d47f1bd3a2de4cfe46">

- **Work Queues：**与入门程序的简单模式相比，多了一个或一些消费端，多个消费端共同消费同一个队列中的消息。
- **应用场景**：对于任务过重或任务较多情况使用工作队列可以提高任务处理的速度。

#### 3、Pub/Sub 订阅模式

<img src="https://note.youdao.com/yws/api/personal/file/C08CE7A7616E4309937898F8410D1AEC?method=download&shareKey=c8823605e30b386aca8b27d4ba64d2e3">

在订阅模型中，多了一个 Exchange 角色，而且过程略有变化：

- P：生产者，也就是要发送消息的程序，但是不再发送到队列中，而是发给X（交换机）

- C：消费者，消息的接收者，会一直等待消息到来

- Queue：消息队列，接收消息、缓存消息

- Exchange：交换机（X）。一方面，接收生产者发送的消息。另一方面，知道如何处理消息，例如递交给某个特别队列、递交给所有队列、或是将消息丢弃。到底如何操作，取决于Exchange的类型。Exchange有常见以下3种类型：

  Fanout：广播，将消息交给所有绑定到交换机的队列

  Direct：定向，把消息交给符合指定routing key 的队列

  Topic：通配符，把消息交给符合routing pattern（路由模式） 的队列

  **Exchange**（交换机）只负责转发消息，不具备存储消息的能力，因此如果没有任何队列与 Exchange 绑定，或者没有符合路由规则的队列，那么消息会丢失！



#### 4、Routing路由模式

<img src="https://note.youdao.com/yws/api/personal/file/1C68ECDE139D4FD494192AC6B43A3F84?method=download&shareKey=c4b00f5102905a8ff7ef6cf83445cca7">

- 队列与交换机的绑定，不能是任意绑定了，而是要指定一个 RoutingKey（路由key）

- 消息的发送方在向 Exchange 发送消息时，也必须指定消息的 RoutingKey

- Exchange 不再把消息交给每一个绑定的队列，而是根据消息的 Routing Key 进行判断，只有队列的Routingkey 与消息的 Routing key 完全一致，才会接收到消息

  

- P：生产者，向 Exchange 发送消息，发送消息时，会指定一个routing key
- X：Exchange（交换机），接收生产者的消息，然后把消息递交给与 routing key 完全匹配的队列
- C1：消费者，其所在队列指定了需要 routing key 为 error 的消息
- C2：消费者，其所在队列指定了需要 routing key 为 info、error、warning 的消息

#### 5、Topics 主题模式

<img src="https://note.youdao.com/yws/api/personal/file/B829800613644F7CB964A8BB2D9B4520?method=download&shareKey=cdde3292040da340f46a63b199f93a55">



- Topic 类型与 Direct 相比，都是可以根据 RoutingKey 把消息路由到不同的队列。只不过 Topic 类型Exchange 可以让队列在绑定 Routing key 的时候使用**通配符**！
- Routingkey 一般都是有一个或多个单词组成，多个单词之间以”.”分割，例如： item.insert
- 通配符规则：# 匹配一个或多个词，* 匹配不多不少恰好1个词，例如：item.# 能够匹配 item.insert.abc 或者 item.insert，item.* 只能匹配 item.insert



# 3、RabbitMQ的安装与配置

**基于centos7.7 安装**

官网：http://www.rabbitmq.com/

1、点击`Get Started`  

<img src="https://note.youdao.com/yws/api/personal/file/D3FFB7790DA2424B9267F44259D185BF?method=download&shareKey=b5355e907be006f64085d6a16861d231">

2、点击`Download+Installation`

<img src="https://note.youdao.com/yws/api/personal/file/BB5B8324E9394D0EAC1694D566BB4CC5?method=download&shareKey=38b20097c7c0e54ca1093bb95df5222c">

3、下载

<img src="https://note.youdao.com/yws/api/personal/file/9441314EDD4147E9A358EA4CE92DAF5D?method=download&shareKey=e23ecdac997922724dc3fad88637d857">

rabbitmq 最新版本`rabbitmq-server-3.8.9-1.el7.noarch.rpm`



查看erlang对应的版本

https://www.rabbitmq.com/which-erlang.html



erlang 版本`erlang-23.1.4-1.el7.x86_64.rpm`

https://bintray.com/rabbitmq-erlang/rpm/erlang



socat 下载 

http://repo.iotti.biz/CentOS/7/x86_64/socat-1.7.3.2-5.el7.lux.x86_64.rpm



4、上传centos7.7 服务器

<img src="https://note.youdao.com/yws/api/personal/file/1505F33E8B214017B43F79922662ED48?method=download&shareKey=8c320ba0e32c68aa62a192f2474e9018">



5、安装

如果已经安装了老版本

卸载

链接：https://www.cnblogs.com/momo-88/p/13588736.html

```
# 卸载erlang
yum list | grep erlang
yum remove erlang-erts-R16B-03.18.el7.x86_64（具体已经存在的版本）
rm -rf /usr/lib64/erlang

# 卸载RabbitMQ
yum list | grep rabbitmq
yum -y remove rabbitmq-server.noarch
```

安装erlang

rpm -ivh erlang-23.1.4-1.el7.x86_64.rpm 

安装socat

rpm -ivh socat-1.7.3.2-5.el7.lux.x86_64.rpm

安装rabbitmq-server-3.8.9-1.el7.noarch.rpm

rpm -ivh rabbitmq-server-3.8.9-1.el7.noarch.rpm

<img src="https://note.youdao.com/yws/api/personal/file/DD63BB65396847CA8F32A96841427A6E?method=download&shareKey=3a89dc29fe8f561d4463c75e8f8c523d">

# 4、开启管理界面配置

开启管理界面

rabbitmq-plugins enable rabbitmq_management



<img src= "https://note.youdao.com/yws/api/personal/file/A4E7EAA53D0340C6BCE36A9282372C9C?method=download&shareKey=5c274532c0c50fc04791cc5f1b6bf534">

**创建用户**

rabbitmqctl add_user admin admin

设置admin为超级管理员

rabbitmqctl set_user_tags admin administrator

授权远程访问（也可以登录后，可视化配置）

rabbitmqctl set_permissions -p / admin "." "." ".*"

创建完成后，重启RabbitMQ

systemctl restart rabbitmq-server



关闭防火墙

systemctl stop firewalld





# 5、启动RabbitMQ服务

启动

service rabbitmq-server start

停止

service rabbitmq-server stop

重启

service rabbitmq-server restart



登录页面

<img src="https://note.youdao.com/yws/api/personal/file/1E40462BFAC449BFA9FBF5277BE0A7D0?method=download&shareKey=3d575a08206f6430d6af0c7a529473f4">



# 6、配置虚拟主机及用户

## 角色说明：

 1、 超级管理员(administrator) 可登陆管理控制台，可查看所有的信息，并且可以对用户，策略(policy)进行操 作。

 2、 监控者(monitoring) 可登陆管理控制台，同时可以查看rabbitmq节点的相关信息(进程数，内存使用 情况，磁盘使用情况等) 

3、 策略制定者(policymaker) 可登陆管理控制台, 同时可以对policy进行管理。但无法查看节点的相关信息(上 图红框标识的部分)。 

4、 普通管理者(management) 仅可登陆管理控制台，无法看到节点信息，也无法对策略进行管理。 

5、 其他 无法登陆管理控制台，通常就是普通的生产者和消费者。



## Virtual Hosts配置

像mysql拥有数据库的概念并且可以指定用户对库和表等操作的权限。 RabbitMQ也有类似的权限管理；在RabbitMQ中可以虚拟消息服务器Virtual Host，每个Virtual Hosts相当于一个相对独立的RabbitMQ服务器，每个 VirtualHost之间是相互隔离的。exchange、queue、message不能互通。 相当 于mysql的db。Virtual Name一般以/开头。

**创建virtual hosts**

<img src="https://note.youdao.com/yws/api/personal/file/41F43A012670488683DCB154E72322B6?method=download&shareKey=32d0713ec514cf6d52c39448133a9961">

1、选择admin

2、选择virtual hosts

3、Add virtual hosts



**设置virtual hosts权限**

<img src="https://note.youdao.com/yws/api/personal/file/F103B5CD32AC45C7A4D781CD80D99461?method=download&shareKey=05a844374359ab8e7e62f445075deedf">

1、点击/test

2、选择 admin 用户

3、点击 set permission

