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




// @WebServlet(name = "GetArticleServlet", urlPatterns = {"/article"})
public class GetArticleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String articleID = request.getParameter("article_id");
        String menu_id = request.getParameter("menu_id");
        String submenu_id = request.getParameter("submenu_id");

        String DB_URL = System.getenv("DB_URL");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");

        String query_article;
        String query_title;
        int idToQuery = 0;
        int articleToQuery = 0;

        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> articleText = new HashMap<>();
        Map<String, String> titlesMap = new HashMap<>();


        if (articleID != null) {
            query_article = "SELECT title, texts, " +
                            "CASE " +
                            "    WHEN write_date > NOW() - INTERVAL 24 HOUR THEN DATE_FORMAT(write_date, '%H:%i') " +
                            "    ELSE DATE_FORMAT(write_date, '%Y-%m-%d') " +
                            "END AS display_date " +
                            "FROM Post WHERE post_id = ?";
            articleToQuery = Integer.parseInt(articleID);
        }
        else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"article_id가 존재하지 않습니다.\"}");
            return;
        }

        if (submenu_id != null) {
            idToQuery = Integer.parseInt(submenu_id);
            query_title = "(SELECT post_id, title, 'prev' as type FROM Post WHERE post_id < ? AND submenu_id = ? ORDER BY post_id DESC LIMIT 1) " +
                          "UNION ALL " +
                          "(SELECT post_id, title, 'next' as type FROM Post WHERE post_id > ? AND submenu_id = ? ORDER BY post_id ASC LIMIT 1)";
        }
        else if (menu_id != null) {
            idToQuery = Integer.parseInt(menu_id);
            query_title = "(SELECT post_id, title, 'prev' as type FROM Post WHERE post_id < ? AND submenu_id IN (SELECT submenu_id FROM SubMenu WHERE menu_id = ?) ORDER BY post_id DESC LIMIT 1) " +
                          "UNION ALL " +
                          "(SELECT post_id, title, 'next' as type FROM Post WHERE post_id > ? AND submenu_id IN (SELECT submenu_id FROM SubMenu WHERE menu_id = ?) ORDER BY post_id ASC LIMIT 1)";
        }
        else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("{\"error\":\"menu_id or submenu_id가 존재하지 않습니다.\"}");
            return;
        }


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement stmt = conn.prepareStatement(query_article)) {
                
                stmt.setInt(1, articleToQuery);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    articleText.put("title", rs.getString("title"));
                    articleText.put("content", rs.getString("texts"));
                    articleText.put("timestamp", rs.getString("display_date"));
                }
                else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().println("{\"error\":\"해당 data가 존재하지 않습니다.\"}");
                    return;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(query_title)){
                
                stmt.setInt(1, articleToQuery);
                stmt.setInt(2, idToQuery);
                stmt.setInt(3, articleToQuery);
                stmt.setInt(4, idToQuery);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String type = rs.getString("type");
                    Map<String, String> navPost = new HashMap<>();
                    navPost.put("id", rs.getString("post_id"));
                    navPost.put("title", rs.getString("title"));
                    
                    articleText.put(type + "Post", navPost);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("{\"error\":\"게시글 데이터 처리 중 오류 발생 : " + e.getMessage() + "\"}");
            return;
        }


        Gson gson = new Gson();
        String jsonResponse = gson.toJson(articleText);

        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
