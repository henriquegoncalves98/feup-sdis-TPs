import java.io.*;
import java.net.*;
import java.util.*;
 
public class MulticastClient {
    public static void main(String[] args) throws IOException {
 
        if (args.length < 4) {
             System.err.println("Client arguments must be: <mcast_addr> <mcast_port> <oper> <opnd>*");
             return;
        }

        //first we need to get the server port and ip which it is serving by getting it with a multicastsocket
        // get a multicast socket and join it to a ip group(e.g. 230.0.0.1)
        MulticastSocket Msocket = new MulticastSocket(Integer.parseInt(args[1]));
        InetAddress address = InetAddress.getByName(args[0]);
        Msocket.joinGroup(address);

        DatagramPacket packet;

        byte[] buf = new byte[256];
        packet = new DatagramPacket(buf, buf.length);
        Msocket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Multicast: " + args[0] + " " + args[1] + " : " + received);
        String [] sResponse = received.split(" ");
        Msocket.leaveGroup(address);
        Msocket.close();
        
        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
 
        // send request
        buf = new byte[256];
        String oper = args[2];
        String opnd = "";
        for(int i = 3; i < args.length; i++)
        {
            if(i == args.length - 1)
                opnd += args[i];
            else opnd += args[i] + " ";
        }
        String fstr = oper + " " + opnd;
        buf = fstr.getBytes();
        address = InetAddress.getByName(sResponse[0]);
        packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(sResponse[1]));
        socket.send(packet);
     
        try{
            // get response
            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
    
            // display response
            received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(fstr + " : " + received);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("\n" + fstr + " : EXCEPTION_ERROR");
        }
     
        socket.close();
    }
}