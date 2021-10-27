package datasecurity_rmi.server.src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datasecurity_rmi.server.src.resources.BCrypt;

public class User {
    private final static String outputFilePath = "/resources/pass.txt";
    private HashMap<String, String> userMap = new HashMap<String, String>();
    private File file = null;


    public User() {
        if (readUser()) {

        } else {
            createUser("Thomas", "1234");
        }
    }

    private boolean readUser(){
         
         return false;
    }

    private void writeFile(){

        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file, true));
            // iterate map entries
            for (Map.Entry<String, String> entry : userMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":" + entry.getValue());

                // new line
                bf.newLine();
            }

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

    private void createUser(String username, String password) {
        
        userMap.put(username, hash(password));
        
        // if (file.exists() && !file.isDirectory()) {
        if (file == null) {
            file = new File(outputFilePath);
        }
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file, true));
            // iterate map entries
            for (Map.Entry<String, String> entry : userMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":" + entry.getValue());

                // new line
                bf.newLine();
            }

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

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}
