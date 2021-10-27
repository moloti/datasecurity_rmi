package datasecurity_rmi.server.src;
import java.util.HashMap;
import java.util.List;

import datasecurity_rmi.server.src.resources.BCrypt;

public class User {
    HashMap<String, Integer> hmap = new HashMap<String, Integer>();

    private void createUser(String Username){

    }

    private String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyHash(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
    
}
