package datasecurity_rmi.client.src;

import java.rmi.Naming;
import java.util.Scanner;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import datasecurity_rmi.server.src.PrinterServer;

public class MyClient {
   private static PrinterServer printerServer;

   public static void main(String argv[]) {

      try {
         // Start Registry
         Registry reg = LocateRegistry.getRegistry();
         // Get Printer server
         printerServer = (PrinterServer) reg.lookup("PrinterServer");
         MyClient client = new MyClient();

         client.callServer(printerServer);
      } catch (RemoteException | NotBoundException e) {
         System.out.println("Error looking up printer");
         e.printStackTrace();
      }
      System.exit(0);
   }

   private static void callServer(PrinterServer server, String command) {
      try {
         String str = server.start();
         System.out.println(str);
      }
   }

   private static boolean showLoginInfo() {
      Scanner input = new Scanner(System.in);
      System.out.println("--- Hello Client! Please Sign in! ---");
      System.out.print("Enter username: ");
      String username = input.nextLine();

      System.out.print("Enter password: ");
      String password = input.nextLine();

      System.out.print("Authenticating...");

      try {
         if(printerServer.authenticate(username, password)){
            System.out.print("Login successfull!");
            return true;
         } else{
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   } 

   


}
