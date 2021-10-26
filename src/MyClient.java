package datasecurity_rmi.src;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;


public class MyClient {
   public static void main(String argv[]) {
         // set the RMI Security Manager, in case we need to load remote
         // classes
         System.setSecurityManager(new RMISecurityManager());

         String url = "rmi://osprey.unf.edu/";   // server
         try {
         // Lookup account object
         PrinterServer printer1 = (PrinterServer)Naming.lookup(url + "Printer1");        
         //Make a deposit
         printer1.print("NewDocument", "Printer1");

   }
     catch (Exception e) {               
     System.out.println("Error looking up printer");
        e.printStackTrace();
     }
     System.exit(0);
   }

}
