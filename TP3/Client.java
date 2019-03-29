import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
 
public class Client {
    public static void main(String[] args) throws IOException {
 
        if (args.length < 4) {
             System.err.println("Client arguments must be: <host_name> <remote_object_name> <oper> <opnd>*");
             return;
        }
 
        // prepare request
        String oper = args[2];
        String opnd = "";
        for(int i = 3; i < args.length; i++)
        {
            if(i == args.length - 1)
                opnd += args[i];
            else opnd += args[i] + " ";
        }
        String fstr = oper + " " + opnd;

        String host = args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            LPlate stub = (LPlate) registry.lookup(args[1]);
            String response = stub.request(fstr);
            System.out.println(fstr + " : " + response);
        } catch (Exception e) {
            System.out.println("\n" + fstr + " : EXCEPTION_ERROR");
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
     
    }
}