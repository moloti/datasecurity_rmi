package datasecurity_rmi.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class UserService {
    private static final String outputFilePath = System.getProperty("user.dir")
            + "/datasecurity_rmi/src/resources/pass.txt";
    private HashMap<String, String> userMap = new HashMap<>();
    private HashMap<String, String> sessionMap = new HashMap<>();
    private String[] roles = null;
    private File file = null;

    public UserService() {
        if (file != null) {
            if (file.exists() && !file.isDirectory()) {
                readUserMap();
            } else {
                createUser("Alice", "spain", new String[] { "manager" });
                // createUser("Bob", "italy", new String[] { "technician" });
                createUser("Cecilia", "france", new String[] { "poweruser" });
                createUser("David", "germany", new String[] { "user" });
                createUser("Erica", "denmark", new String[] { "user" });
                createUser("Fred", "hungary", new String[] { "user" });
                createUser("George", "finland", new String[] { "user", "technician" });
                createUser("Henyr", "sweden", new String[] { "user" });
                createUser("Ida", "norway", new String[] { "poweruser" });
                // Bob leaves the company, George takes over his duties.
                // Henry and ida recruited -> Henry is user, Ida is a poweruser

                // MANAGER -> all operations ---- Alice is managing the print server, so she has
                // the rights to perform all operations
                // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
                // TECHNICIAN -> start, stop restart print server, inspect and change service
                // parameters, invoke status, redConfig and setConfig operations ------
                // Bob is the janitor who doubles as service technician, he has the rights to
                // START,
                // STOP and RESTART the print server as well as inspect and modify the service
                // parameters, i.e., invoke the status, readConfig and setConfig operations
                // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
                // POWER USER -> print files, manage print queue (queue, topQueue, restart print
                // server) ----- Cecilia is a power user, who is allowed to print files and
                // manage the print queue, i.e., use queue and topQueue as well as restart the
                // print server when everything seems to be stuck
                // ––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
                // USER -> print files and display print queue ---- David, Erica, Fred and
                // George are ordinary users who are only allowed to print files and display the
                // print queue.

            }
        } else {
            System.out.println(outputFilePath);
            createUser("Thomas", "1234", new String[] { "manager" });
            System.out.println("User Creation Finished");
        }
    }

    public HashMap<String, String> getUserMap() {
        return userMap;
    }

    public HashMap<String, String> getSessionMap() {
        return sessionMap;
    }

    public void addSession(String user, String sessionkey) {
        sessionMap.put(user, sessionkey);
    }

    private void readUserMap() {
        if (file == null) {
            readFile(file);
        }
    }

    public void createUser(String username, String password, String[] newRoles) {

        userMap.put(username, hash(password));
        System.out.println("Create User");
        if (file == null) {
            file = new File(outputFilePath);
        }
        roles = newRoles;
        writeFile(file);
    }

    public String[] getRoles() {
        return roles;
    }

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    private void writeFile(File file) {
        System.out.println("Inside Write File");
        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file, true));
            // iterate map entries
            for (HashMap.Entry<String, String> entry : userMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":" + entry.getValue());

                // new line
                bf.newLine();
            }
            System.out.println("Done Writing Before Flush");
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                // always close the writer
                bf.close();
            } catch (Exception e) {
            }
        }
    }

    private void readFile(File file) {
        BufferedReader br = null;

        try {

            // create BufferedReader object from the File
            br = new BufferedReader(new FileReader(file));

            String line = null;

            // read file line by line
            while ((line = br.readLine()) != null) {

                // split the line by :
                String[] parts = line.split(":");

                // first part is name, second is number
                String name = parts[0].trim();
                String pass = parts[1].trim();

                // put name, number in HashMap if they are
                // not empty
                if (!name.equals("") && !pass.equals(""))
                    userMap.put(name, pass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Always close the BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
                ;
            }
        }
    }

}
