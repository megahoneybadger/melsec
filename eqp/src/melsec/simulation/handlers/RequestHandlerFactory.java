package melsec.simulation.handlers;

import melsec.commands.multi.MultiBlockBatchReadCommand;
import melsec.commands.multi.MultiBlockBatchWriteCommand;
import melsec.simulation.Memory;
import melsec.simulation.handlers.multi.MultiBlockBatchReadHandler;
import melsec.simulation.handlers.multi.MultiBlockBatchWriteHandler;
import melsec.utils.EndianDataInputStream;

import java.io.ByteArrayInputStream;

public class RequestHandlerFactory {

  //region Class public methods
  /**
   *
   * @param buffer
   * @return
   */
  public static byte[] reply( Memory m, byte [] buffer ){
    try( var bs = new ByteArrayInputStream( buffer )){
      try( var r = new EndianDataInputStream( bs )){
        r.skipBytes( 7 );

        var dataSize = r.readUnsignedShort();
        var iCpuMonitoringTime = r.readUnsignedShort();
        var command = r.readUnsignedShort();

        BaseHandler req = switch( command ){
          case MultiBlockBatchReadCommand.CODE -> new MultiBlockBatchReadHandler( m, r );
          case MultiBlockBatchWriteCommand.CODE -> new MultiBlockBatchWriteHandler( m, r );
          default -> null;
        };

        var buf =req.handle();
        return buf;
      }
    }
    catch( Exception e ){
      int a = 789*8;
      //throw new DecodingException( this, e );
      // new error request handle: todo
    }

    return null;
  }
  //endregion
}
