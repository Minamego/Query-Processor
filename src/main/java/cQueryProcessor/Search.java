package cQueryProcessor;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/Search"})
public class Search extends HttpServlet {

    public static ArrayList<String>titles = new ArrayList<>();
    public static ArrayList<String>urls = new ArrayList<>();
    public static ArrayList<String>prgs = new ArrayList<>();
    public static ArrayList<Boolean>intersed = new ArrayList<>();
    public static int numberResultsPerPage = 5;
    public static String query;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    private boolean collectionExists( String collectionName , MongoIterable<String> collectionNames) {

        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        query = request.getParameter("search");


        queryProcessor qp = new queryProcessor(query);
        String processedQuery = qp.run();
        boolean phSearch = qp.isPhSearch();
        String [] arr = processedQuery.split(" ");

        MongoClient mongoClient = new MongoClient("localhost" , 27017);
        MongoDatabase database = mongoClient.getDatabase("APT");
        MongoIterable<String> collectionNames = database.listCollectionNames();

        MongoCollection<org.bson.Document> terms;
        MongoCollection<org.bson.Document> documents;
        MongoCollection<org.bson.Document> links;

        if (!collectionExists("words" , collectionNames)) {
            database.createCollection("words");
        }
        if (!collectionExists("documents" , collectionNames)) {
            database.createCollection("documents");
        }
        if (!collectionExists("links" , collectionNames)) {
            database.createCollection("links");
        }
        terms = database.getCollection("words");
        documents = database.getCollection("documents");
        links = database.getCollection("links");

        ArrayList<Document> words = new ArrayList<>();
        for(int i = 0 ; i<arr.length ; ++i)
        {
            String cur = arr[i];
            Document word = terms.find(new BasicDBObject("term", cur)).first();
            words.add(word);
        }
        urls = new ArrayList<>();
        titles = new ArrayList<>();
        prgs = new ArrayList<>();
        intersed = new ArrayList<>();

        if(phSearch) Ranker.phraseSearch(words , documents , links,urls,prgs,titles,intersed);
        else Ranker.normalSearch(words , documents , links,urls,prgs,titles,intersed);

//        for (int i = 0; i < 100; ++i) {
//            titles.add("Online judge - Wikipedia");
//            urls.add("https://en.wikipedia.org/wiki/Online_judge");
//            prgs.add("An online judge is an online system to " +
//                    "test programs in programming contests. " +
//                    "They are also used to practice for such contests. " +
//                    "Many of these systems organize their own contests. " +
//                    "The system can compile and execute code, and " +
//                    "test them with pre-constructed data. Submitted " +
//                    "code may be run with restrictions, including " +
//                    "time ...");
//        }
//        for(int i=0; i < urls.size(); ++i){
//            if(i%2 == 0) {
//                intersed.add(true);
//            }else{
//                intersed.add(false);
//            }
//        }

        String results = generatResultsPage();
        out.println(results);

    }

