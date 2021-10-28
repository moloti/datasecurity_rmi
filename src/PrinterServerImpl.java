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
import java.rmi.RemoteException;

public class PrinterServerImpl extends UnicastRemoteObject implements PrinterServer {

    private HashMap<String, String> userMap = new HashMap<>();
    private UserService userService;
    private String status = "";
    private static PrinterServerImpl server;
    private Map<String, LinkedList> printerMap = new HashMap<>();
    private Map<String, String> parameterMap = new HashMap<>();
    private static int session_deadline = 30;

    public PrinterServerImpl() throws RemoteException {
        userService = new UserService();
        userMap = userService.getUserMap();
    }

    @Override
    public String authenticate(String username, String password) throws RemoteException {
        if((!userMap.isEmpty()) || userMap.containsKey(username)){
            if(userService.verifyHash(password, userMap.get(username))){
                String sessionkey = generateSessionKey(new Timestamp(System.currentTimeMillis()));
                userService.addSession(sessionkey, username);
                return sessionkey;
            }else{
                return null;
            }
        }else {
           return null;
        }
    }

    @Override
    public boolean print(String filename, String printer) throws RemoteException {
        System.out.println("Print: " + filename + ", with printer: " + printer);
        if(printerMap.containsKey(printer)){
            LinkedList<String> printerQueue = printerMap.get(printer);
            printerQueue.add(filename);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public LinkedList<String> queue(String printer) throws RemoteException {
        if(printerMap.containsKey(printer)){
            LinkedList<String> printerQueue = printerMap.get(printer);
            return printerQueue;
        }else {
            return null;
        }
    }

    @Override
    public boolean topQueue(String printer, int job) throws RemoteException {
        if(printerMap.containsKey(printer)){
            LinkedList<String> printerQueue = printerMap.get(printer);
            if(printerQueue.size()-1 >= job){
                String removedItem = printerQueue.remove(job);
                printerQueue.addFirst(removedItem);
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }

    }

    @Override
    public void start() throws RemoteException {
        // TODO Auto-generated method stub
        status = "started";
        registerNewPrinter("Printer1");
        registerNewPrinter("Printer2");
        System.out.println("Server Started");
    }

    @Override
    public void stop() throws RemoteException {
        // TODO Auto-generated method stub
        status = "stopped";
        printerMap = new HashMap<>();
        System.out.println("Server stopped");
        status = "";
    }

    @Override
    public void restart() throws RemoteException {
        // TODO Auto-generated method stub
        status = "restarted";
        System.out.println("Server restarted");
        printerMap = new HashMap<>();
        status = "";
        start();
    }

    @Override
    public String status(String printer) throws RemoteException {
        if(printerMap.containsKey(printer)){
            LinkedList<String> printerQueue = printerMap.get(printer);
            if(printerQueue.isEmpty()){
                return "idle";
            }else{
                System.out.println("Status Reported");
                return "Printing and currently " + printerQueue.size() + " items in Queue.";
            }
        }else {
            return "idle";
        }
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        if(parameterMap.containsKey(parameter)){
            return parameterMap.get(parameter);
        }else{
            return null;
        }
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        parameterMap.put(parameter, value);
    }

    @Override
    public void registerNewPrinter(String printerName) {
        LinkedList<String> printerQueue = new LinkedList<>();
        printerMap.put(printerName, printerQueue);
    }

    private String generateSessionKey(Timestamp timestamp){
        Random ranGen = new SecureRandom();
        byte[] aesKey = new byte[32];
        ranGen.nextBytes(aesKey);
        Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        String token = encoder.encodeToString(aesKey);
        String sessionkey =  timestamp + ";" + token;
        System.out.println(sessionkey);
        return sessionkey;
    }

    @Override
    public boolean checkSession(String session) throws RemoteException {
        System.out.println("Inside Check Session");
        if (session != null && userService.getSessionMap().containsKey(session)){
            System.out.println("Inside inside check session");
            Calendar cal = Calendar.getInstance();
            String[] keys = session.split(";");
            java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(keys[0]);
            cal.setTimeInMillis(timestamp.getTime());
            cal.add(Calendar.SECOND, session_deadline);
            Timestamp userSession = new Timestamp(cal.getTime().getTime());
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            System.out.println("Verifying session..");
            return userSession.after(currentTime);
        }else{
            return false;
        }
        
    }

}
