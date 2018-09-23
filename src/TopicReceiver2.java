import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class TopicReceiver2 {
    static MessageProducer response;
    public static void main(String[] args) {

        //connectionFactory 连接工厂，JMS用它创建连接
        ConnectionFactory connectionFactory;

        //connection JMS客户端到JMS provider 的连接
        Connection connection = null;

        //session一个发送或者接收的线程
        final Session session;

        //destination 消息目的地，发送给谁接收 这里注意改成Topic类型的
        Topic destination;

        //消费者消息接收者
        final MessageConsumer consumer;

        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616");

        try {
            //构造工厂得到连接对象
            connection = connectionFactory.createConnection();
            connection.setClientID("yui");

            //启动
            connection.start();

            //获取操作连接
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

            //此处使用的是Topic模式
            destination = session.createTopic("que");
            TopicSubscriber subs=session.createDurableSubscriber(destination,"yui");
            subs.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    TextMessage tm1 = (TextMessage) message;
                    try {
                        response = session.createProducer(tm1.getJMSReplyTo());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    try {
                        TextMessage resM = session.createTextMessage();
                        resM.setText("surprise father fucker");
                        resM.setJMSCorrelationID(tm1.getJMSCorrelationID());
                        System.out.println("Received message2: " + tm1.getText());
                        response.send(tm1.getJMSReplyTo(),resM);
                        session.commit();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
