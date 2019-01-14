package views;

import beans.ChromeListBean;
import com.jgoodies.binding.adapter.Bindings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pm.ChromeListPM;
import pm.PM;
import su.litvak.chromecast.api.v2.ChromeCast;
import views.components.ChromecastCellRenderer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.List;

public class ChromeListView implements View
{
  private JPanel mainPanel;
  private JList <ChromeCast> chromeCastList;
  private PM chromeListPM = new ChromeListPM ();

  ChromeListView ()
  {
    setupGui ();
    setupListeners ();
  }

  private void setupGui ()
  {
    mainPanel = new JPanel ();
    mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    chromeCastList = new JList <> ();
    chromeCastList.setCellRenderer (new ChromecastCellRenderer ());
    mainPanel.add (chromeCastList);
  }

  private void setupListeners ()
  {
    assert getPM () != null;
    Bindings.bind (chromeCastList, ((ChromeListBean) getPM ().getBean ()).getChromecastList ());
  }

  private void populateJList (List <ChromeCast> chromecasts)
  {
    System.out.println (String.format ("Found %d chromecasts", chromecasts.size ()));
    final DefaultListModel <ChromeCast> listModel = new DefaultListModel <> ();
    for (ChromeCast c : chromecasts)
    {
      listModel.addElement (c);
    }
    chromeCastList.setModel (listModel);
    mainPanel.revalidate ();
  }

  @Override
  @NotNull
  public JComponent getGui ()
  {
    return mainPanel;
  }

  @Nullable
  @Override
  public PM getPM ()
  {
    return chromeListPM;
  }
}
