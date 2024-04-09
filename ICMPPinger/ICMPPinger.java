package task5;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

public class ICMPPinger {
  private static final int TIMEOUT = 1000;
  private static final int COUNT = 3;
  private static int packageSent = 0;
  private static int packageRev = 0;
  private static ArrayList<Long> timeRTT = new ArrayList<Long>();

  private static void ping(String hostname) {
    try {
      InetAddress address = InetAddress.getByName(hostname);
      System.out.println("ping " + hostname + ", on address: "+ address.getHostAddress());

      for (int i = 0; i < COUNT; i++) {
        long start = System.currentTimeMillis();
        boolean reachable = address.isReachable(TIMEOUT);
        packageSent+=1;
        long end = System.currentTimeMillis();
        if (reachable) {
          packageRev+=1;
          timeRTT.add(end-start);
          System.out.println("RTT: " + (end - start) + "ms");
        } else {
          System.out.println("Package lost");
        }
        if (!timeRTT.isEmpty()) {
          System.out.println("maxRTT: " + Collections.max(timeRTT) + "ms");
          System.out.println("minRTT: " + Collections.min(timeRTT) + "ms");
        } else {
          System.out.println("maxRTT: 0ms");
          System.out.println("minRTT: 0ms");
        }
        if (packageSent>0) {
          System.out.println("package loss Rate: " + (packageSent-packageRev)/packageSent);
        } else {
          System.out.println("no packets sent");
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    ping("www.mail.ru");
  }
}
