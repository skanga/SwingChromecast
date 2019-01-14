package views;

import beans.DetailBean;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import pm.DetailPM;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.MediaStatus;
import su.litvak.chromecast.api.v2.Volume;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class DetailView implements View
{
  private JPanel mainPanel;
  private JButton playPauseButton;
  private JSlider volumeSlider;

  private DetailPM pm = new DetailPM ();

  private JLabel addressLabel = new JLabel ();
  private JLabel titleLabel = new JLabel ();
  private JLabel nameLabel = new JLabel ();
  private JLabel appTitleLabel = new JLabel ();

  private static final String PLAY = "Play";
  private static final String PAUSE = "Pause";
  private static final String QUIT = "Quit";
  private static final String LOAD_FILE = "File";
  private static final String LOAD_URL = "URL";
  //private static final String DEFAULT_MEDIA_PLAYER_APP = "CC1AD845";

  public DetailView ()
  {
    setupGui ();
    setupListeners ();
  }

  private void setupGui ()
  {
    mainPanel = new JPanel (new MigLayout ("wrap"));

    JPanel infoPanel = new JPanel (new MigLayout ("wrap 2"));
    infoPanel.add (new JLabel ("Title: "));
    infoPanel.add (titleLabel);

    infoPanel.add (new JLabel ("Address: "));
    infoPanel.add (addressLabel);

    infoPanel.add (new JLabel ("Name: "));
    infoPanel.add (nameLabel);

    infoPanel.add (new JLabel ("Application: "));
    infoPanel.add (appTitleLabel);

    JPanel controlPanel = createControlPanel ();
    mainPanel.add (infoPanel);
    mainPanel.add (controlPanel);
  }

  private JPanel createControlPanel ()
  {
    JPanel controls = new JPanel ();
    controls.setLayout (new MigLayout ("wrap 2"));
    playPauseButton = new JButton ();
    playPauseButton.addActionListener (actionEvent -> {
      try
      {
        ChromeCast currentChromeCast = getPM ().getCurrentChromeCast ();
        MediaStatus mediaStatus = currentChromeCast.getMediaStatus ();
        MediaStatus.PlayerState playerState = mediaStatus.playerState;
        //System.out.println ("MediaStatus playerState: " + playerState);
        if (playerState == mediaStatus.playerState.PLAYING)
          currentChromeCast.pause ();
        else if (playerState == mediaStatus.playerState.PAUSED)
          currentChromeCast.play ();
      }
      catch (IOException | NullPointerException e)
      {
        e.printStackTrace ();
      }
    });
    JButton stopAppButton = new JButton ();
    stopAppButton.setToolTipText ("Terminates the currently running application");
    stopAppButton.setAction (new TerminateAppAction ());

    JButton loadButton = new JButton (new LoadFileAction ());

    JButton urlButton = new JButton ("URL");
    urlButton.setAction (new LoadURLAction ());

    // add a 'mute' button next to the slider
    volumeSlider = new JSlider (SwingConstants.HORIZONTAL);
    volumeSlider.addMouseListener (new VolumeChangeListener ());

    controls.add (new JLabel ("Volume:"));
    controls.add (volumeSlider);
    controls.add (loadButton);
    controls.add (urlButton);
    controls.add (playPauseButton);
    controls.add (stopAppButton);
    return controls;
  }

  private void setupListeners ()
  {
    pm.getBean ().addPropertyChangeListener (DetailBean.CHROMECAST, propertyChangeEvent -> {
      ChromeCast chromeCast = (ChromeCast) propertyChangeEvent.getNewValue ();
      updateUI (chromeCast);
    });

    pm.getBean ().addPropertyChangeListener (DetailBean.CAST_STATUS, propertyChangeEvent -> updateUI (pm.getBean ().getChromeCast ()));
  }

  private void updateUI (ChromeCast chromeCast)
  {
    try
    {
      Objects.requireNonNull (chromeCast);
      updateChromecastData (chromeCast);
      updateControls (chromeCast);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace ();
    }
  }

  private void updateChromecastData (ChromeCast chromeCast) throws IOException
  {
    titleLabel.setText (chromeCast.getTitle ());
    addressLabel.setText (chromeCast.getAddress ());
    nameLabel.setText (chromeCast.getName ());
    try
    {
      appTitleLabel.setText (chromeCast.getStatus ().getRunningApp ().name);
    }
    catch (NullPointerException e)
    {
      appTitleLabel.setText ("");
    }
  }

  private void updateControls (ChromeCast chromeCast) throws IOException
  {
    if (chromeCast.getRunningApp () == null)
    {
      return;
    }

    boolean idling = chromeCast.getRunningApp ().isIdleScreen;
    if (!idling)
    {
      playPauseButton.setEnabled (true);
      if (chromeCast.getMediaStatus () == null)
      {
        return;
      }
      MediaStatus.PlayerState playerState = chromeCast.getMediaStatus ().playerState;
      if (playerState == MediaStatus.PlayerState.PAUSED)
      {
        playPauseButton.setText (PLAY);
      }
      else if (playerState == MediaStatus.PlayerState.PLAYING)
      {
        playPauseButton.setText (PAUSE);
      }
    }
    else
    {
      // todo: different way to indicate that nothing is running?
      playPauseButton.setEnabled (false);
    }

    // volume slider
    Volume volume = chromeCast.getStatus ().volume;
    int volumeLevel = (int) (volume.level * 100);
    volumeSlider.setValue (volumeLevel);
  }

  @NotNull
  @Override
  public JComponent getGui ()
  {
    return mainPanel;
  }

  @Override
  public DetailPM getPM ()
  {
    return pm;
  }

  /**
   * Terminates the currently running application
   */
  private class TerminateAppAction extends AbstractAction
  {
    TerminateAppAction ()
    {
      putValue (NAME, QUIT);
    }

    @Override
    public void actionPerformed (ActionEvent actionEvent)
    {
      assert getPM () != null;
      ChromeCast currentChromeCast = getPM ().getCurrentChromeCast ();
      if (currentChromeCast == null)
      {
        return;
      }
      try
      {
        currentChromeCast.stopApp ();
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }
    }
  }

  private class VolumeChangeListener extends MouseAdapter
  {
    @Override
    public void mouseReleased (MouseEvent mouseEvent)
    {
      assert getPM () != null;
      ChromeCast chromeCast = getPM ().getCurrentChromeCast ();
      if (chromeCast == null)
      {
        return;
      }
      try
      {
        float volumeLevel = ((float) volumeSlider.getValue ()) / 100;
        chromeCast.setVolume (volumeLevel);
      }
      catch (Exception ex)
      {
        ex.printStackTrace ();
      }
    }
  }

  private class LoadFileAction extends AbstractAction
  {
    LoadFileAction ()
    {
      putValue (NAME, LOAD_FILE);
    }

    @Override
    public void actionPerformed (ActionEvent actionEvent)
    {
      System.out.println ("Choosing local file");
      //File movieFile = Paths.get ("c:\\Users\\skanga.ORADEV\\Downloads\\demo.mp4").toFile ();
      File movieFile = new File ("");
      boolean testing = false;
      if (!testing)
      {
        JFileChooser fileChooser = new JFileChooser ();
        fileChooser.showOpenDialog (null);
        movieFile = fileChooser.getSelectedFile ();
      }
      pm.playMovieFromFile (movieFile);
    }
  }

  private class LoadURLAction extends AbstractAction
  {
    LoadURLAction ()
    {
      putValue (NAME, LOAD_URL);
    }

    @Override
    public void actionPerformed (ActionEvent actionEvent)
    {
      System.out.println ("Choosing remote URL");
      String url = JOptionPane.showInputDialog (new JLabel ("URL"));
      if (url != null && !url.isEmpty ())
      {
        assert getPM () != null;
        ChromeCast chromeCast = getPM ().getCurrentChromeCast ();
        if (chromeCast == null)
          return;

        try
        {
          String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
          // Eg: http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
          chromeCast.connect ();
          chromeCast.launchApp ("CC1AD845");
          chromeCast.load (fileName,"", url,null);
          chromeCast.play ();
        }
        catch (IOException | GeneralSecurityException e)
        {
          e.printStackTrace ();
        }
      }
    }
  }
}
