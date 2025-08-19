import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;




// @WebServlet(name = "GetTitleServlet", urlPatterns = {"/board/title"})
public class GetTitleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String menuID = request.getParameter("menu_id");
        String submenuID = request.getParameter("submenu_id");

        String DB_URL = System.getenv("DB_URL");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");

        String query;
        String idToQuery = "";

        response.setContentType("application/json;charset=UTF-8");
        List<Map<String, String>> titleList = new ArrayList<>();

        if (menuID != null) {
            query = "SELECT post_id, title, " +
                    "CASE " +
                    "    WHEN write_date > NOW() - INTERVAL 24 HOUR THEN DATE_FORMAT(write_date, '%H:%i') " +
                    "    ELSE DATE_FORMAT(write_date, '%Y-%m-%d') " +
                    "END AS display_date " +
                    "FROM Post WHERE submenu_id IN (SELECT submenu_id FROM SubMenu WHERE menu_id = ?) " +
                    "ORDER BY post_id DESC";
            idToQuery = menuID;
        }
        else if (submenuID != null) {
            query = "SELECT post_id, title, " +
                    "CASE " +
                    "    WHEN write_date > NOW() - INTERVAL 24 HOUR THEN DATE_FORMAT(write_date, '%H:%i') " +
                    "    ELSE DATE_FORMAT(write_date, '%Y-%m-%d') " +
                    "END AS display_date " +
                    "FROM Post WHERE submenu_id = ? ORDER BY post_id DESC";
            idToQuery = submenuID;
        }
        else {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("{\"error\":\"menu_id 또는 submenu_id가 존재하지 않습니다.\"}");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, Integer.parseInt(idToQuery));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> titleMap = new HashMap<>();
                    titleMap.put("num", rs.getString("post_id"));
                    titleMap.put("article", rs.getString("title"));
                    titleMap.put("timestamp", rs.getString("display_date"));
                    titleList.add(titleMap);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"데이터 처리 중 오류가 발생했습니다: " + e.getMessage() + "\"}");
            return;
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(titleList);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
