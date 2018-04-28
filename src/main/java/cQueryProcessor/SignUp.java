package cQueryProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import spark.Request;
import spark.Response;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;

import static spark.Spark.post;

@WebServlet(urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {
    private static MongoCollection<Document> users;
    SignUp(MongoCollection<org.bson.Document> u)
    {
        users = u;
    }

    public void doPost() {

        post("/signup", (Request request, Response response) ->
        {
            response.type("text/html");

            String uname = request.queryParams("uname");
            String psw = request.queryParams("psw");

            if (authontication(uname , psw)) {
               return  Login.generatHomePage(uname);
            } else {
                return "Username is already taken , choose another one";
            }
        });
    }

    private boolean authontication(String username ,String password ){
       Document user =  users.find(new BasicDBObject("username", username)).first();
       if(user != null) return false;
        user = new Document();
        ArrayList<String>arr = new ArrayList<>();
        user.append("username", username)
                .append("password", password)
                .append("notifications_urls" , arr)
                .append("notifications_titles" , arr);
        users.insertOne(user);
        return  true;
    }
}
