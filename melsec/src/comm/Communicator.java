package comm;

public class Communicator {

  private Reader reader;
  private Writer writer;

  public Reader reader(){
    return reader;
  }

  public Writer writer(){
    return writer;
  }

  public Communicator(){
    reader = new Reader();
    writer = new Writer();
  }
}
