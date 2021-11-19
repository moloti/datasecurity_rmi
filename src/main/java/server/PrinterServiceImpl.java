package server;

import database.DatabaseConnector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.naming.AuthenticationException;


public class PrinterServiceImpl extends UnicastRemoteObject implements PrinterService {

    private HashMap<String, String> userMap = new HashMap<>();
    private static UserService userService;
    private String status = "";
    private Map<String, LinkedList> printerMap = new HashMap<>();
    private Map<String, String> parameterMap = new HashMap<>();
    private static int session_deadline = 60;
    public static HashMap<String, List<String>> server_roles = new HashMap<String, List<String>>();
    private boolean ACL;
    private static final String outputFilePath = System.getProperty("user.dir") + "/src/main/resources/";


    public PrinterServiceImpl() throws RemoteException {
        // user initialization
        userService = new UserService();
        DatabaseConnector databaseConnector = new DatabaseConnector();
        userMap = userService.getUserMap();
        // Ask the user what role method they want to go for
        System.out.println(
                "Please enter <ACL> if you want to use Access Control List authorization method or <RBAC> for a Role Based Access Control...");
        Scanner input = new Scanner(System.in);
        String accessPolicy = input.next();
        // Il they choose ACL, we need to go for the acl file where we have the roles,
        // and attach roles to the user_roles variable so that later, we can decide if
        // the user has authorization for a specific operation.
        if (accessPolicy.equalsIgnoreCase("ACL")) {
            System.out.println("Access Control List specified..\nReading ACL file..");
            ACL = true;
            File file = new File(outputFilePath + "acl.txt");
            readAccessFile(file);
        } else if (accessPolicy.equalsIgnoreCase("RBAC")) {
            System.out.println("Role Based Access Control specified..\nReading RBAC file..");
            ACL = false;
            File file = new File(outputFilePath + "rbac.txt");
            readAccessFile(file);
        } else {
            System.out.println("Access policy not known, try again.");
        }
    }



    public HashMap<String, String> getUserMap() throws RemoteException, NotBoundException {
        return userService.getUserMap();
    }

    public String[] getUserRoles(String username) throws RemoteException, NotBoundException {
        return userService.getSpecifiedUserRoles(username);
    }


    public void removeRoles(String username, List<String> roles_to_remove) throws RemoteException, NotBoundException {
        DatabaseConnector database = new DatabaseConnector();
        String user_id = userService.getUserId(username);
        for (int i = 0; i < roles_to_remove.size(); i++) {
            String query = "DELETE FROM user_role WHERE user_id ='" + user_id + "' AND role_id = (SELECT role_id FROM roles WHERE role_name='" + roles_to_remove.get(i) + "')";
            database.delete(query);
        }
        database.close();
    }

