import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;




@WebServlet(name = "GetMenuServlet", urlPatterns = {"/board/Menu"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        
    }
}