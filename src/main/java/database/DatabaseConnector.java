package database;

import java.sql.*;

public class DatabaseConnector {
    public Connection conn;

    public DatabaseConnector() {
        try {
            // étape 1: charger la classe de driver
            Class.forName("org.postgresql.Driver");

            // étape 2: créer l'objet de connexion
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5438/postgres", "postgres",
                    "postgres");
            // étape 3: créer l'objet statement
        } catch (Exception e) {
            System.out.println("Inside Database Connector");
            System.out.println(e);
        }
    }

    public void insert(String query) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void delete(String query) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ResultSet query(String query) {
        ResultSet res = null;
        try {
            Statement stmt = conn.createStatement();
            res = stmt.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Inside Database Query");
            System.out.println(e);
        }
        return res;
    }

    public void close() {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

