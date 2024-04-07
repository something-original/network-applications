import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.File;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {
        new Server().bootstrap();
    }
}

class Server {
    private final static int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;

    private final static String HEADERS =
        "HTTP/1.1 200 OK\n" +
        "Server: naive\n" +
        "Content-Type: text/html\n" +
        "Content-Length: %s\n" +
        "Connection: close\n\n";

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 8088));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
        System.out.println("new client thread");

        AsynchronousSocketChannel clientChannel = future.get(30, TimeUnit.SECONDS);

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                clientChannel.read(buffer).get();

                int position = buffer.position();
                keepReading = position == BUFFER_SIZE;

                byte[] array = keepReading
                        ? buffer.array()
                        : Arrays.copyOfRange(buffer.array(), 0, position);

                builder.append(new String(array));
                buffer.clear();
            }

            String request = builder.toString();
            String response;

            if (request.contains("GET /hello.html")) {
                File file = new File("hello.html");
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    fis.close();

                    String body = new String(data);
                    String headers = String.format(HEADERS, body.length());
                    response = headers + body;
                } else {
                    response = "HTTP/1.1 404 Not Found\n\n<h1>404 Not Found</h1>";
                }
            } else {
                response = "HTTP/1.1 404 Not Found\n\n<h1>404 Not Found</h1>";
            }

            ByteBuffer resp = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(resp);
            clientChannel.close();
        }
    }
}