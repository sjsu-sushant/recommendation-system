# Chatbot Recommendation Engine

##### Architecture 

![alt text](https://github.com/gaurangmhatre/ChatbotRecommendationEngine/blob/master/images/ArchitectureDiagram.jpg)


##### Two main components type explored with different algorithms,

            1. UserBased Recommendation Engine
                -PearsonCorrelationSimilarity
                -LogLikelihoodSimilarity
                -TanimotoCoefficientSimilarity
                -EuclideanDistanceSimilarity
                -GenericUserSimilarity
                -SpearmanCorrelationSimilarity
            2. ItemBased Recommendation Engine
                -PearsonCorrelationSimilarity
                -LogLikelihoodSimilarity
                -TanimotoCoefficientSimilarity
                -EuclideanDistanceSimilarity
                
             Evaluated the results with RMSE, F1, Precision and Recall
     
            
##### The REST API is written with Spring
            
            Endpoint 1:[GET] /getItemBasedRecommendations
            eg:
                http://localhost:8090/getItemBasedRecommendations?userId=200&numberOfRecommendation=6
                
                output:
                [{"itemID":1,"value":3.5782933},
                {"itemID":19,"value":3.5644608},
                {"itemID":13,"value":3.5610337},
                {"itemID":4,"value":3.5541322},
                {"itemID":17,"value":3.5536952},
                {"itemID":18,"value":3.5515275}]
            
            Endpoint 2:[GET] /getUserBasedRecommendations
            eg:
               http://localhost:8090/getUserBasedRecommendations?userId=200&numberOfRecommendation=6
                            
               output:
               [{"itemID":1,"value":3.8856046},
               {"itemID":19,"value":3.7924228},
               {"itemID":13,"value":3.5575802},
               {"itemID":18,"value":3.2640123},
               {"itemID":17,"value":3.2016375},
               {"itemID":4,"value":3.1363637}]
            
            
            Endpoint 3: [POST] /updateUserData
            eg:
                http://localhost:8090/updateUserData     body: {"userId": "200","itemId": "9","ratings": "5"}
                
                
 ##### Technology
            1. Postgrace for loading data
            2. Apache.Mahout for recommendations
            3. Java Spring Boot for REST api
 
The spring application is running on port 8090 by default.
 