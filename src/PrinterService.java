package datasecurity_rmi.src;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

import javax.naming.AuthenticationException;


public interface PrinterService extends Remote{
    public String authenticate(String username, String password) throws RemoteException;
    public boolean checkToken(String token) throws RemoteException;
    public boolean print(String token, String filename, String printer) throws RemoteException, AuthenticationException;   // prints file filename on the specified printer
    public LinkedList<String> queue(String token, String printer) throws RemoteException, AuthenticationException;   // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    public boolean topQueue(String token, String printer, int job) throws RemoteException, AuthenticationException;   // moves job to the top of the queue
    public void start() throws RemoteException;   // starts the print server
    public void stop(String token) throws RemoteException, AuthenticationException;   // stops the print server
    public void restart(String token) throws RemoteException, AuthenticationException;   // stops the print server, clears the print queue and starts the print server again
    public String status(String token, String printer) throws RemoteException, AuthenticationException;  // prints status of printer on the user's display
    public String readConfig(String token, String parameter) throws RemoteException, AuthenticationException;   // prints the value of the parameter on the user's display
    public void setConfig(String token, String parameter, String value) throws RemoteException, AuthenticationException;
}
