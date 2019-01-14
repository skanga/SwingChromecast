package views;

import beans.ChromeListBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pm.PM;
import su.litvak.chromecast.api.v2.ChromeCast;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class HomeView implements View
{
  private JPanel mainPanel;
  private ChromeListView chromecastsView;
  private DetailView detailView;

  public HomeView (String ipaddr, int port)
  {
    setupGui ();
    setupListeners (ipaddr, port);
  }

  private void setupGui ()
  {
    mainPanel = new JPanel (new BorderLayout (2,2));
    chromecastsView = new ChromeListView ();
    mainPanel.add (chromecastsView.getGui (), BorderLayout.WEST);
    detailView = new DetailView ();
    mainPanel.add (detailView.getGui (), BorderLayout.CENTER);
  }

  private void setupListeners (String ipaddr, int port)
  {
    chromecastsView.getPM ().getBean ().addPropertyChangeListener (ChromeListBean.LIST_SELECTION, new PropertyChangeListener ()
    {
      @Override
      public void propertyChange (PropertyChangeEvent pce)
      {
        if (pce.getNewValue () == null)
        {
          return;
        }
        ChromeCast selectedCast = (ChromeCast) pce.getNewValue ();
        detailView.getPM ().setChromecast (selectedCast);
        detailView.getPM ().setIpaddr (ipaddr);
        detailView.getPM ().setPort (port);
      }
    });
  }

  @NotNull
  @Override
  public JComponent getGui ()
  {
    return mainPanel;
  }

  @Nullable
  @Override
  public PM getPM ()
  {
    return null;
  }
}
