import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class RecommendationPublisher {

    private final static String getItemBasedRecommendations_queue = "getItemBasedRecommendations_queue";

    public static void main(String[] argv)
            throws Exception{
/*

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(getItemBasedRecommendations_queue, false, false, false, null);

        String msg = "From java Publisher";
        channel.basicPublish("",getItemBasedRecommendations_queue,null,msg.getBytes("UTF-8"));
        System.out.println("Sent msg = "+msg);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
*/


        /*channel.close();
        connection.close();*/
    }
}
