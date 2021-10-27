package datasecurity_rmi.server.src;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.rmi.RemoteException;

public class PrinterServerImpl extends UnicastRemoteObject implements PrinterServer {

    private String printerName = "";
    private String fileName = "";
    private ArrayList<String> printerList = new ArrayList<>();
    private HashMap<String, String> userMap = new HashMap<>();
    private UserService userService;
    private String status = "";
    private static PrinterServerImpl server;

    public PrinterServerImpl(String aName) throws RemoteException {
              printerName = aName;
              userService = new UserService();
              userMap = userService.getUserMap();
    }

    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        
        return false;
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        System.out.println(filename + " " + printer);
    }

    @Override
    public void queue(String printer) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public String start() throws RemoteException {
        // TODO Auto-generated method stub
        status = "started";
        registerNewPrinter("Printer1");
        registerNewPrinter("Printer2");
        return "Server Started";
    }

    @Override
    public void stop() throws RemoteException {
        // TODO Auto-generated method stub
        status = "idle";
    }

    @Override
    public void restart() throws RemoteException {
        // TODO Auto-generated method stub
        status = "printing";

    }

    @Override
    public void status(String printer) throws RemoteException {
        System.out.println("The printer:" + printer + "is " + status);

    }

    @Override
    public void readConfig(String parameter) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerNewPrinter(String printerName) {
        printerList.add(printerName);
    }

}
