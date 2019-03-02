import java.io.*;
import java.net.*;
import java.util.*;
 
public class Server extends Thread {

    protected DatagramSocket socket = null;
    protected Map<String, String> db = new HashMap<String, String>();
 
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
             System.err.println("Server arguments must be: <port_number>");
             return;
        }

        new Server(args[0]).start();
    }
 
    public Server(String port) throws IOException {
        super("ServerThread");
        socket = new DatagramSocket(Integer.parseInt(port));
 
    }
 
    public void run() {
 
        while (true) {
            try {
                byte[] buf = new byte[256];
 
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
 
                // figure out response
                String dString = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server processing message: " + dString + "...");

                if((dString.substring(0,8)).equals("register"))
                {
                    String plateN = dString.substring(9,16);
                    String owner = dString.substring(18);
                    //check if it already exists
                    dString = "-1";
                    if(db.get(plateN) == null)
                    {
                        //register to database
                        db.put(plateN,owner);
                        dString = Integer.toString(db.size());
                    }
                }
                else if(dString.substring(0,6).equals("lookup"))
                {
                    String plateN = dString.substring(7,14);
                    if((dString = db.get(plateN)) == null)
                        dString = "NOT_FOUND";
                }
                else {
                    dString = "ERROR";
                }

                buf = dString.getBytes();
 
                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        socket.close();
    }
    
}