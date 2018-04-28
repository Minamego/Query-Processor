package cQueryProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import spark.Request;
import spark.Response;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static com.mongodb.client.model.Sorts.descending;
import static spark.Spark.*;
public class AutoComplete extends HttpServlet {
    private static MongoCollection<Document> past;
    AutoComplete(MongoCollection<org.bson.Document> u)
    {
        past = u;
    }

    protected void doGet()  {
        post("/AutoComplete" ,(Request request , Response response)-> {
            String query = request.queryParams("query");
            response.type("text/html");
            return  generatSuggestions(query);
        });

    }

    public String generatSuggestions(String q){

        StringBuffer returnData = null;
        FindIterable<Document> docs = past.find().sort(descending("rank"));
        ArrayList<String> arr = new ArrayList<>();
        for(Document doc : docs)
        {
            String s = doc.getString("text");
            if(s.length() < q.length()) continue;
            boolean flag = true;
            for(int j = 0 ; j<q.length() ; j++)
            {
                if(q.charAt(j) != s.charAt(j))
                {
                    flag = false;
                    break;
                }
            }
            if(flag) arr.add(s);
        }
            returnData = new StringBuffer("{\"query\":{");
            returnData.append("\"name\": \"" +q +"\",");
            returnData.append("\"suggestions\": [");
            for(int i = 0 ; i<arr.size() ; i++)
            {
                if(i != arr.size() -1 ) returnData.append("{\"name\": \""+ arr.get(i) +"\"},");
                else returnData.append("{\"name\": \""+ arr.get(i) +"\"}");

            }
            returnData.append("]}}");

        return returnData.toString();
    }
}
