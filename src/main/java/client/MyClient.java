package client;

import java.net.InetAddress;
import java.util.*;

import javax.naming.AuthenticationException;

import server.PrinterService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class MyClient {
    private static PrinterService server;
    private static String session;
    private static String logged_in_username;

    public static void main(String[] args) throws Exception {
        boolean cancel = false;
        boolean authenticated;

        try {
            // Make reference to SSL-based registry
            Registry reg = LocateRegistry.getRegistry(InetAddress.getLocalHost().getHostName(),
                    Registry.REGISTRY_PORT/* , new RMISSLClientSocketFactory() */);

            // Get Printer server
            server = (PrinterService) reg.lookup("PrinterService");
            System.out.println("Client Connected to Printer Service");

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
        System.out.println("Enter username:");
        String username = input.nextLine();
        System.out.println("Enter password:");
        String password = input.nextLine();

        System.out.println("Authenticating...");

        try {
            session = server.authenticate(username, password);
            if (session != null) {
                System.out.println("Login successfull!");
                logged_in_username = username;
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
        System.out.println("8 - ManageEmployees");
        System.out.println("9 - Quit");

        selection = Integer.parseInt(input.nextLine());

        switch (selection) {
            // prints file filename on the specified printer
            case 1:
                try {
                    boolean access = server.VerifyRole("print", logged_in_username);
                    if (access) {
                        print(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // lists the print queue for a given printer on the user's display in lines of
            // the form <job number> <file name>
            case 2:
                try {
                    boolean access = server.VerifyRole("queue", logged_in_username);
                    if (access) {
                        queue(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // moves job to the top of the queue
            case 3:
                try {
                    boolean access = server.VerifyRole("topQueue", logged_in_username);
                    if (access) {
                        topQueue(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // stops the print server, clears the print queue and starts the print server
            // again
            case 4:
                try {
                    boolean access = server.VerifyRole("restart", logged_in_username);
                    if (access) {
                        restart(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // prints status of printer on the user's display
            case 5:
                try {
                    boolean access = server.VerifyRole("status", logged_in_username);
                    if (access) {
                        status(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // prints the value of the parameter on the user's display
            case 6:
                try {
                    boolean access = server.VerifyRole("readConfig", logged_in_username);
                    if (access) {
                        readConfig(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // sets the printer configuration in parameters
            case 7:
                try {
                    boolean access = server.VerifyRole("setConfig", logged_in_username);
                    if (access) {
                        setConfig(input);
                    } else {
                        System.out.println("You are not authorized to perform this action");
                        chooseAction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            // cancel the selection
            case 8:
                ManageEmployees(input);
                break;
            case 9:
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

    private static void ManageEmployees(Scanner input) {
        System.out.println("Please choose your action");
        int selection;
        Scanner input_management = new Scanner(System.in);

        System.out.println("Please choose an action:");
        System.out.println("-------------------------\n");
        System.out.println("1 - Fire someone");
        System.out.println("2 - Hire someone");
        System.out.println("3 - Change someone's access");

        selection = Integer.parseInt(input_management.nextLine());

        switch (selection) {
            // Fire someone
            case 1:
                // delete user
                // case of all users
                // server.delete(user)
                break;
            case 2:
                // Hire someone
                Scanner hire_input = new Scanner(System.in);
                System.out.println("Enter username of new employee:");
                String username = hire_input.nextLine();
                System.out.println("Enter password of new employee:");
                String password = hire_input.nextLine();
                System.out.println("Choose the roles of the new employee:");
                boolean quit_selection = true;
                List<String> roles = new ArrayList<String>();
                while (quit_selection) {
                    Scanner role_input = new Scanner(System.in);
                    System.out.println("1 - manager");
                    System.out.println("2 - technician");
                    System.out.println("3 - powerUser");
                    System.out.println("4 - user");
                    System.out.println("5 - No more roles");
                    int role_selection = Integer.parseInt(role_input.nextLine());
                    switch (role_selection) {
                        case 1:
                            roles.add("manager");
                            break;
                        case 2:
                            roles.add("technician");
                            break;
                        case 3:
                            roles.add("powerUser");
                            break;
                        case 4:
                            roles.add("user");
                            break;
                        case 5:
                            quit_selection = false;
                            break;
                        default:
                            break;
                    }
                }
                // server.hireEmployee(username, password, roles);
                break;
            case 3:
                HashMap<String, String> userMap = null;
                HashMap<String, String> userRoles = null;
                try {
                    int user_selection;
                    Scanner user_input = new Scanner(System.in);
                    userMap = server.getUserMap();
                    System.out.println("Please choose the concerned employee");
                    System.out.println("-------------------------\n");
                    List keyList = List.copyOf(userMap.keySet());
                    for (int i = 0; i < keyList.size(); i++) {
                        System.out.println(i + " - " + keyList.get(i));
                    }
                    user_selection = Integer.parseInt(user_input.nextLine());
                    List<String> role_of_chosen_user = null;
                    try {
                        String chosen_user = keyList.get(user_selection).toString();
                        role_of_chosen_user = new ArrayList<>(Arrays.asList(server.getUserRoles(chosen_user)));
                    } catch (RemoteException | NotBoundException e) {
                        System.out.println("Error");
                        e.printStackTrace();
                    }

                    // REMOVE ROLES
                    int role_remove_selection;
                    Scanner role_input_remove = new Scanner(System.in);

                    System.out.println("Please select roles to remove:");
                    boolean not_finished_remove = true;
                    List<String> roles_to_remove = new ArrayList<String>();
                    List<String> ROLES_REMOVE = role_of_chosen_user;
                    while (not_finished_remove) {
                        for (int i = 0; i < ROLES_REMOVE.size(); i++) {
                            System.out.println(i + " - " + ROLES_REMOVE.get(i));

                        }
                        System.out.println(ROLES_REMOVE.size() + " - I am done");
                        role_remove_selection = Integer.parseInt(role_input_remove.nextLine());
                        if (role_remove_selection == ROLES_REMOVE.size()) {
                            not_finished_remove = false;
                        } else {
                            roles_to_remove.add(ROLES_REMOVE.get(role_remove_selection));
                            ROLES_REMOVE.remove(role_of_chosen_user.get(role_remove_selection));

                        }
                    }
                    // Now remove from database the roles in roles_to_remove

                    // ADD ROLES
                    int role_add_selection;
                    Scanner role_input_add = new Scanner(System.in);
                    String[] real_roles = {"manager", "technician", "powerUser", "user"};
                    List<String> list_real_roles = new ArrayList<String>(Arrays.asList(real_roles));
                    for (int k = 0; k < role_of_chosen_user.size(); k++) {
                        list_real_roles.remove(role_of_chosen_user.get(k));
                    }
                    System.out.println("Please select a role to add:");
                    boolean not_finished_add = true;
                    List<String> roles_to_add = new ArrayList<String>();
                    List<String> ROLES_ADD = list_real_roles;
                    while (not_finished_add) {
                        for (int i = 0; i < ROLES_ADD.size(); i++) {
                            System.out.println(i + " - " + ROLES_ADD.get(i));

                        }
                        System.out.println(ROLES_ADD.size() + " - I am done");
                        role_add_selection = Integer.parseInt(role_input_add.nextLine());
                        if (role_add_selection == ROLES_ADD.size()) {
                            not_finished_add = false;
                        } else {
                            roles_to_add.add(ROLES_ADD.get(role_add_selection));
                            ROLES_ADD.remove(list_real_roles.get(role_add_selection));
                        }
                    }
                    // Now add from database the roles in roles_to_ad

                } catch (RemoteException | NotBoundException e) {
                    System.out.println("Error");
                    e.printStackTrace();
                }
            default:
                break;

        }
    }
}
