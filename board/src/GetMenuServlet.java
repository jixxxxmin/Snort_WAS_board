import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;




@WebServlet(name = "GetMenuServlet", urlPatterns = {"/board/menu"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String DB_URL = System.getenv("DB_URL");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");

        response.setContentType("application/json;charset=UTP-8");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT menu_name FROM Menu");
             ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println(rs.getString("menu_name"));
                }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println("server error: " + e.getMessage());
        }
    }
}
