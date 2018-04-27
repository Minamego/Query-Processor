package cQueryProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    public static String username = null;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String uname = request.getParameter("uname");
        String psw = request.getParameter("psw");

        if(authontication(uname,psw)){

            username = uname;
            String results = generatHomePage();
            out.println(results);

        }else{

            out.println("invalid name or password");

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    // TODO: implement Sign in authontication
    private boolean authontication(String username,String psw){
        if(username.equals("Hamada") && psw.equals("1234"))return true;
        return false;
    }

    public String generatHomePage(){

        String htmlPage ="<html>\n" +
                "<head>\n" +
                "    <title>Cirage Search Engine</title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <link rel=\"stylesheet\" href=\"HomePage.css\">\n" +
                "\n" +
                "</head>\n" +
                "<body style=\"background-color: #1a1a1a;\">\n" +
                "\n" +
                "<script src= \"HomePage.js\" type=\"text/javascript\">\n" +
                "\n" +
                "</script>\n" +
                "\n" +
                "    <center>\n" +
                "        <h1 style=\"font-size: 120px; margin-top: 100px; color: #1a1a1a; background-color:#bfbfbf; width: 500px;\n" +
                "    border-radius: 20px 20px 20px 20px;\">Cirage</h1>\n" +
                "\n" +
                "        <form  action=\"/search\" method=\"get\">\n" +
                "            <input type=\"text\" placeholder=\"Search...\" name=\"search\" required>\n" +
                "            <br/><br/>\n" +
                "            <button id = \"search_button\" value = \"Go to hello\" class=\"button-search\"><span>Search</span></button>\n" +
                "            <button class=\"button-login\" style=\"display: none;\" onclick=\"document.getElementById('id01').style.display='block'\" style=\"width:auto; margin-left: 20px;\"><span>Login</span></button>\n" +
                "        </form>\n" +
                "\n" +
                "    </center>\n" +
                "\n" +
                "    <div id=\"id01\" class=\"modal\">\n" +
                "        <form class=\"modal-content animate\" action=\"/login\" method=\"post\">\n" +
                "            <div class=\"imgcontainer\">\n" +
                "                <span onclick=\"document.getElementById('id01').style.display='none'\" class=\"close\" title=\"Close Modal\">&times;</span>\n" +
                "                <img src=\"smile.png\" alt=\"smile\" class=\"smile\">\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"container\">\n" +
                "                <label for=\"uname\"><b>Username</b></label>\n" +
                "                <input id=login placeholder=\"Enter Username\" name=\"uname\" required>\n" +
                "\n" +
                "                <label for=\"psw\"><b>Password</b></label>\n" +
                "                <input id=login type=\"password\" placeholder=\"Enter Password\" name=\"psw\" required>\n" +
                "\n" +
                "                <button type=\"submit\">Sign in</button>\n" +
                "\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"container\" style=\"background-color:#f1f1f1\">\n" +
                "                <button type=\"button\" onclick=\"document.getElementById('id01').style.display='none'\" class=\"cancelbtn\">Cancel</button>\n" +
                "                <button type=\"button\" onclick=\"viewSignUpForm();\" class=\"signupbtn\">Sign Up</button>\n" +
                "            </div>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "\n" +
                "\n" +
                "    <div id=\"id02\" class=\"modal\">\n" +
                "        <form class=\"modal-content animate\" action=\"/signup\" method=\"post\">\n" +
                "            <div class=\"imgcontainer\">\n" +
                "                <span onclick=\"document.getElementById('id02').style.display='none'\" class=\"close\" title=\"Close Modal\">&times;</span>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"container\">\n" +
                "                <label for=\"uname\"><b>Username</b></label>\n" +
                "                <input id=login placeholder=\"Enter Your Username\" name=\"uname\" required>\n" +
                "\n" +
                "                <label for=\"psw\"><b>Password</b></label>\n" +
                "                <input id=login placeholder=\"Enter Your Password\" name=\"psw\" required>\n" +
                "\n" +
                "                <button type=\"submit\">Create Account</button>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"container\" style=\"background-color:#f1f1f1\">\n" +
                "                <button type=\"button\" onclick=\"document.getElementById('id02').style.display='none'\" class=\"cancelbtn\">Cancel</button>\n" +
                "            </div>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";

        return htmlPage;
    }
}
