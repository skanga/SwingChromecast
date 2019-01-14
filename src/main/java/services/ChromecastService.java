package services;

import su.litvak.chromecast.api.v2.ChromeCast;

import java.util.List;

public interface ChromecastService
{
  public List <ChromeCast> getChromecasts ();
  public void startDiscovery ();
  public void restartDiscovery ();
  public void stopDiscovery ();
}
