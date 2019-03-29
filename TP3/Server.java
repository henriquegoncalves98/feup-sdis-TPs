import java.io.*;
import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
 
public class Server implements LPlate {

    protected Map<String, String> db = new HashMap<String, String>();

    public Server() {}

    public String request(String opr) {

        System.out.println("Server processing message: " + opr + "...");

        String dString;
        if((opr.substring(0,8)).equals("register"))
        {
            String plateN = opr.substring(9,17);
            String owner = opr.substring(18);
            //check if it already exists
            dString = "-1";
            if(db.get(plateN) == null)
            {
                //register to database
                db.put(plateN,owner);
                dString = Integer.toString(db.size());
            }
        }
        else if(opr.substring(0,6).equals("lookup"))
        {
            String plateN = opr.substring(7,15);
            if((dString = db.get(plateN)) == null)
                dString = "NOT_FOUND";
        }
        else {
            dString = "ERROR";
        }

        return dString;
    }
 
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
             System.err.println("Server arguments must be: <remote_object_name>");
             return;
        }

        try {
            Server obj = new Server();
            LPlate stub = (LPlate) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(args[0], stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
    
}