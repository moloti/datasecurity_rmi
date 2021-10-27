package datasecurity_rmi.server.src;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class PrinterServerImpl extends UnicastRemoteObject implements PrinterServer {

    private String printerName = "";
    private String fileName = "";
    private ArrayList<String> printerList = new ArrayList<String>();
    private String status = "";

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            PrinterServer server = new PrinterServerImpl("");
            server.registerNewPrinter("Printer1");
            server.registerNewPrinter("Printer2");
            reg.rebind("PrinterServer", server);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public PrinterServerImpl(String aName) throws RemoteException {
        printerName = aName;
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
        status = "printing";
        return status;
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
