package task4;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ProxyServer {
  public static void main(String[] args) {
    int tcpSerPort = 8888;
    ServerSocket tcpSerSock = null;
    Socket tcpCliSock = null;
    String hostn = "";
    try {
      tcpSerSock = new ServerSocket(tcpSerPort);
      System.out.println("Ready to serve...");
      while (true) {
        tcpCliSock = tcpSerSock.accept();
        InetAddress clientAddress = tcpCliSock.getInetAddress();
        System.out.println("Received a connection from: " + clientAddress.getHostAddress());
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(tcpCliSock.getInputStream()));
        String message = inFromClient.readLine();
        boolean nextHost = false;
        String filename;
        if (message.contains("www.")) {
          nextHost = true;
          hostn = message.split(" ")[1].split("/")[1];
          filename = "/";
        } else {
          filename = message.split(" ")[1];
        }
        System.out.println("Message: " + message);
        System.out.println("Filename: " + filename);
        String fileExist = "false";
        String fileToUse;
        if (nextHost) {
          fileToUse = hostn;
        } else  {
          fileToUse = filename;
        }
        try {
          DataOutputStream outToClient = new DataOutputStream(tcpCliSock.getOutputStream());

          BufferedReader fileReader = new BufferedReader(new FileReader(fileToUse.substring(1)));
          String line;
          fileExist = "true";
          System.out.println("File Exists!");
          while ((line = fileReader.readLine()) != null) {
            outToClient.writeBytes(line + '\n');
          }
          System.out.println("Read from cache");
          fileReader.close();
        } catch (IOException e) {
          System.out.println("File Exists: " + fileExist);
          if (fileExist.equals("false")) {
            System.out.println("Creating socket on proxyserver");
            Socket c;
            System.out.println("Host Name: " + hostn);
            try {
              c = new Socket(hostn, 80);
              System.out.println("Socket connected to port 80 of the host");
              PrintWriter outToServer = new PrintWriter(c.getOutputStream());
              outToServer.println("GET "+ filename +" HTTP/1.0");
              outToServer.println();
              outToServer.flush();
              if (filename.contains(".jpg") || filename.contains(".png") || filename.contains(".jpeg") || filename.contains(".gif")) {
                DataOutputStream outToClient = new DataOutputStream(tcpCliSock.getOutputStream());
                InputStream inFromServer = c.getInputStream();

                // Creating temporary file
                File tmpFile = createFileAndDirectories("./" + fileToUse);
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);

                int count;
                byte[] buffer = new byte[1024];
                while ((count = inFromServer.read(buffer)) !=-1) {
                  outToClient.write(buffer, 0, count);
                  outToClient.flush();
                  fileOutputStream.write(buffer, 0, count);
                }
                outToClient.flush();
                outToServer.close();
                inFromServer.close();
                c.close();
              } else {
                PrintWriter outToClient = new PrintWriter(tcpCliSock.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(c.getInputStream()));

                // Creating temporary file
                File tmpFile = createFileAndDirectories("./" + fileToUse);
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);

                String inputLine;
                while ((inputLine = inFromServer.readLine()) != null) {
                  outToClient.println(inputLine);
                  outToClient.flush();
                  fileOutputStream.write((inputLine+ '\n').getBytes());
                }
                outToServer.close();
                inFromServer.close();
                c.close();
              }
            } catch (IOException ex) {
              System.out.println("Illegal request");
              ex.printStackTrace();
            }
          } else {
            DataOutputStream outToClient = new DataOutputStream(tcpCliSock.getOutputStream());
            outToClient.writeBytes("HTTP/1.0 404 Not Found\r\n");
            outToClient.writeBytes("Content-Type:text/html\r\n");
          }
        }
        tcpCliSock.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (tcpCliSock != null)
          tcpCliSock.close();
        if (tcpSerSock != null)
          tcpSerSock.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static File createFileAndDirectories(String filePath) throws IOException {
    File file = new File(filePath);
    File parentDir = file.getParentFile(); // Получаем родительскую папку файла

    // Проверяем, существует ли родительская папка файла
    if (parentDir != null && !parentDir.exists()) {
      // Создаем родительскую папку файла и все необходимые родительские папки
      boolean success = parentDir.mkdirs();
      if (!success) {
        throw new IOException("Failed to create directories: " + parentDir.getAbsolutePath());
      }
    }

    // Создаем файл, если он не существует
    if (!file.exists()) {
      boolean success = file.createNewFile();
      if (!success) {
        throw new IOException("Failed to create file: " + file.getAbsolutePath());
      }
    }
    return file;
  }
}
