package melsec.simulation;

import melsec.simulation.events.ChannelEventArgs;
import melsec.simulation.events.IClientDisconnectedEvent;
import melsec.simulation.handlers.RequestHandlerFactory;
import melsec.utils.Coder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class EquipmentChannel {

  //region Class members
  /**
   *
   */
  private String id;
  /**
   *
    */
  private AsynchronousSocketChannel socket;
  /**
   *
   */
  private Memory memory;
  /**
   *
   */
  private IClientDisconnectedEvent handlerDisconnected;
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
   * @param p
   */
  public EquipmentChannel( ChannelParams p ){
    memory = p.memory();
    socket = p.socket();
    handlerDisconnected = p.handler();

    id = Integer
      .valueOf( new SecureRandom().nextInt( 1000 ))
      .toString();

    logger().debug( "Client#{} connected", id );

    recvHeaderAsync();
  }
  //endregion

  //region Class 'IO' methods
  /**
   *
   */
  private void recvHeaderAsync(){
    var header = ByteBuffer.allocate( Coder.HEADER_LENGTH );

    socket.read( header, null, new CompletionHandler<Integer, Void>() {
      @Override
      public void completed( Integer countReadBytes, Void command ) {
        var requestBodySize = Coder.getCommandBodySize( header.array() );
        var requestTotalSize = Coder.HEADER_LENGTH + requestBodySize;

        ByteBuffer bufferTotal = ByteBuffer.allocate( requestTotalSize );

        System.arraycopy( header.array(), 0,
          bufferTotal.array(), 0, Coder.HEADER_LENGTH );

        bufferTotal.position( Coder.HEADER_LENGTH );

        recvBodyAsync( new BodyRecvProgress( bufferTotal, requestBodySize ) );
      }

      @Override
      public void failed( Throwable e, Void command ) {
        drop( e );
      }
    } );
  }
  /**
   *
   * @param p
   */
  private void recvBodyAsync( BodyRecvProgress p ){
    socket.read( p.buffer, 1, TimeUnit.SECONDS, p, new CompletionHandler<>() {
      @Override
      public void completed( Integer readFromStreamCount, BodyRecvProgress pars ) {
        var left = pars.bytesToReadLeft - readFromStreamCount;

        if( left > 0 ) {
          recvBodyAsync( new BodyRecvProgress( p.buffer, left ));
        } else {
          decode( p );
        }
      }

      @Override
      public void failed( Throwable e, BodyRecvProgress pars ) {
        drop( e );
      }
    });
  }
  /**
   *
   * @param p
   */
  private void decode( BodyRecvProgress p ){
    try{
      var reply = RequestHandlerFactory.reply( memory, p.buffer );

      var buffer = ByteBuffer.wrap( reply );

      socket.write( buffer, null, new CompletionHandler<>() {
        @Override
        public void completed( Integer count, Object attachment ) {
          recvHeaderAsync();
        }

        @Override
        public void failed( Throwable e, Object attachment ) {
          drop( e );
        }
      } );
    }
    catch( Exception e ){
      drop( e );
    }
  }
  /**
   *
   * @param e
   */
  public void drop( Throwable e ){
    if( null == socket )
      return;

    try {
      socket.close();
      socket = null;
    } catch( IOException ex) {
      //throw new RuntimeException(ex);
    }

    if( null != handlerDisconnected){
      handlerDisconnected.executed( new ChannelEventArgs( this ) );
    }

    logger().debug( "Client#{} disconnected", id );
  }
  //endregion

  //region Class internal structs
  /**
   *
   * @param buffer
   * @param bytesToReadLeft
   */
  record BodyRecvProgress( ByteBuffer buffer, int bytesToReadLeft ){}
  //endregion
}
