import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LPlate extends Remote {
    String request(String opr) throws RemoteException;
}