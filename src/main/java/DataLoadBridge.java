import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DataLoadBridge {

    private static DataLoadBridge dataLoadBridge= null;

    DBHelper dbhelper;
    UserBasedRecommendation userBasedRecommendation = null;
    ItemBasedRecommendation itemBasedRecommendation = null;

    private DataLoadBridge(){
        dbhelper =  DBHelper.getInstance();
    }

    public static DataLoadBridge getInstance(){
        if(dataLoadBridge ==null)
            dataLoadBridge = new DataLoadBridge();

        return dataLoadBridge;
    }

    public void dataLoad() throws Exception{
        dbhelper.addorupdatedatatoCSV();
    }

    public void insertDataIntotable(UserItemModel user) throws Exception{
        dbhelper.insertDataIntotable(user);
    }


    public void persistPerson(UserItemModel user) throws Exception {
        dbhelper.insertDataIntotable(user);
        dbhelper.addorupdatedatatoCSV();
    }
}
