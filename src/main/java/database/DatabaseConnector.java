package database;


import java.sql.*;

public class DatabaseConnector {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private final String password = "postgres";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void getUsers(){
        String SQL = "SELECT * FROM users";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}



