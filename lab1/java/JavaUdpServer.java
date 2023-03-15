import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JavaUdpServer {

    public static void main(String args[])
    {
        System.out.println("JAVA UDP SERVER");
        DatagramSocket socket = null;
        int portNumber = 9009;

        try{
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];

            while(true) {
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                byte[] received = receivePacket.getData();
                InetAddress address = receivePacket.getAddress();
                System.out.println("received msg: " + new String(received) + "from " + address.toString());

                Integer value = Integer.parseInt(new String(received)) + 1;
                byte[] sendBuffer = ByteBuffer.allocate(4).putInt(value).array();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, receivePacket.getPort());
                socket.send(sendPacket);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
