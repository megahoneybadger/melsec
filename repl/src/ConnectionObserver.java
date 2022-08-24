import melsec.net.events.ConnectionEventArgs;
import melsec.net.events.IConnectionEventListener;

import java.text.MessageFormat;

public class ConnectionObserver implements IConnectionEventListener {

  public void initialized( ConnectionEventArgs args ){
    System.out.println( MessageFormat.format(
      "connection {0} initialized", args.toString() ));
  };

  public  void connecting( ConnectionEventArgs args ){
    System.out.println( MessageFormat.format(
      "connection [{0}] try connecting to {1}...", args.id(), args.toString() ));
  };

  public void established( ConnectionEventArgs args ){
    System.out.println( MessageFormat.format(
      "connection {0} established", args.toString() ));
  };

  public  void dropped( ConnectionEventArgs args ){
    System.out.println( MessageFormat.format(
      "connection {0} dropped", args.toString() ));
  };

  public  void disposed( ConnectionEventArgs args ){
    System.out.println( MessageFormat.format(
      "connection {0} disposed", args.toString() ));
  };
}
