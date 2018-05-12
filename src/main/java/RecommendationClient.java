import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@EnableAutoConfiguration
public class RecommendationClient {
    private static RecommendationClient recommendationClient = null;

    ItemBasedRecommendation itemBasedRecommender = null;
    UserBasedRecommendation userBasedRecommender = null;
    DataLoadBridge dataLoadBridge= null;

    private RecommendationClient() throws Exception {
    }

    public static RecommendationClient  getInstance()throws Exception{
        if(recommendationClient == null)
            recommendationClient=  new RecommendationClient();

        return recommendationClient;
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    // http://localhost:8090/getItemBasedRecommendations?userId=200&numberOfRecommendation=6
    /*@RequestMapping("/getItemBasedRecommendations")
    @ResponseBody
    ResponseEntity<Object> getItemBasedRecommendations(@RequestParam(value="userId", defaultValue="200") String userId, @RequestParam(value="numberOfRecommendation", defaultValue="5") String numberOfRecommendation )throws Exception{
       return ItemBasedRecommendation.getInstance().getItemBasedRecommendations(userId,numberOfRecommendation);
    }*/

    // http://localhost:8090/getUserBasedRecommendations?userId=200&numberOfRecommendation=6
    /*@RequestMapping("/getUserBasedRecommendations")
    @ResponseBody
    ResponseEntity<Object> getUserBasedRecommendations(@RequestParam(value="userId", defaultValue="200") String userId, @RequestParam(value="numberOfRecommendation", defaultValue="5") String numberOfRecommendation )throws Exception{
        return UserBasedRecommendation.getInstance().getUserBasedRecommendations(userId,numberOfRecommendation);
    }*/

    // http://localhost:8090/updateUserData     body: {"userId": "200","itemId": "9","ratings": "5"}
    /*@RequestMapping(value = "/updateUserData", method = RequestMethod.POST)
    public ResponseEntity< String > persistUserData(@RequestBody UserItemModel user) throws Exception {
        DataLoadBridge.getInstance().persistPerson(user);
        ItemBasedRecommendation.getInstance().setupProcess();
        UserBasedRecommendation.getInstance().setupProcess();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/

    public void setup() throws Exception{
        ItemBasedRecommendation.getInstance().setupProcess();
        UserBasedRecommendation.getInstance().setupProcess();
    }
    public static void main(String[] args) throws Exception{
        RecommendationClient.getInstance().setup();
        SpringApplication.run(RecommendationClient.class, args);

    }
}
