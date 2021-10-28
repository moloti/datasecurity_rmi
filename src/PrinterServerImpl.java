package datasecurity_rmi.src;

import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.rmi.RemoteException;

public class PrinterServerImpl extends UnicastRemoteObject implements PrinterServer {

    private HashMap<String, String> userMap = new HashMap<>();
    private UserService userService;
    private String status = "";
    private static PrinterServerImpl server;
    private Map<String, LinkedList> printerMap = new HashMap<>();
    private Map<String, String> parameterMap = new HashMap<>();

    public PrinterServerImpl() throws RemoteException {
        userService = new UserService();
        userMap = userService.getUserMap();
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        if((!userMap.isEmpty()) || userMap.containsKey(username)){
           return userService.verifyHash(password, userMap.get(username));
        }else {
           return false;
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

}
