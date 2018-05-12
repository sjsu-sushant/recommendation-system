import com.google.gson.Gson;
import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class RecommendationSubscriber {

    private static RecommendationSubscriber recommendationSubscriber=null;

    private RecommendationSubscriber(){}

    public static RecommendationSubscriber getInstance(){
        if(recommendationSubscriber==null)
            recommendationSubscriber=new RecommendationSubscriber();
        return recommendationSubscriber;
    }

    static JSONObject user=null;
    static String getItemBasedRecommendations_queue = "getItemBasedRecommendations_queue";
    static String getUserBasedRecommendations_queue = "getUserBasedRecommendations_queue";
    static String setUserData_queue = "setUserData_queue";


    private static void allConsumers() throws Exception{
        ConnectionFactory factory =  new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection =  factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(getItemBasedRecommendations_queue,false,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();

                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                Map user = new Gson().fromJson(message, Map.class);
                String userId = user.get("userId").toString();
                String numberOfRecommendation= user.get("numberOfRecommendation").toString();

                System.out.println("===user id = "+userId+"  numberOfRecommendation= "+numberOfRecommendation);
                String output="";
                try {
                    output = ItemBasedRecommendation.getInstance().getItemBasedRecommendations(userId, numberOfRecommendation);
                }catch (Exception e){e.printStackTrace();}
                channel.basicPublish( "", properties.getReplyTo(), replyProps, output.getBytes("UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);

                synchronized(this) {
                    this.notify();
                }
                try {
                    RecommendationSubscriber.getInstance().allConsumers();
                }catch(Exception e){e.printStackTrace();}
                //return ItemBasedRecommendation.getInstance().getItemBasedRecommendations(userId,numberOfRecommendation).toString();

            }
        };
        channel.basicConsume(getItemBasedRecommendations_queue, true, consumer);


        channel.queueDeclare(getUserBasedRecommendations_queue,false,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer1 = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();

                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                Map user = new Gson().fromJson(message, Map.class);
                String userId = user.get("userId").toString();
                String numberOfRecommendation= user.get("numberOfRecommendation").toString();

                System.out.println("===user id = "+userId+"  numberOfRecommendation= "+numberOfRecommendation);
                String output="";
                try {
                    output = UserBasedRecommendation.getInstance().getUserBasedRecommendations(userId, numberOfRecommendation);
                }catch (Exception e){e.printStackTrace();}
                channel.basicPublish( "", properties.getReplyTo(), replyProps, output.getBytes("UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);

                synchronized(this) {
                    this.notify();
                }
                try {
                    RecommendationSubscriber.getInstance().allConsumers();
                }catch(Exception e){e.printStackTrace();}
                //return ItemBasedRecommendation.getInstance().getItemBasedRecommendations(userId,numberOfRecommendation).toString();

            }
        };
        channel.basicConsume(getUserBasedRecommendations_queue, true, consumer1);


        channel.queueDeclare(setUserData_queue,false,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        Consumer consumer2 = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();

                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                Map user = new Gson().fromJson(message, Map.class);
                String userId = user.get("userId").toString();
                String itemId= user.get("itemId").toString();
                String ratings= user.get("ratings").toString();

                System.out.println("===user id = "+userId+"  itemId= "+itemId+ "  ratings= "+ratings);

                String output="Success";
                try {
                    DataLoadBridge.getInstance().persistPerson(new UserItemModel(userId,itemId,ratings));
                    ItemBasedRecommendation.getInstance().setupProcess();
                    UserBasedRecommendation.getInstance().setupProcess();
                }catch (Exception e){e.printStackTrace();}
                channel.basicPublish( "", properties.getReplyTo(), replyProps, output.getBytes("UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);

                synchronized(this) {
                    this.notify();
                }
                try {
                    RecommendationSubscriber.getInstance().allConsumers();
                }catch(Exception e){e.printStackTrace();}
            }
        };
        channel.basicConsume(setUserData_queue, true, consumer2);
    }

    public static void main(String[] args) throws Exception {
        RecommendationSubscriber.getInstance().setup();
        RecommendationSubscriber.getInstance().allConsumers();
    }

    public void setup() throws Exception{
        ItemBasedRecommendation.getInstance().setupProcess();
        UserBasedRecommendation.getInstance().setupProcess();
    }
}
