package database;

import java.sql.*;

public class DatabaseConnector {
    private Connection conn;

    public static ResultSet main(String query) {
        ResultSet res = null;
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5438/postgres", "postgres",
                    "postgres");
            Statement stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return res;
    }
}