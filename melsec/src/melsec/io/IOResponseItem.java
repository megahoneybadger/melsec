package melsec.io;

import melsec.bindings.IPlcObject;
import melsec.bindings.PlcObjectPrinter;

import java.text.MessageFormat;

public record IOResponseItem( IOType type, IPlcObject target, IOResult result ) {

  public String toString(){


    String resPrefix = result.success() ? "OK" : "NG";

    if( type == IOType.Read ){

      return ( result.success() ) ?

        MessageFormat.format( "Read [{0}] {1}", resPrefix,
          PlcObjectPrinter.toString( result.value() ) ) :

        MessageFormat.format( "Read [{0}] {1} -> {2}", resPrefix,
          PlcObjectPrinter.toString( target, false ), result.error().getMessage() );



//      Read [OK] bit [Bh@0x64 RecvGlassRequestBit1] 1
//      Read [OK] bit [Bh@0xc8 RecvGlassRequestBit2] 1
//      Read [OK] U2 [Wh@0x12c GlassId] 58624
//      Read [NG] bit [Bh@0x64 RecvGlassRequestBit1] -> Failed to encode mbbr#354 [1w|2b]. suck my dick
//      Read [NG] bit [Bh@0xc8 RecvGlassRequestBit2] -> Failed to encode mbbr#354 [1w|2b]. suck my dick
//      Read [NG] U2 [Wh@0x12c GlassId] -> Failed to encode mbbr#354 [1w|2b]. suck my dick

//      Write [NG] bit [Bh@0x191 RecvGlassReplyBit] 1 -> Failed to encode mbbw#42 [1b]. suck my dick


    } else {
      return ( result.success() ) ?

        MessageFormat.format( "Write [{0}] {1}", resPrefix,
          PlcObjectPrinter.toString( result.value() ) ) :

        MessageFormat.format( "Write [{0}] {1} -> {2}", resPrefix,
          PlcObjectPrinter.toString( target ), result.error().getMessage() );
    }
  }
}