    private String generatResultsPage(){

        String htmlPage = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"\n" +
                "http-equiv=\"refresh\" content=\"0; url=http://example.com/\">\n" +
                "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                "<link rel=\"stylesheet\" href=\"results.css\">\n" +
                "<script\n" +
                "  src=\"https://code.jquery.com/jquery-migrate-3.0.1.min.js\"\n" +
                "  integrity=\"sha256-F0O1TmEa4I8N24nY0bya59eP6svWcshqX1uzwaWC4F4=\"\n" +
                "  crossorigin=\"anonymous\"></script>\n" +
                "\n" +
                "</head>\n" +
                "<body>\n" +
                "<div style=\"background: #bfbfbf; \n" +
                "    border-radius: 5px 5px 5px 5px;\">\n" +
                "    \n" +
                "<form class=\"search\" action=\"/search\" method=\"get\">\n" +
                "    \n" +
                "    <h1><a href = \"index.jsp\"><span style=\"color: #1a1a1a;\">Cirage</span></a></h1>\n" +
                "    <input type=\"text\" placeholder=\"Search..\" name=\"search\" value=\"" + this.query +"\">\n" +
                "    <button type=\"submit\"><i class=\"fa fa-search\"></i></button>\n" +
                "    <a href=\"#\"><img id = \"bell\"" +(Login.username == null ? "style=\"display: none;":"")+"\" onclick=\"hello();\" src=\"bell-icon.png\"/></a>\n" +
                "</form>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "<div style=\"margin-left: 11%;\">\n" +
                "    \n" +
                "\n";

        for (int i = 0; i < urls.size(); ++i) {
            htmlPage = htmlPage + "<form id=\"r"+ (i+1) +"\" class=\"result\">\n" +
                    "    <h1><a href=\""+ Search.urls.get(i) +"\">"+ Search.titles.get(i) +"</a></h1>\n" +
                    "    <u>" + urls.get(i) + " </u>\n" +
                    "    <p>" + prgs.get(i) + "</p>\n" +
                    "\n" +
                    "</form>\n" +
                    "\n" +
                    "<form id =\"f"+ (i+1) +"\" class=\"follow\"  action=\"/follow\" method =\"get\">\n" +
                    "    <input type=\"hidden\" name=\"url\" value=\"https://en.wikipedia.org/wiki/Online_judge\">\n" +
                    "    <input type=\"hidden\" name=\"indx\" value=\"" + i + "\">\n" +
                    "    <input type=\"hidden\" name=\"follow\" value=\"" + (intersed.get(i)!=null && intersed.get(i) ? "no":"yes") + "\">\n" +
                    "    <input type=\"submit\""+ (Login.username == null ? "style=\"display: none;\"":"") +"value=\"" + (intersed.get(i)!=null && intersed.get(i) ? "unfollow":"follow") + "\">\n" +
                    "</form>"+
                    "\n";
        }
      htmlPage = htmlPage + "\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<div id = \"pagingBar\" class=\"pagingBar\">\n" +
                "    <button class = \"PreviousButton\" id = \"prvbtn\">&laquo; Previous</button>\n" +
                "\n" +
                "      <button id = \"page1\" class=\"pageButton\" onclick=\"moveToPage(1);\">1</button>\n" +
                "      <button id = \"page2\" class=\"pageButton\" onclick=\"moveToPage(2);\">2</button>\n" +
                "      <button id = \"page3\" class=\"pageButton\" onclick=\"moveToPage(3);\">3</button>\n" +
                "      <button id = \"page4\" class=\"pageButton\" onclick=\"moveToPage(4);\">4</button>\n" +
                "      <button id = \"page5\" class=\"pageButton\" onclick=\"moveToPage(5);\">5</button>\n" +
                "      <button id = \"page6\" class=\"pageButton\" onclick=\"moveToPage(6);\">6</button>\n" +
                "      <button id = \"page7\" class=\"pageButton\" onclick=\"moveToPage(7);\">7</button>\n" +
                "      <button id = \"page8\" class=\"pageButton\" onclick=\"moveToPage(8);\">8</button>\n" +
                "      <button id = \"page9\" class=\"pageButton\" onclick=\"moveToPage(9);\">9</button>\n" +
                "      <button id = \"page10\" class=\"pageButton\" onclick=\"moveToPage(10);\">10</button>\n" +
                "\n" +
                "\n" +
                "    <button class = \"NextButton\" id = \"nxtbtn\">Next &raquo;</button>\n" +
                "</div>\n" +
                "        \n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "function hello() {\n" +
              "\tconsole.log(\"Hell Bell\");\n" +
              "}\n" +
              "\n" +
              "function changeFollow(id){\n" +
              "\tif(document.getElementById(\"i\" + id).innerHTML == \"follow\"){\n" +
              "\t\tdocument.getElementById(\"i\" + id).innerHTML = \"unfollow\";\n" +
              "\t\tdocument.getElementById(\"ii\" + id).value = \"no\";\n" +
              "\t} else{\n" +
              "\t\tdocument.getElementById(\"i\" + id).innerHTML = \"follow\";\n" +
              "\t\tdocument.getElementById(\"ii\" + id).value = \"yes\";\n" +
              "\t}\n" +
              "}\n" +
              "\n" +
              "\n" +
              "console.log(\"Script\");\n" +
              "\n" +
              "var np;       // number of pages\n" +
              "var nrpp = " + numberResultsPerPage + "; // nubmber of results per page\n" +
              "var totalNoRes = " + urls.size() + "; // total number of results\n" +
              "var nrLstP = totalNoRes % nrpp;\n" +
              "var curPage; // current active page\n" +
              "var id=\"r\";\n" +
              "\n" +
              "function updateButtons(nextCliked){\n" +
              "\n" +
              "  if(nextCliked == 1){\n" +
              "    var newVal = curPage;\n" +
              "    document.getElementById(\"page\"+(curPage%10)).style.background = 'orange';\n" +
              "    document.getElementById(\"page\"+(curPage%10)).style.color = 'white';\n" +
              "    for (var i=1; i<= 10; i++) {\n" +
              "      document.getElementById(\"page\"+i).innerHTML = newVal++;\n" +
              "      if(newVal > np){\n" +
              "      \tfor (i++; i<= 10; i++){\n" +
              "      \t\tdocument.getElementById(\"page\"+i).innerHTML = newVal++;\n" +
              "      \t\tdocument.getElementById(\"page\"+i).style.background = \"gray\";\n" +
              "      \t}\t\n" +
              "      \tbreak;\n" +
              "      }\n" +
              "    } \n" +
              "  }else{\n" +
              "  \tvar newVal = curPage;\n" +
              "  \tfor (i = 1; i<= 10; i++){\n" +
              "      \tdocument.getElementById(\"page\"+i).style.background = \"white\";\n" +
              "    }\n" +
              "    document.getElementById(\"page\"+10).style.background = 'orange';\n" +
              "    document.getElementById(\"page\"+10).style.color = 'white';\n" +
              "    for (var i=10; i> 0; i--) {\n" +
              "      document.getElementById(\"page\"+i).innerHTML = newVal--;\n" +
              "    } \n" +
              "  }\n" +
              "\n" +
              "}\n" +
              "\n" +
              "function removeCurPage(){\n" +
              "\n" +
              "\tvar i = (curPage-1)*nrpp + 1;\n" +
              "\tif(curPage == np && nrLstP != 0){\n" +
              "\t\tfor (var j = 1; j <= nrLstP; j++) {\n" +
              "\t\t\tdocument.getElementById(id + i).style.display = \"none\";\n" +
              "\t\t\tdocument.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "\n" +
              "\t\t\ti++;\n" +
              "\t\t}\n" +
              "\t}else{\n" +
              "\t\tfor (var j = 1; j <= nrpp; j++) {\n" +
              "\t\t\tdocument.getElementById(id + i).style.display = \"none\";\n" +
              "\t\t\tdocument.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "\n" +
              "\t\t\ti++;\n" +
              "\t\t}\n" +
              "\t}\n" +
              "}\n" +
              "\n" +
              "function viewPage(pn) {\n" +
              "\tvar i = (pn-1)*nrpp + 1;\n" +
              "\tif(pn == np && nrLstP){\n" +
              "\t\tfor (var j = 1; j <= nrLstP; j++) {\n" +
              "\t\t\tdocument.getElementById(id + i).style.display = \"block\";\n" +
              "\t\t\tdocument.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "\t\t\ti++;\n" +
              "\t\t}\n" +
              "\t}else{\n" +
              "\t\tfor (var j = 1; j <= nrpp; j++) {\n" +
              "\t\t\tdocument.getElementById(id + i).style.display = \"block\";\n" +
              "\t\t\tdocument.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "\t\t\ti++;\n" +
              "\t\t}\n" +
              "\t}\n" +
              "}\n" +
              "\n" +
              "function moveToPage(bid){\n" +
              "\tconsole.log(\"moveToPage #\"+bid);\n" +
              "\tif(document.getElementById(\"page\"+bid).innerHTML <= np){\n" +
              "\n" +
              "\t\tvar tmp;\n" +
              "\t\tif(curPage%10 == 0)tmp=10;\n" +
              "\t\telse tmp = curPage%10;\n" +
              "\t\tif(document.getElementById(\"page\"+tmp).innerHTML != document.getElementById(\"page\"+bid).innerHTML) {\n" +
              "\t\t\tdocument.getElementById(\"page\"+tmp).style.background = 'white';\n" +
              "\t\t\tdocument.getElementById(\"page\"+tmp).style.color = 'black';\n" +
              "\t\t\tremoveCurPage();\n" +
              "\t\t\tcurPage = document.getElementById(\"page\"+bid).innerHTML;\n" +
              "\t\t\tviewPage(curPage);\n" +
              "\t\t\tif(curPage%10 == 0)tmp=10;\n" +
              "\t\t\telse tmp = curPage%10;\n" +
              "\t\t\tdocument.getElementById(\"page\"+tmp).style.background = 'orange';\n" +
              "\t\t\tdocument.getElementById(\"page\"+tmp).style.color = 'white';\n" +
              "\t\t\twindow.scrollTo(0, 0);\n" +
              "\t\t}\n" +
              "\t}\n" +
              "}\n" +
              "\n" +
              "window.onload = function () {\n" +
              "\n" +
              "\tvar x = document.getElementById(\"page1\");\n" +
              "\tconsole.log(x);\n" +
              "\tx.style.background = 'orange';\n" +
              "\tx.style.color = 'white';\n" +
              "  \tcurPage = 1;\n" +
              "  for (var i = 1; i <= totalNoRes; i++) {\n" +
              "    x = document.getElementById(id + i);\n" +
              "    if(i>=1 && i<= nrpp){\n" +
              "       x.style.display = \"block\";\n" +
              "       document.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "    }else{\n" +
              "       x.style.display = \"none\";\n" +
              "       document.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "    }\n" +
              "  }\n" +
              "  np = Math.ceil(totalNoRes/nrpp);\n" +
              "\n" +
              "  if(np == 1){\n" +
              "  \tdocument.getElementById(\"pagingBar\").style.display = \"none\";\n" +
              "  }\n" +
              "\n" +
              "  for(var i = np+1; i <= 10; i++) {\n" +
              "    document.getElementById(\"page\"+i).style.background = \"gray\";\n" +
              "  } \n" +
              "}\n" +
              "\n" +
              "window.onclick = function(event) {\n" +
              "\n" +
              "var nextButton = document.getElementById(\"nxtbtn\");\n" +
              "var PreviousButton = document.getElementById(\"prvbtn\");\n" +
              "if (event.target == nextButton) {\n" +
              "\n" +
              "\t    for (var i = 1; i <= totalNoRes; i++) {\n" +
              "\t\t    \n" +
              "\t\t    var x = document.getElementById(id + i);\n" +
              "\t\t    if(x.style.display == \"block\" && curPage != np) {\n" +
              "\t\t        for (var j = 1; j <= nrpp; j++) {\n" +
              "\t\t          x.style.display = \"none\";\n" +
              "\t\t          document.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "\t\t          i++;\n" +
              "\t\t          if(i > totalNoRes)break;\n" +
              "\t\t          x = document.getElementById(id + i);\n" +
              "\t\t        }\n" +
              "\t\t        if(i <= totalNoRes){\n" +
              "\t\t        for (var j = 1; j <= nrpp; j++) {\n" +
              "\t\t          x.style.display = \"block\";\n" +
              "\t\t          document.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "\t\t          i++;\n" +
              "\t\t          if(i > totalNoRes)break;\n" +
              "\t\t          x = document.getElementById(id + i);\n" +
              "\t\t        }\n" +
              "\t\t      }\n" +
              "\t\t    } \n" +
              "\t    }\n" +
              "\n" +
              "\t    if(curPage < np){\n" +
              "\t    \tvar tmp;\n" +
              "\t    \tif(curPage%10 == 0)tmp=10;\n" +
              "\t    \telse tmp = curPage%10;\n" +
              "\t    \tdocument.getElementById(\"page\"+tmp).style.background = 'white';\n" +
              "\t   \t    document.getElementById(\"page\"+tmp).style.color = 'black';\n" +
              "\t    \tcurPage++;\n" +
              "\t    \ttmp++;\n" +
              "\t    \tif ((curPage-1)%10 == 0) {\n" +
              "\t    \t\tupdateButtons(1);\n" +
              "\t    \t}\n" +
              "\t    \tif(tmp <= 10){\n" +
              "\t\t    \tdocument.getElementById(\"page\"+tmp).style.background = 'orange';\n" +
              "\t\t   \t    document.getElementById(\"page\"+tmp).style.color = 'white';\n" +
              "\t\t    \twindow.scrollTo(0, 0);\n" +
              "\t    \t}\n" +
              "\t    }\n" +
              "\t    console.log(\"curPage= \" + curPage);\n" +
              "\n" +
              "\t}else if(event.target == PreviousButton){\n" +
              "\n" +
              "\t  if(curPage == np && nrLstP != 0){\n" +
              "\t  \tx = document.getElementById(id + totalNoRes);\n" +
              "\t  \tvar i = totalNoRes;\n" +
              "\t  \tfor (var j = 1; j <= nrLstP; j++){\n" +
              "\t          x.style.display = \"none\"; \n" +
              "\t          document.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "\t          i--;\n" +
              "\t          if(j+1 > nrLstP)break;\n" +
              "\t          x = document.getElementById(id + i);\n" +
              "\t    }\n" +
              "\t    if(i <= totalNoRes - nrLstP){\n" +
              "\t\t    x = document.getElementById(id + i);\n" +
              "\t\t    for (var j = 1; j <= nrpp; j++){\n" +
              "\t\t          x.style.display = \"block\"; \n" +
              "\t\t          document.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "\t\t          i--;\n" +
              "\t\t          if(j+1 > nrpp)break;\n" +
              "\t\t          x = document.getElementById(id + i);\n" +
              "\t\t    }\n" +
              "\t\t}\n" +
              "\t  } else {\n" +
              "\n" +
              "\t\t  for (var i = totalNoRes; i >= 1; i--) {\n" +
              "\t\t    x = document.getElementById(id + i);\n" +
              "\t\t    if(x.style.display == \"block\" && curPage != 1){\n" +
              "\t\t        for (var j = 1; j <= nrpp; j++){\n" +
              "\t\t          x.style.display = \"none\"; \n" +
              "\t\t          document.getElementById(\"f\"+i).style.display = \"none\";\n" +
              "\t\t          i--;\n" +
              "\t\t          x = document.getElementById(id + i);\n" +
              "\t\t        }\n" +
              "\t\t        for (var j = 1; j <= nrpp; j++){\n" +
              "\t\t          x.style.display = \"block\"; \n" +
              "\t\t          document.getElementById(\"f\"+i).style.display = \"block\";\n" +
              "\t\t          i--;\n" +
              "\t\t          x = document.getElementById(id + i);\n" +
              "\t\t        }\n" +
              "\t\t        break;\n" +
              "\t\t    }\n" +
              "\t\t  }\n" +
              "\t\t}\n" +
              "\t  \tif(curPage  >= 2){\n" +
              "\n" +
              "\t  \t\tvar tmp;\n" +
              "\t    \tif(curPage%10 == 0)tmp=10;\n" +
              "\t    \telse tmp = curPage%10;\n" +
              "\t  \t\tdocument.getElementById(\"page\"+tmp).style.background = 'white';\n" +
              "\t   \t    document.getElementById(\"page\"+tmp).style.color = 'black';\n" +
              "\t    \tcurPage--;\n" +
              "\t    \tif (curPage%10 == 0) {\n" +
              "\t    \t\tupdateButtons(0);\n" +
              "\t    \t}\n" +
              "\t    \ttmp--;\n" +
              "\t    \tif(tmp >= 1){\n" +
              "\t\t    \tdocument.getElementById(\"page\"+tmp).style.background = 'orange';\n" +
              "\t\t   \t    document.getElementById(\"page\"+tmp).style.color = 'white';\n" +
              "\t\t\t\twindow.scrollTo(0, 0);\n" +
              "\t\t\t}\n" +
              "\t  \t}\n" +
              "\t    console.log(\"curPage= \" + curPage);\n" +
              "\t    \n" +
              "\t}\n" +
              "}\n" +
              "\n"+
                "</script>" +
                "\n" +
                "</html>";

        return htmlPage;
    }
}
