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
      boolean cancel = false;
      boolean authenticated = false;

      try {
         // Start Registry
         Registry reg = LocateRegistry.getRegistry();
         // Get Printer server
         server = (PrinterServer) reg.lookup("PrinterServer");
         System.out.println("Client Connected to Server");

         while (!cancel) {
            authenticated = false;
            while (!authenticated)
               authenticated = showLoginInfo();
            cancel = chooseAction();
         }

      } catch (RemoteException | NotBoundException e) {
         System.out.println("Error looking up printer");
         e.printStackTrace();
      }
      System.exit(0);
   }

   private static void callServer(String command) {
      try {
         server.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static boolean chooseAction() {
      int selection;
      Scanner input = new Scanner(System.in);

      System.out.println("Please choose an action:");
      System.out.println("-------------------------\n");
      System.out.println("1 - Print something");
      System.out.println("2 - Show the printing queue for a certain printer");
      System.out.println("3 - Move Job to the top of the Queue");
      System.out.println("4 - Restart print server");
      System.out.println("5 - Get Printer Configuration");
      System.out.println("6 - Set Printer Configuration");
      System.out.println("7 - Quit");

      selection = input.nextInt();

      switch (selection) {
      case 1:
      System.out.println("What do you want to print?");
      try {
         server.print("filename", "printer");
      } catch (Exception e) {
         //TODO: handle exception
      }
      
         break;
      case 2:

         break;
      case 3:

         break;
      case 4:

         break;
      case 5:

         break;
      case 6:

         break;
      case 7:

         break;

      default:
         break;
      }

      return false;
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
         } else {
            System.out.println("Access Denied");
            return false;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

}
