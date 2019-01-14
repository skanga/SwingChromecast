package views.components;

import net.miginfocom.swing.MigLayout;
import su.litvak.chromecast.api.v2.ChromeCast;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class ChromecastCellRenderer extends JPanel implements ListCellRenderer <ChromeCast>
{
  private static Color BABY_BLUE = new Color (137, 207, 240);

  @Override
  public Component getListCellRendererComponent (JList <? extends ChromeCast> list, ChromeCast chromeCast, int index, boolean isSelected, boolean cellFocussed)
  {
    Color unselectedColour = Color.WHITE;
    Color selectedColor = BABY_BLUE;

    this.setLayout (new MigLayout ("debug"));

    if (isSelected)
    {
      this.setBackground (selectedColor);
    }
    else
    {
      this.setBackground (unselectedColour);
    }

    JLabel nameLabel = new JLabel (chromeCast.getTitle ());
    this.add (nameLabel);

    return this;
  }
}
