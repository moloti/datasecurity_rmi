package server;

import java.rmi.server.UnicastRemoteObject;
import java.io.FileReader;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.io.BufferedReader;
import java.io.File;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Base64.Encoder;
import java.util.*;


import javax.naming.AuthenticationException;

import java.rmi.RemoteException;

public class PrinterServiceImpl extends UnicastRemoteObject implements PrinterService {

    private HashMap<String, String> userMap = new HashMap<>();
    private UserService userService;
    private String status = "";
    private static PrinterServiceImpl service;
    private Map<String, LinkedList> printerMap = new HashMap<>();
    private Map<String, String> parameterMap = new HashMap<>();
    private static int session_deadline = 60;
    private static HashMap<String, List<String>> server_roles;
    private boolean ACL;
    private static final String outputFilePath = System.getProperty("user.dir")
            + "/src/main/resources/";

    public PrinterServiceImpl() throws RemoteException {
        // user initialization
        userService = new UserService();
        userMap = userService.getUserMap();
        // Ask the user what role method they want to go for
        System.out.println(
                "enter <ACL> if you want to use Access Control List authorization method or <RBAC> for a Role Based Access Crontrol.");
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
            // Otherwise, we need to read the rbac file.
        } else if (accessPolicy.equalsIgnoreCase("RBAC")) {
            System.out.println("Role Based Access Control specified..\nReading RBAC file..");
            ACL = false;
            File file = new File(outputFilePath + "rbac.txt");
            readAccessFile(file);
        } else {
            System.out.println("Access policy not known, try again.");
        }
    }

    private void readAccessFile(File file){
        BufferedReader br = null;

        try {

            // create BufferedReader object from the File
            br = new BufferedReader(new FileReader(file));

            String line = null;

            // read file line by line
            while ((line = br.readLine()) != null) {
                if (ACL){
                    String[] parts = line.split(":");
                    String operation = parts[0].trim();
                    String allowed_users_str = parts[1].trim();
                    String[] allowed_users_parts = allowed_users_str.split("-");
                    // List<String> list = Arrays.asList(new String[]{"foo", "bar"});
                    List<String> allowed_users = new ArrayList<String>();
                    for (int i=0; i<allowed_users_parts.length; i++){
                        allowed_users.add(allowed_users_parts[i].trim());
                    }
                    server_roles.put(operation, allowed_users);
                }
                else{
                    String[] parts = line.split(":");
                    String role = parts[0].trim();
                    String allowed_operations_str = parts[1].trim();
                    String[] allowed_operations_parts = allowed_operations_str.split("-");
                    List<String> allowed_operations = Arrays.asList(new String[allowed_operations_parts.length]);
                    for (int i=0; i<allowed_operations_parts.length; i++){
                        // allowed_operations[i] = allowed_operations_parts[i].trim();
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

    private static boolean AccessVerificiationRBAC(String[] current_user_roles, String operation) {
        // We go through all the current user roles, and check if the server roles allow
        // this operation
        for (int i = 0; i < current_user_roles.length; i++) {
            if (server_roles.get(current_user_roles[i]).contains(operation)) {
                return true;
            }
        }
        return false;
    }

    public boolean VerifyRole(String operation, String logged_in_user) throws RemoteException, AuthenticationException {
        String[] user_roles = userService.getRoles();
        if (ACL) {
            System.out.println("acl");
            return true;
        } else {
            // return true if access allowed, otherwise it returns false
            return AccessVerificiationRBAC(user_roles, operation);
        }
    }

    @Override
    public String authenticate(String username, String password) throws RemoteException {
        if ((!userMap.isEmpty()) || userMap.containsKey(username)) {
            if (userService.verifyHash(password, userMap.get(username))) {
                String sessionkey = generateSessionKey(new Timestamp(System.currentTimeMillis()));
                userService.addSession(sessionkey, username);
                return sessionkey;
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
        String sessionkey = timestamp + ";" + token;
        System.out.println(sessionkey);
        return sessionkey;
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

}
