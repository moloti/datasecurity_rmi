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
        database = new DatabaseConnector();
        database.query("DELETE FROM user_role;\n" +
                "DELETE FROM transaction_user;\n" +
                "DELETE FROM users;\n" +
                "DELETE FROM role_transaction;\n");
        database.close();
        System.out.println("Creating users...");
        createUser("Alice", "spain", new String[]{"manager"});
        createUser("Bob", "italy", new String[]{"technician"});
        createUser("Cecilia", "france", new String[]{"powerUser"});
        createUser("David", "germany", new String[]{"user"});
        createUser("Erica", "denmark", new String[]{"user"});
        createUser("Fred", "hungary", new String[]{"user"});
        createUser("George", "finland", new String[]{"user"});
        System.out.println("User Creation Finished");

        fillRolesTransaction("manager", new String[]{"print", "queue", "topQueue", "start", "stop", "restart", "status", "readConfig", "setConfig", "manageEmployees"});
        fillRolesTransaction("powerUser", new String[]{"print", "queue", "topQueue", "restart"});
        fillRolesTransaction("user", new String[]{"print", "queue"});
        fillRolesTransaction("technician", new String[]{"start", "stop", "restart", "status", "readConfig", "setConfig"});

        fillTransactionUsers("print", new String[]{"Alice", "Cecilia", "David", "Erica", "Fred", "George"});
        fillTransactionUsers("queue", new String[]{"Alice", "Cecilia", "David", "Erica", "Fred", "George"});
        fillTransactionUsers("topQueue", new String[]{"Alice", "Cecilia"});
        fillTransactionUsers("start", new String[]{"Alice"});
        fillTransactionUsers("stop", new String[]{"Alice"});
        fillTransactionUsers("restart", new String[]{"Alice", "Cecilia"});
        fillTransactionUsers("status", new String[]{"Alice"});
        fillTransactionUsers("readConfig", new String[]{"Alice"});
        fillTransactionUsers("setConfig", new String[]{"Alice"});
        fillTransactionUsers("manageEmployees", new String[]{"Alice"});
    }

    private void fillTransactionUsers(String transaction, String[] initial_users) {
        database = new DatabaseConnector();
        String query = "SELECT * FROM transactions where transaction_name='" + transaction + "'";
        ResultSet res = database.query(query);
        String transaction_id = null;
        try {
            res.next();
            transaction_id = res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        String user_id = null;
        try {
            for (int i = 0; i < initial_users.length; i++) {
                String query_t = "SELECT * FROM users where user_name='" + initial_users[i] + "'";
                ResultSet res_t = database.query(query_t);
                res_t.next();
                user_id = res_t.getString(1);

                String insert_query = "INSERT INTO transaction_user (transaction_id,user_id) VALUES('" + transaction_id + "','" + user_id + "')";
                database.insert(insert_query);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        database.close();
    }

    private void fillRolesTransaction(String role, String[] initial_transactions) {
        database = new DatabaseConnector();
        String query = "SELECT * FROM roles where role_name='" + role + "'";
        ResultSet res = database.query(query);
        String role_id = null;
        try {
            res.next();
            role_id = res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        String transaction_id = null;
        try {
            for (int i = 0; i < initial_transactions.length; i++) {
                String query_t = "SELECT * FROM transactions where transaction_name='" + initial_transactions[i] + "'";
                ResultSet res_t = database.query(query_t);
                res_t.next();
                transaction_id = res_t.getString(1);

                String insert_query = "INSERT INTO role_transaction (role_id,transaction_id) VALUES('" + role_id + "','" + transaction_id + "')";
                database.insert(insert_query);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        database.close();
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
        // Create the user
        String query = "INSERT INTO users (user_name,password) VALUES ('" + username + "','" + password + "')";
        database.insert(query);
        // Manage the roles
        ResultSet res = database.query("SELECT * FROM users WHERE user_name='" + username + "'");

        String user_id = null;
        try {
            res.next();
            user_id = res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        String role_id = null;
        ResultSet res_role = null;
        for (int i = 0; i < newRoles.length; i++) {
            res_role = database.query("SELECT * FROM roles WHERE role_name='" + newRoles[i] + "'");
            try {
                res_role.next();
                role_id = res_role.getString(1);
                String role_query = "INSERT INTO user_role (role_id, user_id) VALUES ('" + role_id + "','" + user_id + "')";
                database.insert(role_query);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
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
