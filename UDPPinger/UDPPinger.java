import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPPinger {
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                DatagramSocket socket = new DatagramSocket();
                String data = String.format("Request %d", i);
                System.out.println("Request number " + i + " sent to client");
                byte[] sendData = data.getBytes();
                InetAddress address = InetAddress.getByName("localhost");
                int port = 8081; // Receiver port

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                socket.send(sendPacket);

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(response);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
