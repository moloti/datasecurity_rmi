package server;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;

import database.DatabaseConnector;

public class UserService {
    private static final String outputFilePath = System.getProperty("user.dir") + "/src/main/resources/pass.txt";
    private static final File outputFile = new File(outputFilePath);
    private final HashMap<String, String> userMap = new HashMap<>();
    private final HashMap<String, String[]> userRoles = new HashMap<>();
    private final HashMap<String, String> sessionMap = new HashMap<>();
    private static DatabaseConnector database;

    public UserService() {
        System.out.println("Creating users...");
        createUser("Alice", "spain", new String[]{"manager"});
        createUser("Bob", "italy", new String[]{"technician"});
        createUser("Cecilia", "france", new String[]{"powerUser"});
        createUser("David", "germany", new String[]{"user"});
        createUser("Erica", "denmark", new String[]{"user"});
        createUser("Fred", "hungary", new String[]{"user"});
        createUser("George", "finland", new String[]{"user", "technician"});
        createUser("Henry", "sweden", new String[]{"user"});
        createUser("Ida", "norway", new String[]{"powerUser"});
        System.out.println("User Creation Finished");
    }

    public HashMap<String, String> getUserMap() {
        return userMap;
    }

    public HashMap<String, String> getSessionMap() {
        return sessionMap;
    }

    public void addSession(String user, String sessionKey) {
        sessionMap.put(user, sessionKey);
    }

    private void createUser(String username, String password, String[] newRoles) {
        database = new DatabaseConnector();
        String query = "INSERT INTO users (user_name,password) VALUES ('" + username + "','" + password + "')";
        database.query(query);
        ResultSet res = database.query("SELECT * FROM users WHERE user_name=" + username);
        String user_id = null;
        try {
            System.out.println(res.getString(1));
            user_id = res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        String role_id = null;
        ResultSet res = database.query("SELECT * FROM roles WHERE role_name=" + "username");
        try {
            System.out.println(res.getString(1));
            role_id = res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        String role_query = "INSERT INTO roles (role_id, user_id) VALUES ('"+role_id+"',"+user_id+"')";
        database.close();
        userRoles.put(username, newRoles);
        userMap.put(username, hash(password));
        writeFile();
    }

    public String[] getUserRoles() {
        return userRoles.get(sessionMap.keySet().toArray()[0]);
    }

    public String[] getSpecifiedUserRoles(String username) {
        return userRoles.get(username);
    }

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    private void writeFile() {
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(outputFile, true));
            // iterate map entries
            for (HashMap.Entry<String, String> entry : userMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":" + entry.getValue());

                // new line
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                // always close the writer
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
