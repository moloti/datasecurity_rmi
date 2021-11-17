package datasecurity_rmi.src;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import datasecurity_rmi.src.PrinterServiceImpl;
// import datasecurity_rmi.src.RMISSLClientSocketFactory;
// import datasecurity_rmi.src.RMISSLServerSocketFactory;

public class ApplicationServer extends UnicastRemoteObject {

    public ApplicationServer() throws Exception {
        super(Registry.REGISTRY_PORT/* , new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory() */);
    }

    public static void main(String[] args) {

        try {
            // Create SSL-based registry
            Registry registry = LocateRegistry.createRegistry(
                    Registry.REGISTRY_PORT/* , new RMISSLClientSocketFactory(), new RMISSLServerSocketFactory() */);

            PrinterService server = new PrinterServiceImpl();

            // Bind this object instance to the name "HelloServer"
            registry.rebind("PrinterService", server);

            System.out.println("Server Started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
