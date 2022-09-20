package melsec.simulation.handlers;

import melsec.commands.multi.MultiBlockBatchReadCommand;
import melsec.commands.multi.MultiBlockBatchWriteCommand;
import melsec.exceptions.InvalidRangeException;
import melsec.simulation.Memory;
import melsec.simulation.handlers.multi.MultiBlockBatchReadHandler;
import melsec.simulation.handlers.multi.MultiBlockBatchWriteHandler;
import melsec.types.CompletionCode;
import melsec.utils.EndianDataInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RequestHandlerFactory {

  //region Class public methods
  /**
   *
   * @param buffer
   * @return
   */
  public static byte[] reply(Memory m, ByteBuffer buffer ){
    var completionCode = CompletionCode.InternalError;

    try( var bs = new ByteArrayInputStream( buffer.array() )){
      try( var r = new EndianDataInputStream( bs )){
        r.skipBytes( 7 );

        var dataSize = r.readUnsignedShort();
        var iCpuMonitoringTime = r.readUnsignedShort();
        var command = r.readUnsignedShort();

        BaseHandler req = switch( command ) {
          case MultiBlockBatchReadCommand.CODE -> new MultiBlockBatchReadHandler( m, r );
          case MultiBlockBatchWriteCommand.CODE -> new MultiBlockBatchWriteHandler( m, r );
          default -> null;
        };

        return req.handle();
      }
    }
    catch( IOException e ){
      completionCode = CompletionCode.DecodingError;
    }
    catch( InvalidRangeException e ){
      completionCode = CompletionCode.InvalidRange;
    }
    catch( Exception e ) {
      completionCode = CompletionCode.InternalError;
    }

    return new ErrorHandler( completionCode ).handle();
  }
  //endregion
}
