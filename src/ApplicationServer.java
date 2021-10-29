package datasecurity_rmi.src;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {
    public static void main(String[] args) {
        try {
            PrinterService server = new PrinterServiceImpl();
            Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            reg.rebind("PrinterService", server);
            server.start();
            System.out.println("Service Started");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
