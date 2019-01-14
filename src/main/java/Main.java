import server.MediaServer;
import views.ChromeListView;
import views.HomeView;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

// https://github.com/DylanMeeus/SwingChromecast
// https://github.com/vitalidze/chromecast-java-api-v2
public class Main
{
  // KangaTV ("192.168.0.213")
  public static void main (String[] args)
  {
    // Change port if requested
    int port = 8888;
    if (args.length > 0)
      port = Integer.parseInt (args [0]);

    String name = "localhost";
    String ipaddr = "127.0.0.1";
    try
    {
      name = InetAddress.getLocalHost ().getHostName ();
      ipaddr = InetAddress.getLocalHost ().getHostAddress ();
    }
    catch (UnknownHostException e)
    {
    }

    System.out.println ("Ready at the following URLs:");
    System.out.println ("http://" + name + ":" + port + "/");
    System.out.println ("http://" + ipaddr + ":" + port + "/");

    //System.setProperty ("org.slf4j.simpleLogger.logFile", "System.out");
    MediaServer s = MediaServer.getMediaServer (ipaddr, port);
    System.out.println ("Server set up!");
    JFrame frame = new JFrame ();
    frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    frame.setSize (new Dimension (800, 800));
    frame.setContentPane (new HomeView (ipaddr, port).getGui ());
    frame.setVisible (true);
  }
}
