package datasecurity_rmi.src;

import java.rmi.Naming;
import java.util.LinkedList;
import java.util.Scanner;

import javax.naming.AuthenticationException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MyClient {
   private static PrinterService server;
   private static String session;

   public static void main(String[] args) throws Exception {
      boolean cancel = false;
      boolean authenticated = false;

      try {
         // Start Registry
         Registry reg = LocateRegistry.getRegistry();
         // Get Printer server
         server = (PrinterService) reg.lookup("PrinterServer");
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

   private static boolean showLoginInfo() {
      Scanner input = new Scanner(System.in);
      try {
         if (session != null) {
            System.out.println("Checking session...");
            if (session != null && server.checkToken(session)) {
               System.out.println("Session valid!");
               return true;
            }
         }
      } catch (Exception e) {
         // TODO: handle exception
      }

      System.out.println("--- Hello Client! Please Sign in! ---");
      System.out.println("Enter username: ");
      String username = input.nextLine();

      System.out.println("Enter password: ");
      String password = input.nextLine();

      System.out.println("Authenticating...");

      try {
         session = server.authenticate(username, password);
         System.out.println(session);
         if (session != null) {
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

   private static boolean chooseAction() {
      int selection;
      Scanner input = new Scanner(System.in);

      System.out.println("Please choose an action:");
      System.out.println("-------------------------\n");
      System.out.println("1 - Print something");
      System.out.println("2 - Show the printing queue for a certain printer");
      System.out.println("3 - Move Job to the top of the Queue");
      System.out.println("4 - Restart print server");
      System.out.println("5 - Restart print server");
      System.out.println("6 - Get Printer Configuration");
      System.out.println("7 - Set Printer Configuration");
      System.out.println("8 - Quit");

      selection = Integer.parseInt(input.nextLine());

      switch (selection) {
      // prints file filename on the specified printer
      case 1:
         print(input);
         break;
      // lists the print queue for a given printer on the user's display in lines of
      // the form <job number> <file name>
      case 2:
         queue(input);
         break;
      // moves job to the top of the queue
      case 3:
         topQueue(input);
         break;
      // stops the print server, clears the print queue and starts the print server
      // again
      case 4:
         restart(input);
         break;
      // prints status of printer on the user's display
      case 5:
         status(input);
         break;
      // prints the value of the parameter on the user's display
      case 6:
         readConfig(input);
         break;
      // sets the printer configuration in parameters
      case 7:
         setConfig(input);
         break;
      // cancel the selection
      case 8:
         break;
      default:
         break;
      }

      return false;
   }

   private static void print(Scanner input) {
      System.out.println("What file do you want to print?");
      System.out.println("Please enter file name:");
      String filename = input.nextLine();
      System.out.println("Please enter target printer:");
      String printername = input.nextLine();
      try {
         if (server.print(session, filename, printername)) {
            System.out.println("Printer: " + printername + ", prints: " + filename);
         } else {
            System.out.println("Printer doesn't exist, please try again!");
         }
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void queue(Scanner input) {
      System.out.println("Please enter printer to receive printing queue:");
      String printername = input.nextLine();
      try {
         LinkedList<String> queue = server.queue(session, printername);
         if (queue != null) {
            System.out.println("Current Queue: \n" + queue);
         } else {
            System.out.println("Printer doesn't exist!");
         }

      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private static void topQueue(Scanner input) {
      System.out.println("Please enter target printer:");
      String printername = input.nextLine();
      System.out.println("Please enter job number:");
      int job = input.nextInt();

      try {
         if (server.topQueue(session, printername, job)) {
            System.out.println("Priority successfully changed");
         } else {
            System.out.println("Printer doesn't exist, please try again!");
         }
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void restart(Scanner input) {
      System.out.println("Printer Server is restarting...");
      try {
         server.restart(session);
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private static void status(Scanner input) {
      System.out.println("Please enter target printer:");
      String printername = input.nextLine();
      try {
         String status = server.status(session, printername);
         if (status != null) {
            System.out.println("Status of printer: \n" + status);
         } else {
            System.out.println("Printer doesn't exist!");
         }
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   private static void readConfig(Scanner input) {
      System.out.println("Please enter target parameter:");
      String parameter = input.nextLine();

      try {
         String status = server.readConfig(session, parameter);
         if (status != null) {
            System.out.println("Value of parameter " + parameter + ": \n" + status);
         } else {
            System.out.println("Parameter doesn't exist!");
         }
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void setConfig(Scanner input) {
      System.out.println("Please enter target parameter:");
      String parameter = input.nextLine();
      System.out.println("Please enter new value for Parameter " + parameter + " :");
      String value = input.nextLine();

      try {
         server.setConfig(session, parameter, value);
         System.out.println("Parameter successfully changed!");
      } catch (AuthenticationException ae) {
         System.out.println("You are not authenticated! Please login in again!");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}
