package cQueryProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        String uname = request.getParameter("uname");
        String psw = request.getParameter("psw");

        if(authontication(uname)) {
            Login lg = new Login();
            Login.username = uname;
            out.println(lg.generatHomePage());
        }else{
            out.println("Username is already taken , choose another one");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    //TODO : implemnt the function -- if(user is already in data base )return false; else return true;
    private boolean authontication(String username){
        if(username.equals("Hamada"))return false;
        return true;
    }
}
