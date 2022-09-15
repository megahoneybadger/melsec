package melsec.simulation;

import melsec.simulation.handlers.RequestHandlerFactory;
import melsec.utils.Coder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;


public class Channel {

  //region Class members
  private Object syncObject;
  private int port;
  private AsynchronousSocketChannel socket;
  private Memory memory;
  //endregion

  //region Class initialization
  public Channel( Memory m, int port ){
    this.port = port;
    syncObject = new Object();
    memory = m;

  }
  //endregion

  //region Class 'IO' methods
  public void start() throws IOException {
    synchronized( syncObject ){
      var server  = AsynchronousServerSocketChannel
        .open()
        .bind(new InetSocketAddress( port));

      System.out.println( "server is listening" );

      server.accept( null, new CompletionHandler<AsynchronousSocketChannel,Void>() {

        @Override
        public void completed( AsynchronousSocketChannel ch, Void att ){
          recvHeader( ch );
        }

        @Override
        public void failed( Throwable exc, Void att ) {
          System.out.println( "client failed to connect" );
        }
      });
    }
  }

  private void recvHeader(AsynchronousSocketChannel channel){
    try{
      var bufferHeader = ByteBuffer.allocate( Coder.HEADER_LENGTH );

      synchronized( syncObject ) {
        socket = channel;

        channel.read( bufferHeader, null, new CompletionHandler<Integer, Void>() {
          @Override
          public void completed( Integer countReadBytes, Void command ) {
            var requestBodySize = Coder.getCommandBodySize( bufferHeader.array() );
            var requestTotalSize = Coder.HEADER_LENGTH + requestBodySize;

            ByteBuffer bufferTotal = ByteBuffer.allocate( requestTotalSize );

            System.arraycopy( bufferHeader.array(), 0,
              bufferTotal.array(), 0, Coder.HEADER_LENGTH );

            bufferTotal.position( Coder.HEADER_LENGTH );

            recvBody( new BodyRecvProgress( bufferTotal, requestBodySize ) );
          }

          @Override
          public void failed( Throwable e, Void command ) {
            //drop
          }
        } );
      }
    }
    catch( Exception e ){
      
    }
  }
  /**
   *
   * @param p
   */
  private void recvBody( BodyRecvProgress p ){
    try{
      //logger().debug( "Receiving {} body: {} bytes left", p.command, p.bytesToReadLeft );

      synchronized( syncObject ){
        socket.read( p.buffer, 1, TimeUnit.SECONDS, p, new CompletionHandler<>() {
          @Override
          public void completed( Integer readFromStreamCount, BodyRecvProgress pars ) {
            var left = pars.bytesToReadLeft - readFromStreamCount;

            if( left > 0 ) {
              recvBody( new BodyRecvProgress( p.buffer, left ));
            } else {
              decode( p );
            }
          }

          @Override
          public void failed( Throwable e, BodyRecvProgress pars ) {
            //drop( p.command, e );
          }
        });
      }
    }
    catch( Exception e ){
      //drop( p.command, e );
    }
  }
  /**
   *
   * @param p
   */
  private void decode( BodyRecvProgress p ){
    try{
      //logger().debug( "Received {} body", p.command );

      // var reply = RequestHandlerFactory.reply( p.buffer() );
      // handler.reply();
      // handler.encode()

      var reply = RequestHandlerFactory.reply( memory, p.buffer.array() );

      var buffer = ByteBuffer.wrap( reply );

      socket.write( buffer, null, new CompletionHandler<>() {
        @Override
        public void completed( Integer count, Object attachment ) {
          recvHeader( socket );
        }

        @Override
        public void failed( Throwable e, Object attachment ) {

        }
      } );
    }
    catch( Exception exc ){
      //done( p.command, exc );
    }
  }
  //endregion

  //region Class internal structs

  record BodyRecvProgress( ByteBuffer buffer, int bytesToReadLeft ){}
  //endregion
}
