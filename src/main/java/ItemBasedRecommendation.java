import com.google.gson.Gson;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.ConjugateGradientOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;



public class ItemBasedRecommendation {

    private static ItemBasedRecommendation itemBasedRecommendation = null;

    static ItemBasedRecommender recommender = null;
    DataLoadBridge dataLoadBridge = null;
    private ItemBasedRecommendation(){
        dataLoadBridge =  DataLoadBridge.getInstance();
    }

    public static ItemBasedRecommendation getInstance(){
        if(itemBasedRecommendation ==null){
            itemBasedRecommendation=new ItemBasedRecommendation();
        }
        return itemBasedRecommendation;
    }

    /*ResponseEntity<Object> getItemBasedRecommendations(String userId, String numberOfRecommendation )throws Exception{
        String output = "";
        List<RecommendedItem> recommendations;
        if(userId==null) {
            return null;
        }
        int user = Integer.parseInt(userId);
        int numberRecommendation =  Integer.parseInt(numberOfRecommendation);
        recommendations = recommender.recommend(user, numberRecommendation);
        return new ResponseEntity<>(recommendations, HttpStatus.OK);
    }*/

    //Overlaod for string written type
    String getItemBasedRecommendations(String userId, String numberOfRecommendation )throws Exception{
       // String output = "";
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
        System.out.println("ITEM Based recommendation system");

        DataModel model = new FileDataModel(new File("UserItemRating.csv")); //original file

        //ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
        //ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
        ItemSimilarity similarity = new TanimotoCoefficientSimilarity(model);
        //ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);

        int neighbours = 15;
        System.out.println( "Neighbours = "+neighbours);
        System.out.println( "Similarity = "+similarity.toString());

        Optimizer optimizer=new ConjugateGradientOptimizer();
        recommender = new KnnItemBasedRecommender(model,similarity,optimizer,neighbours);

        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                ItemSimilarity similarity = new EuclideanDistanceSimilarity(model);
                //Optimizer optimizer = new NonNegativeQuadraticOptimizer();
                return new GenericItemBasedRecommender(model,similarity);
            }
        };


        RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        double score = evaluator.evaluate(recommenderBuilder, null, model, 0.7, 1.0);
        System.out.println("RMSE: " + score);

        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, model, null, 10, 0.1, 0.7); // evaluate precision recall at 10

        System.out.println("Precision: " + stats.getPrecision());
        System.out.println("Recall: " + stats.getRecall());
        System.out.println("F1 Score: " + stats.getF1Measure());
    }
}
