package datasecurity_rmi.client.src;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import datasecurity_rmi.server.src.PrinterServer;

public class MyClient {
   private static PrinterServer printerServer;
   public static void main(String argv[]) {

      try {
         // Lookup account object
         Registry reg = LocateRegistry.getRegistry();
         printerServer = (PrinterServer) reg.lookup("PrinterServer");
         // Make a deposit
         MyClient client = new MyClient();
         client.callServer(printerServer);
      } catch (RemoteException | NotBoundException e) {
         System.out.println("Error looking up printer");
         e.printStackTrace();
      }
      System.exit(0);
   }

   public void callServer(PrinterServer server) {
      try {
         String str = server.start();
         System.out.println(str);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
