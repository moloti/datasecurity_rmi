package datasecurity_rmi.src;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {
    public static void main(String[] args) {
        try {
            PrinterServer server = new PrinterServerImpl("");
            Registry reg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            reg.rebind("PrinterServer", server);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