    public void addRoles(String username, List<String> roles_to_add) throws RemoteException, NotBoundException {
        DatabaseConnector database = new DatabaseConnector();
        String user_query = "SELECT * FROM users WHERE user_name='" + username + "'";
        ResultSet user_res = database.query(user_query);
        String user_id = null;
        try {
            user_res.next();
            user_id = user_res.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        for (int i = 0; i < roles_to_add.size(); i++) {
            String role_query = "SELECT * FROM roles WHERE role_name='" + roles_to_add.get(i) + "'";
            ResultSet role_res = database.query(role_query);
            try {
                role_res.next();
                String role_id = role_res.getString(1);
                String insert_query = "INSERT INTO user_role (role_id,user_id) VALUES('" + role_id + "','" + user_id + "')";
                database.insert(insert_query);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        database.close();
    }

    private void readAccessFile(File file) {
        BufferedReader br = null;
        try {

            // create BufferedReader object from the File
            br = new BufferedReader(new FileReader(file));

            String line;

            // read file line by line
            while ((line = br.readLine()) != null) {
                if (ACL) {
                    String[] parts = line.split(":");
                    String operation = parts[0].trim();
                    String[] allowed_users_parts = parts[1].trim().split("-");
                    List<String> allowed_users = new ArrayList<String>();
                    for (int i = 0; i < allowed_users_parts.length; i++) {
                        allowed_users.add(allowed_users_parts[i].trim());
                    }
                    server_roles.put(operation, allowed_users);
                } else {
                    String[] parts = line.split(":");
                    String role = parts[0].trim();
                    String[] allowed_operations_parts = parts[1].trim().split("-");
                    List<String> allowed_operations = new ArrayList<String>();
                    for (int i = 0; i < allowed_operations_parts.length; i++) {
                        allowed_operations.add(allowed_operations_parts[i].trim());
                    }
                    server_roles.put(role, allowed_operations);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Always close the BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
                ;
            }
        }
    }

    public static boolean AccessVerificationRBAC(ArrayList<String> current_user_roles, String operation) {
        // We go through all the current user roles, and check if the server roles allow
        // this operation
        for (int i = 0; i < current_user_roles.size(); i++) {
            if (server_roles.get(current_user_roles.get(i)).contains(operation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean AccessVerificationACL(String logged_in_user, String current_operation) {
        // We check if the current operation allows this user
        if (server_roles.get(current_operation).contains(logged_in_user)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean VerifyRole(String operation, String logged_in_user) throws RemoteException, AuthenticationException {
        if (ACL) {
            return AccessVerificationACL(logged_in_user, operation);
        } else {
            // return true if access allowed, otherwise it returns false
            DatabaseConnector database = new DatabaseConnector();
            String query = "SELECT roles.role_name, roles.role_id\n" +
                    "FROM roles\n" +
                    "INNER JOIN user_role ON roles.role_id = user_role.role_id\n" +
                    "INNER JOIN users ON user_role.user_id = users.user_id\n" +
                    "WHERE users.user_name = '" + logged_in_user + "'";
            ArrayList<String> user_roles = new ArrayList<String>();
            try {
                ResultSet res = database.query(query);
                while (res.next()) {
                    user_roles = new ArrayList<String>(Arrays.asList(res.getString(1)));
                }

            } catch (Exception e) {
                System.out.println(e);
            }
            database.close();
            return AccessVerificationRBAC(user_roles, operation);
        }
    }

    @Override
    public String authenticate(String username, String password) throws RemoteException {
        if ((!userMap.isEmpty()) || userMap.containsKey(username)) {
            if (userService.verifyHash(password, userMap.get(username))) {
                String sessionKey = generateSessionKey(new Timestamp(System.currentTimeMillis()));
                userService.addSession(username, sessionKey);
                return sessionKey;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean print(String token, String filename, String printer)
            throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            System.out.println("Print: " + filename + ", with printer: " + printer);
            if (printerMap.containsKey(printer)) {
                LinkedList<String> printerQueue = printerMap.get(printer);
                printerQueue.add(filename);
                return true;
            } else {
                return false;
            }
        } else
            throw new AuthenticationException();
    }

    @Override
    public LinkedList<String> queue(String token, String printer) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            if (printerMap.containsKey(printer)) {
                LinkedList<String> printerQueue = printerMap.get(printer);
                return printerQueue;
            } else {
                return null;
            }
        } else
            throw new AuthenticationException();
    }

    @Override
    public boolean topQueue(String token, String printer, int job) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            if (printerMap.containsKey(printer)) {
                LinkedList<String> printerQueue = printerMap.get(printer);
                if (printerQueue.size() - 1 >= job) {
                    String removedItem = printerQueue.remove(job);
                    printerQueue.addFirst(removedItem);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else
            throw new AuthenticationException();
    }

    @Override
    public void start() throws RemoteException {
        // TODO Auto-generated method stub
        status = "started";
        registerNewPrinter("Printer1");
        registerNewPrinter("Printer2");
        System.out.println("Printer Service Started");
    }

    @Override
    public void stop(String token) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            status = "stopped";
            printerMap = new HashMap<>();
            System.out.println("Printer Service stopped");
            status = "";
        } else
            throw new AuthenticationException();
    }

    @Override
    public void restart(String token) throws RemoteException, AuthenticationException {
        // TODO Auto-generated method stub
        if (checkToken(token)) {
            status = "restarted";
            System.out.println("Printer Service restarted");
            printerMap = new HashMap<>();
            status = "";
            start();
        } else
            throw new AuthenticationException();
    }

    @Override
    public String status(String token, String printer) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            if (printerMap.containsKey(printer)) {
                LinkedList<String> printerQueue = printerMap.get(printer);
                if (printerQueue.isEmpty()) {
                    return "idle";
                } else {
                    System.out.println("Status Reported");
                    return "Printing and currently " + printerQueue.size() + " items in Queue.";
                }
            } else {
                return "idle";
            }
        } else
            throw new AuthenticationException();
    }

    @Override
    public String readConfig(String token, String parameter) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            if (parameterMap.containsKey(parameter)) {
                return parameterMap.get(parameter);
            } else {
                return null;
            }
        } else
            throw new AuthenticationException();
    }

    @Override
    public void setConfig(String token, String parameter, String value)
            throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            parameterMap.put(parameter, value);
        } else
            throw new AuthenticationException();
    }

    private void registerNewPrinter(String printerName) {
        LinkedList<String> printerQueue = new LinkedList<>();
        printerMap.put(printerName, printerQueue);
    }

    private String generateSessionKey(Timestamp timestamp) {
        Random ranGen = new SecureRandom();
        byte[] aesKey = new byte[32];
        ranGen.nextBytes(aesKey);
        Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String token = encoder.encodeToString(aesKey);
        String sessionKey = timestamp + ";" + token;
        return sessionKey;
    }

    @Override
    public boolean checkToken(String token) throws RemoteException {
        System.out.println("Inside Check Session");
        if (token != null && userService.getSessionMap().containsKey(token)) {
            System.out.println("Inside inside check session");
            Calendar cal = Calendar.getInstance();
            String[] keys = token.split(";");
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(keys[0]);
            cal.setTimeInMillis(timestamp.getTime());
            cal.add(Calendar.SECOND, session_deadline);
            Timestamp userSession = new Timestamp(cal.getTime().getTime());
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            System.out.println("Verifying session..");
            return userSession.after(currentTime);
        } else {
            return false;
        }

    }

    public boolean removeUserRoles(String token, String user, List<String> roles) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            try {
                DatabaseConnector database = new DatabaseConnector();
                String user_id = userService.getUserId(user);
                for (String role : roles
                ) {
                    String query = "DELETE FROM user_role WHERE user_id = %s AND role_id = (select role_id from roles where role_name = %s)".formatted(user_id, role, role);
                    System.out.println(query);
                    database.query(query);
                }
                database.close();
                return true;
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }
        } else
            throw new AuthenticationException();
    }

    public boolean addUserRoles(String token, String user, List<String> roles) throws RemoteException, AuthenticationException {
        if (checkToken(token)) {
            DatabaseConnector database = new DatabaseConnector();
            String user_query = "SELECT * FROM users WHERE user_name='" + user + "'";
            ResultSet user_res = database.query(user_query);
            String user_id = null;
            try {
                user_res.next();
                user_id = user_res.getString(1);
            } catch (Exception e) {
                System.out.println(e);
            }
            for (int i = 0; i < roles.size(); i++) {
                String role_query = "SELECT * FROM roles WHERE role_name='" + roles.get(i) + "'";
                ResultSet role_res = database.query(role_query);
                try {
                    role_res.next();
                    String role_id = role_res.getString(1);
                    String insert_query = "INSERT INTO user_role (role_id,user_id) VALUES('" + role_id + "','" + user_id + "')";
                    database.insert(insert_query);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            database.close();
            return true;
        } else
            throw new AuthenticationException();
    }


}
