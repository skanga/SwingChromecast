package pm;

import beans.DetailBean;
import domain.ChromeCastApp;
import server.MediaServer;
import su.litvak.chromecast.api.v2.Application;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.Status;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class DetailPM implements PM
{
  private String ipaddr = "";
  private int port = 0;

  private DetailBean bean = new DetailBean ();

  public void setIpaddr (String ipaddr) { this.ipaddr = ipaddr; }

  public void setPort (int port) { this.port = port; }

  public void setChromecast (ChromeCast cast)
  {
    bean.setChromeCast (cast);
  }

  public ChromeCast getCurrentChromeCast ()
  {
    return bean.getChromeCast ();
  }

  @Override
  public DetailBean getBean ()
  {
    return bean;
  }

  public void playMovieFromFile (File movieFile)
  {
    MediaServer mediaServer = MediaServer.getMediaServer (ipaddr, port);
    mediaServer.setMovieFile (movieFile);
    // play this on the chromecast..
    ChromeCast chromeCast = getCurrentChromeCast ();
    if (chromeCast == null)
    {
      return;
    }
    try
    {
      chromeCast.connect ();
      Status status = chromeCast.getStatus();
      if (chromeCast.isAppAvailable (ChromeCastApp.DEFAULT_MEDIA_PLAYER.getApp ())
          && !status.isAppRunning (ChromeCastApp.DEFAULT_MEDIA_PLAYER.getApp ()))
      {
        Application app = chromeCast.launchApp (ChromeCastApp.DEFAULT_MEDIA_PLAYER.getApp ());
      }
      //chromeCast.launchApp (ChromeCastApp.DEFAULT_MEDIA_PLAYER.getApp ());
      chromeCast.load (movieFile.getName (),           // Media title
          "",  // URL to thumbnail based on media URL
          "http://" + ipaddr + ":" + port + "/moviefile.mp4",
          null // media content type (optional, will be discovered automatically)
      );
      System.out.println ("Loaded video file: " + movieFile.getAbsolutePath () + " at: http://" + ipaddr + ":" + port + "/moviefile.mp4");
      chromeCast.play ();
    }
    catch (IOException | GeneralSecurityException e)
    {
      e.printStackTrace ();
    }
  }
}
