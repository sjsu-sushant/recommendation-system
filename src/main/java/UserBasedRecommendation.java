import com.google.gson.Gson;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserBasedRecommendation {

    private static UserBasedRecommendation userBasedRecommendation = null;

    static UserBasedRecommender recommender = null;

    private UserBasedRecommendation(){
    }

    public static UserBasedRecommendation getInstance(){
        if(userBasedRecommendation==null){
            userBasedRecommendation = new UserBasedRecommendation();
        }
        return userBasedRecommendation;
    }

    /*ResponseEntity<Object> getUserBasedRecommendations(String userId, String numberOfRecommendation )throws Exception{
        List<RecommendedItem> recommendations;
        if(userId==null) {
            return null;
        }
        int user = Integer.parseInt(userId);
        int numberRecommendation =  Integer.parseInt(numberOfRecommendation);

        recommendations = recommender.recommend(user, numberRecommendation);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);

    }*/

    String getUserBasedRecommendations(String userId, String numberOfRecommendation)throws Exception{
        List<RecommendedItem> recommendations;
        if(userId==null) {
            return null;
        }
        int user = Integer.parseInt(userId);
        int numberRecommendation =  Integer.parseInt(numberOfRecommendation);
        recommendations = recommender.recommend(user, numberRecommendation);
        String output  = new Gson().toJson(recommendations);
        return output;
    }



    public void setupProcess() throws Exception{
        // TODO Auto-generated method stub
        System.out.println("USER Based recommendation system");


        DataModel model = new FileDataModel(new File("UserItemRating.csv")); //original file

        ///UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //UserSimilarity similarity = new LogLikelihoodSimilarity(model);
        UserSimilarity similarity = new TanimotoCoefficientSimilarity(model);
        //UserSimilarity similarity = new EuclideanDistanceSimilarity(model);
        //UserSimilarity similarity = new GenericUserSimilarity(model);
        //UserSimilarity similarity = new SpearmanCorrelationSimilarity(model);

        //UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);

        int neighbours = 15;
        System.out.println( "Neighbours = "+neighbours);
        System.out.println( "Similarity = "+similarity.toString());
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(neighbours, similarity, model);

        recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                //ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
                UserSimilarity similarity = new EuclideanDistanceSimilarity(model);


                UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood,similarity);
            }
        };


        RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        double score = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1.0);
        System.out.println("RMSE: " + score);

        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, model, null, 10, 0.1, 0.7); // evaluate precision recall at 10 //0.1

        System.out.println("Precision: " + stats.getPrecision());
        System.out.println("Recall: " + stats.getRecall());
        System.out.println("F1 Score: " + stats.getF1Measure());
    }
}
