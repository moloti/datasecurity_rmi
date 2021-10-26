package datasecurity_rmi.src;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.RemoteException;

public class PrinterServerImpl extends UnicastRemoteObject implements PrinterServer  {
    private String printerName = "";
    private String fileName = "";
    private ArrayList printerList;
    private String status = "";

    public PrinterServerImpl(String aName) throws RemoteException {
        printerName = aName;
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        printerList.
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
    public void start() throws RemoteException {
        // TODO Auto-generated method stub
        status = "printing";
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
    
}
