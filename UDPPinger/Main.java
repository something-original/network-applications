public class Main {
    public static void main(String[] args) {
        Thread pingerThread = new Thread(() -> {
            UDPPinger pinger = new UDPPinger();
            pinger.run();
        });

        Thread receiverThread = new Thread(() -> {
            UDPReceiver receiver = new UDPReceiver();
            receiver.run();
        });

        pingerThread.start();
        receiverThread.start();
    }
}