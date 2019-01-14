package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;

/**
 * Media server from which we can stream videos to the chromecast (or other sources?)
 */
public class MediaServer
{
  private static File movieFile;
  private static MediaServer SERVER;

  private MediaServer (String ipaddr, int port)
  {
    try
    {
      setupServer (ipaddr, port);
    }
    catch (Exception ex)
    {
      ex.printStackTrace ();
    }
  }

  public static MediaServer getMediaServer (String ipaddr, int port)
  {
    SERVER = new MediaServer (ipaddr, port);
    return SERVER;
  }

  public void setupServer (String ipaddr, int port) throws IOException
  {
    HttpServer httpServer = HttpServer.create (new InetSocketAddress (ipaddr, port), 8000);
    httpServer.createContext ("/moviefile", new MovieHandler ());
    httpServer.setExecutor (null);
    httpServer.start ();
  }

  public void setMovieFile (File file)
  {
    movieFile = file;
  }

  static class MovieHandler implements HttpHandler
  {
    @Override
    public void handle (HttpExchange httpExchange) throws IOException
    {
      //URI uri = httpExchange.getRequestURI();
      //String query = uri.getQuery();
      //if (query != null)
      System.out.println ("Sending video of " + movieFile.length () + " bytes");
      httpExchange.sendResponseHeaders (200, movieFile.length ());
      httpExchange.getResponseHeaders ().put ("Content-Type", Arrays.asList (new String[] {"video/mp4"}));
      OutputStream os = httpExchange.getResponseBody ();
      byte[] buffer = new byte[1024];
      FileInputStream fs = new FileInputStream (movieFile);
      int count = 0;
      while ((count = fs.read (buffer)) >= 0)
      {
        os.write (buffer, 0, count);
      }
      os.flush ();
      fs.close ();
      os.close ();
    }
  }
}
