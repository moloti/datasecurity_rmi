package datasecurity_rmi.src;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class MyClient {
   public static void main(String argv[]) {
         // set the RMI Security Manager, in case we need to load remote
         // classes
         System.setSecurityManager(new RMISecurityManager());

         try {
         // Lookup account object
         Registry reg = LocateRegistry.getRegistry();
         PrinterServer printerServer = (PrinterServer) reg.lookup("PrinterServer");        
         //Make a deposit
         printerServer.print("NewDocument", "Printer1");

   }
     catch (RemoteException | NotBoundException e) {               
     System.out.println("Error looking up printer");
        e.printStackTrace();
     }
     System.exit(0);
   }

   public void callServer(){
      try {
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}


