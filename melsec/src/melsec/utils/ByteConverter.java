package melsec.utils;

import melsec.bindings.*;
import melsec.types.BitDeviceCode;
import melsec.types.IDeviceCode;
import melsec.types.WordDeviceCode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

public class ByteConverter {

  //region Class 'To Bytes' methods
  /**
   *
   * @param o
   * @return
   */
  public static byte[] toBytes( IPlcWord o ){
    var val = switch( o.type() ){
      case I2, I4, U2, U4 -> ((IPlcNumber)o ).value();
      case String -> ((PlcString)o).value();
      default -> null;
    };

    Function<Integer, ByteBuffer> getBuffer = (Integer size ) -> ByteBuffer
      .allocate( size )
      .order( ByteOrder.LITTLE_ENDIAN );

    var buffer = switch( o.type() ){
      case I2 -> getBuffer
        .apply( 2 )
        .putShort( ( short )val )
        .array();

      case U2 -> getBuffer
        .apply( 2 )
        .putShort( ( short )( ( ( int )val & 0xFFFF ) )  )
        .array();

      case I4 -> getBuffer
        .apply( 4 )
        .putInt( ( int )val )
        .array();

      case U4 -> getBuffer
        .apply( 4 )
        .putInt( ( int )( ( ( long )val & 0xFFFF_FFFFl ) ) )
        .array();

      case String -> {
        var so = ( PlcString )o;
        int length = so.size();
        var text = ( String )val;

        if( text.length() < length ){
          text = java.lang.String.format( "%-" + length + "." + length + "s", text );
        } else if( text.length() > length ){
          text = text.substring( 0, length );
        }

        var arr = text.getBytes();

        if( arr.length % 2 != 0 ){
          var arrAligned = new byte[ arr.length + 1 ];
          System.arraycopy( arr, 0, arrAligned, 0, arr.length );
          arr = arrAligned;
        }

        yield arr;
      }

      case Struct -> {
        var proto = (PlcStruct) o;
        var items = proto.items();
        var address = proto.address();

        var size = getBytesCount( proto );
        var target = new byte[ size ];
        var targetIndex = 0;

        for(var next : items) {
          var offset = next.address() - address;

          if( offset > 0 ) {
            targetIndex += 2 * offset;
            address += offset;
          }

          var source = toBytes( next );

          System.arraycopy( source, 0, target, targetIndex, source.length );

          address += getBytesCount( next ) / 2;
          targetIndex += source.length;
        }

        yield target;
      }

      default -> null;
    };

    return buffer;
  }
  /**
   *
   * @param v
   * @param bytesNo
   * @return
   */
  public static byte[] toBytes( int v, int bytesNo ){
    var arr = new byte[ bytesNo ];

    for( int i = 0; i < bytesNo; ++i ){
      int iNextByte = v & 255;
      arr[ i ] = ( byte )iNextByte;

      v >>= 8;
    }

    return arr;
  }
  /**
   *
   * @param o
   * @return
   */
  public static int getBytesCount( IPlcWord o ){
    return switch( o.type() ){
      case U2, I2 -> 2;
      case U4, I4 -> 4;
      case String -> {
        var s = ( PlcString ) o;
        var extra = ( s.size() % 2 == 0 ) ? 0 : 1;
        yield ( s.size() + extra );
      }
      case Struct -> {
        var st = ( PlcStruct )o;
        var items = st.items();

        if( 0 == items.size() )
          yield 0;

        var last = items.get( items.size() - 1 );
        var bytes = ( last.address() - st.address()) * 2 + getBytesCount( last );

        yield bytes;
      }
      default -> 0;
    };
  }
  //endregion

  //region Class 'From Bytes' methods
  /**
   *
   * @param arr
   * @param proto
   * @return
   */
  public static Object fromBytes( byte[] arr, IPlcWord proto ){
    var buffer = ByteBuffer
      .wrap( arr )
      .order( ByteOrder.LITTLE_ENDIAN  );

    return switch( proto.type() ){
      case I2 -> buffer.getShort();
      case U2 -> buffer.getShort() & 0xFFFF;
      case I4 -> buffer.getInt();
      case U4 -> buffer.getInt() & 0xFFFF_FFFFl;
      case String -> {
        var s = new String( arr );
        s = s.substring( 0, Math.min( s.length(), (( PlcString ) proto ).size() ));
        yield s;
      }
      default -> null;
    };
  }
  //endregion

  //region Class 'Misc' methods
  /**
   *
   * @param a
   * @param b
   * @return
   */
  public static byte [] concat( byte [] a, byte [] b ){
    a = UtilityHelper.coalesce( a, empty() );
    b = UtilityHelper.coalesce( b, empty() );

    var temp = new byte[ a.length + b.length ];
    System.arraycopy( a, 0, temp, 0, a.length );
    System.arraycopy( b, 0, temp, a.length, b.length );

    return temp;
  }
  /**
   *
   * @return
   */
  public static byte [] empty(){
    return new byte[ 0 ];
  }
  //endregion
}
