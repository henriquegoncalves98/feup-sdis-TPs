import java.io.*;
import java.net.*;
import java.util.*;
 
public class Client {
    public static void main(String[] args) throws IOException {
 
        if (args.length < 4) {
             System.err.println("Client arguments must be: <host_name> <port_number> <oper> <opnd>*");
             return;
        }
 
        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
 
        // send request
        byte[] buf = new byte[256];
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
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(args[1]));
        socket.send(packet);
     
        try{
            // get response
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
    
            // display response
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(fstr + " : " + received);
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("\n" + fstr + " : EXCEPTION_ERROR");
        }
     
        socket.close();
    }
}