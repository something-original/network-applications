import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;


public class UDPReceiver {
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                DatagramSocket socket = new DatagramSocket(8081); // Receiver port
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                LocalDateTime currentDateTime = LocalDateTime.now();
                String answer = "Ping " + i + " " + currentDateTime.toString();
                byte[] sendData = answer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
