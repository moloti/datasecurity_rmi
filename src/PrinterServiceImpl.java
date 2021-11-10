package datasecurity_rmi.src;

import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Base64.Encoder;

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

    public PrinterServiceImpl() throws RemoteException {
        // user initialization
        userService = new UserService();
        userMap = userService.getUserMap();
        System.out.println(
                "enter <ACL> if you want to use Access Control List authorization method or <RBAC> for a Role Based Access Crontrol.");
        Scanner input = new Scanner(System.in);
        String accessPolicy = input.next();
        if (accessPolicy.equalsIgnoreCase("ACL")) {
            System.out.println("Access Control List specified..\nReading ACL file..");
            ACL = true;
            policy = 1;
            readFile(new File("acl.yml"));
        }
        else if (accessPolicy.equalsIgnoreCase("RBAC")) {
            System.out.println("Role Based Access Control specified..\nReading RBAC file..");
            ACL = false;
            policy = 1;
            readFile(new File("rbac.yml"));
        }
        else {
            System.out.println("Unknown access policy..\nEnter either 'ACL' or 'RBAC'");
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
