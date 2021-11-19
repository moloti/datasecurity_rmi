package database;

import java.sql.*;

public class DatabaseConnector {
    public static void main(String args[]) {
        try {
            // étape 1: charger la classe de driver
            Class.forName("org.postgresql.Driver");

            // étape 2: créer l'objet de connexion
            Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5438/postgres", "postgres",
                    "postgres");
            
            // étape 3: créer l'objet statement
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM roles");

            // étape 4: exécuter la requête
            while (res.next())
                System.out.println(res.getInt(1) + "  " + res.getString(2) + "  " + res.getString(3));

            // étape 5: fermez l'objet de connexion
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}