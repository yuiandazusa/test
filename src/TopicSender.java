import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TopicSender {
    public static void main(String[] args) {

        //ConnectionFactory是连接工厂，JMS用它创建连接
        ConnectionFactory connectionFactory;

        //Connection JMS客户端到JMS provider的连接
        Connection connection = null;

        //Session 一个发送或者接收消息的线程
        Session session;

        //Destination 消息发送目的地，消息发送给谁接收
        Topic destination;

        //MessageProducer 消息发送者
        MessageProducer messageProducer;

        //构造ConnectionFactory 实例对象，此处采用ActiveMQ的实现jar
        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616");

        try {
            //构造工厂得到连接对象
            connection = connectionFactory.createConnection();

            //启动
            connection.start();

            //获取操作连接
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            //创建一个Queue，SecondQueue 此处使用的是Topic模式
            destination = session.createTopic("que");
            //创建一个临时的主题目的地，用于存放响应的消息
            Destination destination1 = session.createTemporaryQueue();

            //得到消息生产者【发送者】
            messageProducer = session.createProducer(destination);
            //创建消息消费者，用于消费响应
            MessageConsumer consumer = session.createConsumer(destination1);

            //设置持久化，根据实际情况而定
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

            //创建一个消息对象
            TextMessage message = session.createTextMessage();
            //设置消息选择条件
            message.setIntProperty("num",10);

            //把我们的消息写入msg对象中
            BufferedReader b=new BufferedReader(new InputStreamReader(System.in));
            while (true){
                System.out.println("请输入信息：");
                String s = b.readLine();
                if("end".equals(s))
                    break;
                message.setText(s);
                message.setJMSReplyTo(destination1);
                String correlationId ="yuiazu";
                message.setJMSCorrelationID(correlationId);
                messageProducer.send(message);
                System.out.println("Message successfully sent.");
            }

            //发送消息

            //session.commit();
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage tm = (TextMessage) message;
                    try {
                        System.out.println("response message:"+tm.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }/*finally {
            try {
                if(null != connection){
                    connection.close();
                }
            } catch (Throwable ignore) {
            }
        }*/
    }
}
