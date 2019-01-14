package beans;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.list.SelectionInList;
import su.litvak.chromecast.api.v2.ChromeCast;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static services.DefaultChromecastService.getChromecastService;

public class ChromeListBean extends Model
{
  private SelectionInList chromeCastList = new SelectionInList ();
  private Timer timer;
  private ChromeCast selectedChromeCast;
  public static final String LIST_SELECTION = "chromeListSelection";

  public ChromeListBean ()
  {
    TimerTask task = new TimerTask ()
    {
      @Override
      public void run ()
      {
        List <ChromeCast> chromecasts = getChromecastService ().getChromecasts ();
        // once we have found some chromecasts, stop reloading and let the user handle refreshes
        if (!chromecasts.isEmpty ())
        {
          System.out.println ("Discovered " + chromecasts.size () + " chromecast(s)");
          dumpChromecastList (chromecasts);
          timer.cancel ();
          chromeCastList.setList (chromecasts);
        }
      }
    };
    timer = new Timer ("serviceTimer");
    timer.scheduleAtFixedRate (task, 0, 5000);

    chromeCastList.addPropertyChangeListener (new PropertyChangeListener ()
    {
      @Override
      public void propertyChange (PropertyChangeEvent propertyChangeEvent)
      {
        ChromeCast oldChromeCast = selectedChromeCast;
        selectedChromeCast = (ChromeCast) chromeCastList.getSelection ();
        firePropertyChange (LIST_SELECTION, oldChromeCast, selectedChromeCast);
      }
    });
  }

  public SelectionInList getChromecastList ()
  {
    return chromeCastList;
  }

  private void dumpChromecastList (List <ChromeCast> chromecasts)
  {
    for (ChromeCast chromeCast : chromecasts)
    {
      System.out.println ("ChromeCast: " + chromeCast.getTitle () + " with name: " + chromeCast.getName () + " at " + chromeCast.getAddress () + ":" + chromeCast.getPort () + " with application: " + chromeCast.getApplication ());
    }
  }
}
