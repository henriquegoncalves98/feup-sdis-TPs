import java.io.*;
import java.net.*;
import java.util.*;
 
public class MulticastServer extends Thread {

    protected int TIME_OUT = 1000;
    protected DatagramSocket socket = null;
    protected String mcast_addr = null;
    protected String mcast_port = null;
    protected String MulticastCommand = null;
    protected Map<String, String> db = new HashMap<String, String>();
 
    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
             System.err.println("Server arguments must be: <srvc_port> <mcast_addr> <mcast_port>");
             return;
        }

        new MulticastServer(args[0],args[1],args[2]).start();
    }
 
    public MulticastServer(String port, String mcast_addr, String mcast_port) throws IOException {
        super("MulticastServerThread");
        this.mcast_addr = mcast_addr;
        this.mcast_port = mcast_port;

        socket = new DatagramSocket(Integer.parseInt(port));
        //setSocket Time out
        socket.setSoTimeout(TIME_OUT);
        
        //get the initial multicast arguments to the byte buffer to send the receivers clients
        //check hostname with the input 'hostname' in the win cmd(mine for example = "HenriquePC")
        MulticastCommand = "HenriquePC " + port;
        byte[] buf = new byte[256];
        buf = MulticastCommand.getBytes();

        InetAddress group = InetAddress.getByName(mcast_addr);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(mcast_port));
        socket.send(packet);
        System.out.println("Multicast: " + mcast_addr + " " + mcast_port + " : " + MulticastCommand);
 
    }
 
    public void run() {
 
        while (true) {
            try {
                byte[] buf = new byte[256];

                DatagramPacket packet;
                try {
                    // receive request
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                } catch(SocketTimeoutException e) {
                    //Repeat the initial multicast process every 1 sec without answer
                    buf = MulticastCommand.getBytes();

                    InetAddress group = InetAddress.getByName(mcast_addr);
                    packet = new DatagramPacket(buf, buf.length, group, Integer.parseInt(mcast_port));
                    socket.send(packet);
                    System.out.println("Multicast: " + mcast_addr + " " + mcast_port + " : " + MulticastCommand);
                    continue;
                }
 
                // figure out response
                String dString = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server processing message: " + dString + "...");

                if((dString.substring(0,8)).equals("register"))
                {
                    String plateN = dString.substring(9,17);
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
                    String plateN = dString.substring(7,15);

                    if((dString = db.get(plateN)) == null)
                        dString = "NOT_FOUND";
                }
                else {
                    dString = "ERROR";
                }
                
                System.out.println("Server response: " + dString);
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