import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static DBHelper dBHelper = null;

    Connection c = null;
    Statement stmt = null;

    private DBHelper(){
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ChatbotRecommentionDB",
                    "postgres", "toor");
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }


    public static DBHelper getInstance(){
        if(dBHelper ==null )
            dBHelper =  new DBHelper();

        return dBHelper;
    }

    void normalize() {

    }

    ResultSet getAllDataForProcess(){
        ResultSet rs = null;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM UserItemRelation;");


        }catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }

        return rs;
    }

    public List<UserItemModel> getListFromResultset(){
        ResultSet rs = getAllDataForProcess();
        List<UserItemModel> output = new ArrayList<UserItemModel>();
        try{
            while(rs.next()) {
                UserItemModel user = new UserItemModel();
                user.setUserId(rs.getString("User_id"));
                user.setItemId(rs.getString("Item_id"));
                user.setRatings(rs.getString("Ratings"));
                user.setNormalizedRatings(rs.getString("NormalizedRatings"));
                output.add(user);
            }
        }catch (SQLException e) {
                System.err.println( e.getClass().getName()+": "+ e.getMessage() );
                System.exit(0);
        }
        return output;
    }

    public void insertDataIntotable(int userId, int itemId, int ratings)throws Exception{
        stmt = c.createStatement();
        String  query = "INSERT INTO UserItemRelation (User_id,Item_id,Ratings) "
                + "VALUES ("+userId +", "+ itemId + ", "+ratings +");";
        stmt.executeUpdate(query);
    }

    public void insertDataIntotable(UserItemModel user)throws Exception{
        stmt = c.createStatement();
        String  query = "INSERT INTO UserItemRelation (User_id,Item_id,Ratings) "
                + "VALUES ("+user.userId +", "+ user.itemId + ", "+user.ratings +");";
        stmt.executeUpdate(query);
    }



    public void addorupdatedatatoCSV()throws Exception{
        //Connection connection = DriverManager.getConnection(url);
        CopyManager copyManager = new CopyManager((BaseConnection) c);
        File file = new File("UserItemRating.csv");
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        String query = "SELECT User_id,Item_id,Ratings FROM UserItemRelation";

        copyManager.copyOut("COPY (" + query + ") TO STDOUT WITH (FORMAT CSV)", fileOutputStream);

    }



    public void normalizeForUser(){

    }

}
