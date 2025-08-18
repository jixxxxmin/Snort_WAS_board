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




@WebServlet(name = "GetMenuServlet", urlPatterns = {"/board/menu"})
public class GetMenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        String DB_URL = System.getenv("DB_URL");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");

        String query;

        response.setContentType("application/json;charset=UTF-8");
        List<Map<String, String>> menuList = new ArrayList<>();
        Map<String, Map<String, Object>> menuIDMap = new HashMap<>();

        query = "SELECT * FROM Menu";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> menuMap = new HashMap<>();
                menuMap.put("이름", rs.getString("menu_name"));
                menuMap.put("메뉴", new ArrayList<Map<String, Object>>());
                menuList.add(menuMap);

                String menuID = rs.getString("menu_id");
                menuIDMap.put(menuID, menuMap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Get Menu error: " + e.getMessage());
        }

        query = "SELECT * FROM SubMenu";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()){
            
            while (rs.next()) {
                String menuID = rs.getString("menu_id");
                Map<String, Object> parentMenu = menuIDMap.get(menuID);

                Map<String, String> menuMap = new HashMap<>();
                menuMap.put("num", rs.getString("submenu_id"));
                menuMap.put("이름", rs.getString("submenu_name"));
                menuMap.put("설명", rs.getString("descript"));

                ((List<Map<String, String>>) parentMenu.get("메뉴")).add(menuMap);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Get SubMenu error: " + e.getMessage());
        }
        
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(menuList);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
