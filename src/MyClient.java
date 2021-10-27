package datasecurity_rmi.src;

import java.rmi.Naming;
import java.util.Scanner;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MyClient {
   private static PrinterServer server;

   public static void main(String[] args) throws Exception {
      boolean cancle = false;
      boolean authenticated = false;

      try {
         // Start Registry
         Registry reg = LocateRegistry.getRegistry();
         // Get Printer server
         server = (PrinterServer) reg.lookup("PrinterServer");
         System.out.println("Client Connected to Server");

         while (!cancle) {
            authenticated = false;
            while (!authenticated)
               authenticated = showLoginInfo();
         }

      } catch (RemoteException | NotBoundException e) {
         System.out.println("Error looking up printer");
         e.printStackTrace();
      }
      System.exit(0);
   }

   private static void callServer(String command) {
      try {
         String str = server.start();
         System.out.println(str);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static boolean showLoginInfo() {
      Scanner input = new Scanner(System.in);
      System.out.println("--- Hello Client! Please Sign in! ---");
      System.out.println("Enter username: ");
      String username = input.nextLine();

      System.out.println("Enter password: ");
      String password = input.nextLine();

      System.out.println("Authenticating...");

      try {
         if (server.authenticate(username, password)) {
            System.out.println("Login successfull!");
            return true;
         }else{
            System.out.println("Access Denied");
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

}
