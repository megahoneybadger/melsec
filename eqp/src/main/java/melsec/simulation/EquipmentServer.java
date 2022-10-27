package melsec.simulation;

import melsec.bindings.IPlcObject;
import melsec.types.exceptions.InvalidRangeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquipmentServer {

  //region Class members
  /**
   *
   */
  private Object syncObject;
  /**
   *
   */
  private ServerOptions options;
  /**
   *
   */
  private boolean run;
  /**
   *
   */
  private AsynchronousServerSocketChannel server;
  /**
   *
   */
  private ArrayList<EquipmentChannel> clients;
  //endregion

  //region Class properties
  /**
   *
   * @return
   */
  Logger logger(){
    return LogManager.getLogger();
  }
  //endregion

  //region Class initialization
  /**
   *
   * @param o
   */
  public EquipmentServer( ServerOptions o ){
    options = o;
    syncObject = new Object();
    clients = new ArrayList<>();
  }
  //endregion

  //region Class public methods
  /**
   *
   */
  public void start() throws IOException {
    synchronized( syncObject ){
      if( run )
        return;

      run = true;

      logger().debug( "Equipment is listening at {}:{}",
        options.address().getHostAddress(), options.port() );

      server = AsynchronousServerSocketChannel
        .open()
        .bind( new InetSocketAddress( options.address(), options.port() ));

      acceptClientAsync();
    }
  }
  /**
   *
   */
  public void stop() throws IOException {
    synchronized( syncObject ){
      if( !run )
        return;

      run = false;

      server.close();

      clients
        .stream()
        .toList()
        .forEach( x -> x.drop( null ));

      clients.clear();
    }
  }
  //endregion

  //region Class 'Listening' methods
  /**
   *
   */
  private void acceptClientAsync(){
    synchronized( syncObject ){
      if( !run )
        return;

      server.accept(null, new CompletionHandler<>() {
        @Override
        public void completed( AsynchronousSocketChannel socket, Object attachment) {
          acceptClientAsync();
          addChannel( socket );
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
          acceptClientAsync();
        }
      });
    }
  }
  /**
   *
   * @param socket
   */
  private void addChannel( AsynchronousSocketChannel socket ){
    synchronized( syncObject ){
      if( !run )
        return;

      if(null != socket && socket.isOpen()) {
        var params = options.toChannelParams(
          socket, args -> removeChannel( args.channel() ) );

        clients.add( new EquipmentChannel( params ) );
      }
    }
  }
  /**
   *
   * @param channel
   */
  private void removeChannel( EquipmentChannel channel ){
    synchronized( syncObject ){
      clients.remove( channel );
    }
  }
  //endregion

  //region Class 'IO' methods
  /**
   *
   * @param o
   */
  public void write( IPlcObject o ) throws InvalidRangeException {
    options.memory().write( o );
  }

  /**
   *
   * @param arr
   * @throws InvalidRangeException
   */
  public void write( IPlcObject [] arr ) throws InvalidRangeException {
    write( List.of( arr ) );
  }
  /**
   *
   * @param list
   */
  public void write( List<IPlcObject> list ) throws InvalidRangeException {
    options.memory().write( list );
  }


  /**
   *
   * @param o
   * @return
   */
  public Object read( IPlcObject o ) throws InvalidRangeException {
    return options.memory().read( o );
  }
  /**
   *
   */
  public void reset(){
    options.memory().reset();
  }
  //endregion
}
