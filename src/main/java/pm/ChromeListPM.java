package pm;

import beans.ChromeListBean;
import com.jgoodies.binding.beans.Model;

public class ChromeListPM implements PM
{
  private Model bean = new ChromeListBean ();

  @Override
  public Model getBean ()
  {
    return bean;
  }
}
